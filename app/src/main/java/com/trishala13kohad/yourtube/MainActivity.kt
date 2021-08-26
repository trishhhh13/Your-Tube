package com.trishala13kohad.yourtube

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

class MainActivity : AppCompatActivity() {

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
        mAdapter = VideoAdapter()
        recyclerView.adapter = mAdapter
    }
    private fun searchFromKeyword(word: String){

        val url = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&maxResults=100&q=$word&key=AIzaSyAgkkGRqm3Cq2uvIJzwQ3Qk6rK4mzs6xTM"

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
                    val videoUrls = "https://www.youtube.com/watch?v=$id"
                    val snippet = videosJsonObject.getJSONObject("snippet")
                    val channel = snippet.getString("channelTitle")
                    val date = snippet.getString("publishedAt").substring(0,10)
                    val title = snippet.getString("title")
                    val description = snippet.getString("description")
                    val thumbnails = snippet.getJSONObject("thumbnails")
                    val defaul = thumbnails.getJSONObject("high")
                    val thumbnailUrl = defaul.getString("url")
                    val videos = VideoDetails(title, thumbnailUrl, date, description, channel)
                    videosArray.add(videos)
                }
                mAdapter.updateVideo(videosArray)
            },
            {

            }
        )
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }
}