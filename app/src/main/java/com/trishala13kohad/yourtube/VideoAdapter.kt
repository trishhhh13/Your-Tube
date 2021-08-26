package com.trishala13kohad.yourtube


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class VideoAdapter: RecyclerView.Adapter<VideoViewHolder>() {
    private val items: ArrayList<VideoDetails> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val currentItem = items[position]
        holder.title.text = currentItem.title
        holder.description.text = currentItem.description
        holder.channel.text = currentItem.channelName
        holder.date.text = currentItem.date
        Glide.with(holder.itemView.context).load(currentItem.thumbnailUrl).into(holder.thumbnail)

    }

    override fun getItemCount(): Int {
        return items.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateVideo(updatedNews: ArrayList<VideoDetails>)
    {
        items.clear()
        items.addAll(updatedNews)

        notifyDataSetChanged()
    }
}
class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    val title : TextView = itemView.findViewById(R.id.titleView)
    val thumbnail : ImageView = itemView.findViewById(R.id.thumbnailImage)
    val description : TextView = itemView.findViewById(R.id.desc)
    val date : TextView = itemView.findViewById(R.id.date)
    val channel : TextView = itemView.findViewById(R.id.channelName)
}