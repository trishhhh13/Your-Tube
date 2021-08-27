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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val pb = findViewById<ProgressBar>(R.id.spinner)
        val keyword = findViewById<EditText>(R.id.inputKeyword)
        val search = findViewById<ImageView>(R.id.searchIcon)
        search.setOnClickListener {
            pb.visibility = View.VISIBLE
            searchFromKeyword(keyword.text.toString())
            pb.visibility = View.GONE
        }
        mAdapter = VideoAdapter(this)
        recyclerView.adapter = mAdapter
    }
    private fun searchFromKeyword(word: String){

        val url = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&type=videos&maxResults=100&q=$word&key=AIzaSyC94z0-8uL9DGCgVzWK9EKC8kqyhWkl2S4"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url, null,
            {
                val videoJsonArray = it.getJSONArray("items")
                val videosArray = ArrayList<VideoDetails>()
                videosArray.clear()
                for(i in 0 until videoJsonArray.length()){
                    val videosJsonObject = videoJsonArray.getJSONObject(i)
                    val id = videosJsonObject.getJSONObject("id").getString("videoId")
                    val urli = "https://youtube.googleapis.com/youtube/v3/videos?part=statistics&id=$id&key=AIzaSyC94z0-8uL9DGCgVzWK9EKC8kqyhWkl2S4"
                    val jsonObjectRequest = JsonObjectRequest(
                        Request.Method.GET,
                        urli, null,
                        {
                            val videoJsonArray1 = it.getJSONArray("items")
                            val LVArray = ArrayList<LikesViews>()
                                val videosJsonObject1 = videoJsonArray1.getJSONObject(0)
                                val stats = videosJsonObject1.getJSONObject("statistics")
                                val likes = stats.getString("likeCount")
                                val views = stats.getString("viewCount")
                                LVArray.add(LikesViews(likes, views))
                            mAdapter.updateVide(LVArray)
                        },
                        {

                        }
                    )
                    MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)

                    val snippet = videosJsonObject.getJSONObject("snippet")
                    val channel = snippet.getString("channelTitle")
                    val date = snippet.getString("publishedAt").substring(0,10)
                    val title = snippet.getString("title")
                    val description = snippet.getString("description")
                    val thumbnails = snippet.getJSONObject("thumbnails")
                    val defaul = thumbnails.getJSONObject("high")
                    val thumbnailUrl = defaul.getString("url")
                    val videos = VideoDetails(title, thumbnailUrl, date, description, channel, id)
                    videosArray.add(videos)
                }
                mAdapter.updateVideo(videosArray)
            },
            {

            }
        )
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    override fun onItemClicked(item: VideoDetails, itemlv: LikesViews) {
        val i = Intent(this, VideoActivity::class.java)
        i.putExtra(VideoActivity.NAME_EXTRA, item.ids)
        i.putExtra("description",item.description)
        i.putExtra("date", item.date)
        i.putExtra("channel", item.channelName)
        i.putExtra("title", item.title)

        i.putExtra("likes", itemlv.likes)
        i.putExtra("views", itemlv.views)
        startActivity(i)
    }
}