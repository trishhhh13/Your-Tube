package com.trishala13kohad.yourtube

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView

class VideoActivity : YouTubeBaseActivity() {

    private val api_key = "AIzaSyC94z0-8uL9DGCgVzWK9EKC8kqyhWkl2S4"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        //Getting video id to play further
        val id = intent.getStringExtra("ids")

        //Getting title and setting to UI
        val title = intent.getStringExtra("title")
        val titleVideo = findViewById<TextView>(R.id.vidTitle)
        titleVideo.text = title

        //Getting likes and setting to UI
        val like = intent.getStringExtra("likes")
        val likes = findViewById<TextView>(R.id.likess)
        likes.text = like

        //Getting views and setting to UI
        val view = intent.getStringExtra("views")
        val views = findViewById<TextView>(R.id.viewss)
        views.text = view

        //Getting description and setting to UI
        val desc = intent.getStringExtra("description")
        val describe = findViewById<TextView>(R.id.desco)
        describe.text = desc

        //Getting date and setting date and year to UI
        val dates = intent.getStringExtra("date")
        val datePublished = findViewById<TextView>(R.id.datess)
        val yearPublished = findViewById<TextView>(R.id.year)
        if (dates != null) {
            val temp = dates.substring(8)+dates.substring(4, 7)
            datePublished.text = temp
            yearPublished.text = dates.substring(0, 4)
        }

        // Get reference to the view of Video player
        val ytPlayer = findViewById<YouTubePlayerView>(R.id.ytPlayer)

        ytPlayer.initialize(api_key, object : YouTubePlayer.OnInitializedListener{

            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider?,
                player: YouTubePlayer?,
                p2: Boolean
            ) {
                player?.loadVideo(id)
                player?.play()
            }

            // Inside onInitializationFailure
            // implement the failure functionality
            // Here we will show toast
            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {
                Toast.makeText(this@VideoActivity , "Video player Failed" , Toast.LENGTH_SHORT).show()
            }
        })
    }
}
