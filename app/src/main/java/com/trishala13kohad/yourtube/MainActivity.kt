package com.trishala13kohad.yourtube

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest

class MainActivity : AppCompatActivity(), VideoClicked {

    private lateinit var mAdapter: VideoAdapter
     lateinit var pb :ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pb = findViewById(R.id.spinner)
        //Setting recycler view layout
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val keyword = findViewById<EditText>(R.id.inputKeyword)
        val search = findViewById<ImageView>(R.id.searchIcon)

        //Setting onClickListener for e item in the recycler view
        search.setOnClickListener {
            searchFromKeyword(keyword.text.toString())
        }

        //Setting custom adapter with the recycler view
        mAdapter = VideoAdapter(this)
        recyclerView.adapter = mAdapter
    }
    private fun searchFromKeyword(word: String){

        //Showing progress bas as the search begins
        pb.visibility = View.VISIBLE

        //Youtube url for searching list
        val url = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&type=videos&maxResults=100&q=$word&key=AIzaSyC94z0-8uL9DGCgVzWK9EKC8kqyhWkl2S4"

        //Json object request for the title, thumbnail etc. of the video
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url, null,
            {
                val videoJsonArray = it.getJSONArray("items")

                //Arraylist to store the details of the video
                val videosArray = ArrayList<VideoDetails>()
                videosArray.clear()

                for(i in 0 until videoJsonArray.length()){
                    val videosJsonObject = videoJsonArray.getJSONObject(i)

                    val id = videosJsonObject.getJSONObject("id").getString("videoId")

                    //URL to get likes and views of a particular video
                    val urli = "https://youtube.googleapis.com/youtube/v3/videos?part=statistics&id=$id&key=AIzaSyC94z0-8uL9DGCgVzWK9EKC8kqyhWkl2S4"
                    val jsonObjectRequest = JsonObjectRequest(
                        Request.Method.GET,
                        urli, null,
                        {
                            val videoJsonArray1 = it.getJSONArray("items")

                            //Arraylist to store likes and views of the particular video
                            val lVArray = ArrayList<LikesViews>()
                                val videosJsonObject1 = videoJsonArray1.getJSONObject(0)

                                val stats = videosJsonObject1.getJSONObject("statistics")
                                val likes = stats.getString("likeCount")
                                val views = stats.getString("viewCount")

                            //Adding to the array containing likes and views
                                lVArray.add(LikesViews(likes, views))

                            //Updating to theUI
                            mAdapter.updateVide(lVArray)
                        },
                        {})
                    //Adding request to Singleton class
                    MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)

                    //object to get the extra details of the video
                    val snippet = videosJsonObject.getJSONObject("snippet")

                    //getting channel name
                    val channel = snippet.getString("channelTitle")

                    //getting date when the video got published
                    val date = snippet.getString("publishedAt").substring(0,10)

                    //getting title of the video
                    val title = snippet.getString("title")

                    //getting description of the video
                    val description = snippet.getString("description")

                    //getting thumbnail url of the video
                    val thumbnails = snippet.getJSONObject("thumbnails")
                    val resolve = thumbnails.getJSONObject("high")
                    val thumbnailUrl = resolve.getString("url")

                    //Adding video details the the arraylist
                    val videos = VideoDetails(title, thumbnailUrl, date, description, channel, id)
                    videosArray.add(videos)
                }
                //updating the details to the UI
                mAdapter.updateVideo(videosArray)

                //making progressbar go away
                pb.visibility = View.GONE
            },
            {})
        //Adding jsonObjectRequest too the queue
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    //Called when an item in the recycler view is clicked
    override fun onItemClicked(item: VideoDetails, itemLV: LikesViews) {

        val i = Intent(this, VideoActivity::class.java)

        //Putting extras to display in the result activity
        i.putExtra("ids", item.ids)
        i.putExtra("description",item.description)
        i.putExtra("date", item.date)
        i.putExtra("channel", item.channelName)
        i.putExtra("title", item.title)
        i.putExtra("likes", itemLV.likes)
        i.putExtra("views", itemLV.views)

        //Starting the activity
        startActivity(i)
    }
}