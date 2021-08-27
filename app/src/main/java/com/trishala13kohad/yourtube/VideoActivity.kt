package com.trishala13kohad.yourtube

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView

class VideoActivity : YouTubeBaseActivity() {

    // Change the AppCompactActivity to YouTubeBaseActivity()

    companion object {

    const val NAME_EXTRA = "hello"
}
    // Add the api key that you had
    // copied from google API
    // This is a dummy api key
    private val api_key = "AIzaSyC94z0-8uL9DGCgVzWK9EKC8kqyhWkl2S4"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        val id = intent.getStringExtra(NAME_EXTRA)
        val title = intent.getStringExtra("title")
        val titlevid = findViewById<TextView>(R.id.vidTitle)
        titlevid.text = title
        val like = intent.getStringExtra("likes")
        val likes = findViewById<TextView>(R.id.likess)
        likes.text = like
        val view = intent.getStringExtra("views")
        val views = findViewById<TextView>(R.id.viewss)
        views.text = view

        val desc = intent.getStringExtra("description")
        val desci = findViewById<TextView>(R.id.desco)
        desci.text = desc

        val dates = intent.getStringExtra("date")

        val tarikh = findViewById<TextView>(R.id.datess)
        if (dates != null) {
            tarikh.text = dates.substring(8)+ dates.substring(4, 7)
        }

        val tarik = findViewById<TextView>(R.id.year)
        if (dates != null) {
            tarik.text = dates.substring(0, 4)
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
