package com.example.cet3013_a2.main_activity

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cet3013_a2.R
import com.example.cet3013_a2.roomdb.Record

class RecordsAdapter(context: Context,
                     recordList: List<Record>,
                     onItemClick: (position: Int) -> Unit) :
    RecyclerView.Adapter<RecordsAdapter.ViewHolder>() {

    private val mContext = context // Context passed in
    private val mRecordList = recordList // Record list passed in
    private val mOnItemClick = onItemClick

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // record holder resource
        var recordItem: TextView
        init {
            recordItem = itemView.findViewById(R.id.item_record_name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsAdapter.ViewHolder {
        // Inflate the custom layout (the layout_records_item)
        val view = View.inflate(mContext, R.layout.layout_records_item, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordsAdapter.ViewHolder, position: Int) {
        // Bind the reporter name and relation to the view holder
        holder.recordItem.text = mRecordList[position].title
        holder.recordItem.setOnClickListener {
            mOnItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        // Return the size of the record list
        return mRecordList.size
    }
}