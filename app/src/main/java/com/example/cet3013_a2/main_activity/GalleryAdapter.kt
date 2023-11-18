package com.example.cet3013_a2.main_activity

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.cet3013_a2.R
import java.io.File
import java.util.LinkedList


//class GalleryAdapter(context: Context, imagePaths: List<String>): RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
class GalleryAdapter(context: Context, imagePaths: LinkedList<Int>, imageList: ArrayList<String>) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    private val mImagePaths = imagePaths
    private val mContext = context
    private val mImageList = imageList

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var imgItem: ImageButton
        var imgItem: ImageView
        init {
//            imgItem = itemView.findViewById<ImageButton>(R.id.img_item)
            imgItem = itemView.findViewById<ImageView>(R.id.gallery_item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
//            LayoutInflater.from(mContext).inflate(R.layout.layout_gallery_item, parent, false)
            LayoutInflater.from(mContext).inflate(R.layout.gallery_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // DEMO ONLY - change image set method later
        val mCurrent = mImagePaths[position]
        holder.imgItem.setImageResource(mCurrent)
        holder.imgItem.setOnClickListener {
            Toast.makeText(mContext, "$position", Toast.LENGTH_SHORT).show()
        }

        // T2
//        val imgFile = File(mImageList[position])
//        if (imgFile.exists()) {
//            Glide.with(mContext).load(imgFile).into(holder.imgItem)
//        }
    }

    override fun getItemCount(): Int {
        return mImagePaths.size
//        return mImageList.size
//        return 12
    }

}