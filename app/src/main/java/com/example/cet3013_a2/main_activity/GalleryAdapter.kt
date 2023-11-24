package com.example.cet3013_a2.main_activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cet3013_a2.R
import java.io.File

class GalleryAdapter(context: Context, imageList: ArrayList<String>, private val imageClickListener: OnImageClickListener) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    private val mContext = context // Context passed in
    private val mImageList = imageList // Image list passed in

    // Interface for the onClickListener
    interface OnImageClickListener {
        // Method to be implemented in the MainActivity
        fun onImageClick(imagePath: String)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Image holder resource
        var imgItem: ImageView
        init {
            imgItem = itemView.findViewById<ImageView>(R.id.gallery_item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the custom layout (the gallery_item_layout)
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.layout_gallery_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the image ID from the image list passed in
        val imgFile = File(mImageList[position])
        if (imgFile.exists()) {
            // Set the image into the image holder resource if the file exist
            Glide.with(mContext).load(imgFile).into(holder.imgItem)
        }
        // Set the onClickListener for each image
        holder.imgItem.setOnClickListener{
            // Call the interface method to launch the AddRecordActivity
            imageClickListener.onImageClick(mImageList[position])
        }
    }

    override fun getItemCount(): Int {
        // Return the size of the image list
        return mImageList.size
    }

}