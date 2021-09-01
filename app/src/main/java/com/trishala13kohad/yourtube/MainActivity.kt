package com.trishala13kohad.yourtube

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest

class MainActivity : AppCompatActivity(), VideoClicked {

    //Video adapter to update views of video details
    private lateinit var mAdapter: VideoAdapter
    private lateinit var pb: ProgressBar

    //next page button
    private lateinit var next: ImageView

    //previous page button
    private lateinit var previous: ImageView
    private lateinit var recyclerView: RecyclerView

    //textView and imageView when no data is found
    private lateinit var foundNothing: TextView
    private lateinit var nothingFoundImage: ImageView

    //keyword to search for
    private lateinit var keyword: TextView

    //to get the next 50 pages
    private lateinit var pageToken: String
    private lateinit var pageNo: TextView

    //to save unit numbers
    private var pointer = 0
    private var point = 0
    private var pg = 1

    //to find when the next and previous button is clicked for the first time or not
    private var nextClick = false
    private var prevClick = false

    //api key to be used
    private val apiKey = BuildConfig.YOU_KEY

    //Arraylist to store the details of the video
    private val videosArray = ArrayList<VideoDetails>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previous = findViewById(R.id.previous)
        next = findViewById(R.id.next)
        pb = findViewById(R.id.spinner)
        pageNo = findViewById(R.id.pageNo)

        //Setting recycler view layout
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        foundNothing = findViewById(R.id.found_nothing)
        nothingFoundImage = findViewById(R.id.found_nothing_image)

        keyword = findViewById<EditText>(R.id.inputKeyword)

        val search = findViewById<ImageView>(R.id.searchIcon)

        //searching when the last button that is imeOptions button is clicked
        keyword.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (keyword.text.toString() != "" && keyword.text.toString().trim() != "") {
                    pointer = 0
                    val imm: InputMethodManager = getSystemService(
                        Context.INPUT_METHOD_SERVICE
                    ) as InputMethodManager
                    imm.hideSoftInputFromWindow(search.windowToken, 0)
                    searchFromKeyword(keyword.text.toString())
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        //Setting onClickListener for e item in the recycler view
        search.setOnClickListener {
            if (keyword.text.toString() != "" && keyword.text.toString().trim() != "") {
                pointer = 0
                val imm: InputMethodManager = getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager
                imm.hideSoftInputFromWindow(search.windowToken, 0)
                searchFromKeyword(keyword.text.toString())
            }
        }

        //Going to previous page when clicked previous button
        previous.setOnClickListener {
            println("$point space $pointer")
            if (point >= 10) {
                prevClick = true
                point -= 10
                pg -= 1
                pageNo.text = pg.toString()
                if (keyword.text.toString() != "" && keyword.text.toString().trim() != "") {
                    searchByKeyword(keyword.text.toString())
                }
            } else if (pointer >= 10 && !prevClick) {
                pointer -= 10
                pg -= 1
                pageNo.text = pg.toString()
                if (keyword.text.toString() != "" && keyword.text.toString().trim() != "") {
                    searchFromKeyword(keyword.text.toString())
                }
            } else if (pointer == 40) {
                prevClick = false
                pg -= 1
                pageNo.text = pg.toString()
                if (keyword.text.toString() != "" && keyword.text.toString().trim() != "") {
                    searchFromKeyword(keyword.text.toString())
                }

            } else {
                Toast.makeText(
                    this, "Click on > to go to next page",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //going to next page ehen clicked next button
        next.setOnClickListener {
            if (pointer <= 39) {
                nextClick = true
                pointer += 10
                pg += 1
                pageNo.text = pg.toString()
                if (keyword.text.toString() != "" && keyword.text.toString().trim() != "") {
                    searchFromKeyword(keyword.text.toString())
                }
            } else if (point == 0 && nextClick) {
                nextClick = false
                pg += 1
                pageNo.text = pg.toString()
                if (keyword.text.toString() != "" && keyword.text.toString().trim() != "") {
                    searchByKeyword(keyword.text.toString())
                }
            } else if (point <= 39) {
                point += 10
                pg += 1
                pageNo.text = pg.toString()
                if (keyword.text.toString() != "" && keyword.text.toString().trim() != "") {
                    searchByKeyword(keyword.text.toString())
                }
            } else {
                Toast.makeText(
                    this, "Looks like you've reached the end",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //Setting custom adapter with the recycler view
        mAdapter = VideoAdapter(this)
        recyclerView.adapter = mAdapter
    }


    //function called for first five pages as we can get only 50 response at a time
    @SuppressLint("SetTextI18n")
    private fun searchFromKeyword(word: String) {

        //Showing progress bas as the search begins
        pb.visibility = View.VISIBLE
        foundNothing.visibility = View.GONE
        nothingFoundImage.visibility = View.GONE
        recyclerView.scrollToPosition(0)

        //Youtube url for searching list
        val url = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&type=videos&" +
                "maxResults=100&q=$word&key=$apiKey"

        //Json object request for the title, thumbnail etc. of the video
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url, null,
            {
                try {
                    val videoJsonArray = it.getJSONArray("items")
                    pageToken = it.getString("nextPageToken")
                    videosArray.clear()
                    for (i in pointer until pointer + 10) {
                        val videosJsonObject = videoJsonArray.getJSONObject(i)
                        val id: String
                        try {
                            id = videosJsonObject.getJSONObject("id").getString("videoId")
                        } catch (e: Exception) {
                            e.printStackTrace()
                            continue
                        }

                        //URL to get likes and views of a particular video
                        likesAndViews(id)

                        //object to get the extra details of the video
                        val snippet = videosJsonObject.getJSONObject("snippet")

                        //getting channel name
                        val channel = snippet.getString("channelTitle")

                        //getting date when the video got published
                        val date = snippet.getString("publishedAt").substring(0, 10)

                        //getting title of the video
                        val title = snippet.getString("title")

                        //getting description of the video
                        val description = snippet.getString("description")

                        //getting thumbnail url of the video
                        val thumbnails = snippet.getJSONObject("thumbnails")
                        val resolve = thumbnails.getJSONObject("high")
                        val thumbnailUrl = resolve.getString("url")

                        //Adding video details the the arraylist
                        val videos =
                            VideoDetails(title, thumbnailUrl, date, description, channel, id)
                        videosArray.add(videos)

                        recyclerView.visibility = View.VISIBLE

                    }
                    //updating the details to the UI
                    mAdapter.updateVideo(videosArray)
                    next.visibility = View.VISIBLE
                    previous.visibility = View.VISIBLE
                    pageNo.visibility = View.VISIBLE

                    //making progressbar go away
                    pb.visibility = View.GONE
                } catch (e: Exception) {

                    videosArray.clear()
                    next.visibility = View.GONE
                    previous.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    foundNothing.visibility = View.VISIBLE
                    nothingFoundImage.visibility = View.VISIBLE
                    pb.visibility = View.GONE
                    pageNo.visibility = View.GONE

                }
            },
            {})
        //Adding jsonObjectRequest too the queue
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    //function to call next 50 pages
    @SuppressLint("SetTextI18n")
    private fun searchByKeyword(word: String) {

        //Showing progress bas as the search begins
        pb.visibility = View.VISIBLE
        foundNothing.visibility = View.GONE
        nothingFoundImage.visibility = View.GONE
        recyclerView.scrollToPosition(0)

        //Youtube url for searching list
        val url = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&type=videos&" +
                "maxResults=100&q=$word&key=$apiKey&pageToken=$pageToken"

        //Json object request for the title, thumbnail etc. of the video
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url, null,
            {
                try {
                    val videoJsonArray = it.getJSONArray("items")

                    videosArray.clear()
                    for (i in point until point + 10) {
                        val videosJsonObject = videoJsonArray.getJSONObject(i)
                        val id: String
                        try {
                            id = videosJsonObject.getJSONObject("id")
                                .getString("videoId")
                        } catch (e: Exception) {
                            e.printStackTrace()
                            continue
                        }

                        //URL to get likes and views of a particular video
                        likesAndViews(id)

                        //object to get the extra details of the video
                        val snippet = videosJsonObject.getJSONObject("snippet")

                        //getting channel name
                        val channel = snippet.getString("channelTitle")

                        //getting date when the video got published
                        val date = snippet.getString("publishedAt").substring(0, 10)

                        //getting title of the video
                        val title = snippet.getString("title")

                        //getting description of the video
                        val description = snippet.getString("description")

                        //getting thumbnail url of the video
                        val thumbnails = snippet.getJSONObject("thumbnails")
                        val resolve = thumbnails.getJSONObject("high")
                        val thumbnailUrl = resolve.getString("url")

                        //Adding video details the the arraylist
                        val videos =
                            VideoDetails(title, thumbnailUrl, date, description, channel, id)
                        videosArray.add(videos)

                        recyclerView.visibility = View.VISIBLE

                    }
                    //updating the details to the UI
                    mAdapter.updateVideo(videosArray)
                    next.visibility = View.VISIBLE
                    previous.visibility = View.VISIBLE
                    pageNo.visibility = View.VISIBLE

                    //making progressbar go away
                    pb.visibility = View.GONE
                } catch (e: Exception) {

                    videosArray.clear()
                    next.visibility = View.GONE
                    previous.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    foundNothing.visibility = View.VISIBLE
                    nothingFoundImage.visibility = View.VISIBLE
                    pageNo.visibility = View.GONE
                    pb.visibility = View.GONE


                }
            },
            {})
        //Adding jsonObjectRequest too the queue
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    //handling the back button click
    //not to jump out of the app instantly from any page
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK && pointer >= 10) {
            pointer -= 10
            pg -= 1
            pageNo.text = pg.toString()
            searchFromKeyword(keyword.text.toString())
            true
        } else if (keyCode == KeyEvent.KEYCODE_BACK && point >= 10) {
            point -= 10
            pg -= 1
            pageNo.text = pg.toString()
            searchByKeyword(keyword.text.toString())
            true
        } else
            super.onKeyDown(keyCode, event)
    }

    /*getting response from the url using videoId in order to get
     the views and likes of the particular video*/
    private fun likesAndViews(id: String) {
        val urli =
            "https://youtube.googleapis.com/youtube/v3/videos?part=statistics&" +
                    "id=$id&key=$apiKey"
        val jsonObjectRequest1 = JsonObjectRequest(
            Request.Method.GET,
            urli, null,
            {
                val videoJsonArray1 = it.getJSONArray("items")

                //Arraylist to store likes and views of the particular video
                val lVArray = ArrayList<LikesViews>()
                val videosJsonObject1 = videoJsonArray1.getJSONObject(0)

                val stats = videosJsonObject1.getJSONObject("statistics")
                val likes: String = try {
                    stats.getString("likeCount")
                } catch (e: Exception) {
                    "-"
                }
                val views: String = try {
                    stats.getString("viewCount")
                } catch (e: Exception) {
                    "-"
                }

                //Adding to the array containing likes and views
                lVArray.add(LikesViews(likes, views))

                //Updating to theUI
                mAdapter.updateVide(lVArray)
            },
            {
                Log.e("Error", "something went wrong")
            })
        //Adding request to Singleton class
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest1)
    }

    //Called when an item in the recycler view is clicked
    override fun onItemClicked(item: VideoDetails, itemLV: LikesViews) {

        val i = Intent(this, VideoActivity::class.java)

        //Putting extras to display in the result activity
        i.putExtra("ids", item.ids)
        i.putExtra("description", item.description)
        i.putExtra("date", item.date)
        i.putExtra("channel", item.channelName)
        i.putExtra("title", item.title)
        i.putExtra("likes", itemLV.likes)
        i.putExtra("views", itemLV.views)

        //Starting the activity
        startActivity(i)
    }
}