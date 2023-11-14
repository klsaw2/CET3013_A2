package com.example.cet3013_a2.main_activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cet3013_a2.R

class GalleryAdapter(context: Context, imagePaths: List<String>): RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    private val mImagePaths = imagePaths
    private val mContext = context

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var imgItem: ImageButton
        init {
            imgItem = itemView.findViewById<ImageButton>(R.id.img_item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.layout_gallery_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // DEMO ONLY - change image set method later
        holder.imgItem.setImageResource(R.drawable.pizza1)
        holder.imgItem.setOnClickListener {
            Toast.makeText(mContext, "$position", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return mImagePaths.size
    }

}