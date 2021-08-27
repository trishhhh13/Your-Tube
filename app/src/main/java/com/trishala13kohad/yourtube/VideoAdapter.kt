package com.trishala13kohad.yourtube


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class VideoAdapter(private val listener: VideoClicked): RecyclerView.Adapter<VideoViewHolder>() {

    //ArrayList of videos detail
    private val items: ArrayList<VideoDetails> = ArrayList()
    //Arraylist of likes and counts of every video shown
    private val itemsLV: ArrayList<LikesViews> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        val viewHolder = VideoViewHolder(view)

        //Setting onClickListener for items in recycler view
        view.setOnClickListener {
            listener.onItemClicked(items[viewHolder.adapterPosition],
                itemsLV[viewHolder.adapterPosition])
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val currentItem = items[position]

        //Setting text in the first page
        holder.title.text = currentItem.title
        holder.description.text = currentItem.description
        holder.channel.text = currentItem.channelName
        holder.date.text = currentItem.date
        holder.id.text = currentItem.ids
        Glide.with(holder.itemView.context).load(currentItem.thumbnailUrl).into(holder.thumbnail)

    }

    override fun getItemCount(): Int {
        return items.size
    }

    //updating arraylist of video details
    @SuppressLint("NotifyDataSetChanged")
    fun updateVideo(updatedVideos: ArrayList<VideoDetails>)
    {
        items.clear()
        items.addAll(updatedVideos)

        notifyDataSetChanged()
    }

    //updating arraylist of likes and views
    @SuppressLint("NotifyDataSetChanged")
    fun updateVide(lvArray: ArrayList<LikesViews>) {

        itemsLV.addAll(lvArray)

        notifyDataSetChanged()

    }
}
class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    //getting the views to set the text
    val title : TextView = itemView.findViewById(R.id.titleView)
    val thumbnail : ImageView = itemView.findViewById(R.id.thumbnailImage)
    val description : TextView = itemView.findViewById(R.id.descript)
    val date : TextView = itemView.findViewById(R.id.date)
    val channel : TextView = itemView.findViewById(R.id.channelName)
    val id : TextView = itemView.findViewById(R.id.idText)
}
interface VideoClicked{
    //overriden in mainActivity
    fun onItemClicked(item: VideoDetails, itemLV: LikesViews)
}