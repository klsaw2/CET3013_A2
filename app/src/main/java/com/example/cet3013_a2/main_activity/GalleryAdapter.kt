package com.example.cet3013_a2.main_activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cet3013_a2.R
import java.io.File

class GalleryAdapter(context: Context, imageList: ArrayList<String>) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    private val mContext = context // Context passed in
    private val mImageList = imageList // Image list passed in

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
            LayoutInflater.from(mContext).inflate(R.layout.gallery_item_layout, parent, false)
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
            // Display a toast message when the image is clicked
            Toast.makeText(mContext, "The picture path :" + mImageList[position], Toast.LENGTH_SHORT).show()
            // Other Function
//            TODO("Launch add new activity with selected image")
        }
    }

    override fun getItemCount(): Int {
        // Return the size of the image list
        return mImageList.size
    }

}