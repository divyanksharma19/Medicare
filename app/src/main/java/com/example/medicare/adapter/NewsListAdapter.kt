package com.example.medicare.adapter

import News
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.medicare.R


class NewsListAdapter( private val listener: NewsItemClicked): RecyclerView.Adapter<NewsViewHolder>() {
    private val items: ArrayList<News> = ArrayList()
//    private val backgroundColors = arrayOf(
//
//        R.color.lavender,
//
//
//    )
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news,parent,false)
        val viewHolder = NewsViewHolder(view)
        view.setOnClickListener{
            listener.onitemClicked(items[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = items[position]
        val context = holder.itemView.context

        val titleText = if (currentItem.title == "null") "News" else currentItem.title
        holder.titleView.text = titleText
        holder.author.text = currentItem.author

        val glideRequest = Glide.with(context)
            .load(currentItem.imageUrl)

        // Check if currentItem.imageUrl is null and set a placeholder drawable
        if (currentItem.imageUrl == null || currentItem.imageUrl == "null") {
            glideRequest.placeholder(R.drawable.medicarelogo) // Replace 'default_image' with your drawable resource
        }

        glideRequest.into(holder.image)
        // Assign a background color to the item based on its position
//        val colorResId = backgroundColors[position % backgroundColors.size]
//        holder.itemView.setBackgroundResource(colorResId)
    }



    @SuppressLint("NotifyDataSetChanged")
    fun updateNews(updateNews:ArrayList<News>){
        items.clear()
        items.addAll(updateNews)

        notifyDataSetChanged()
    }
}
class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val titleView: TextView = itemView.findViewById(R.id.title)
    val image: ImageView = itemView.findViewById(R.id.image)
    val author: TextView = itemView.findViewById(R.id.author)

}
interface NewsItemClicked{
    fun onitemClicked(item:News)

}