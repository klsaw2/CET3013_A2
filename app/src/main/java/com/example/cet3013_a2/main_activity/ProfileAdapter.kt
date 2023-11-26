package com.example.cet3013_a2.main_activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cet3013_a2.EditReporterActivity
import com.example.cet3013_a2.R

class ProfileAdapter(context: Context, reporterList: ArrayList<Array<String>>) :
    RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {

    private val mContext = context // Context passed in
    private val mReporterList = reporterList // Reporter list passed in

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // reporter holder resource
        var reporterNameItem: TextView
        var reporterRelationItem: TextView

        init {
            reporterNameItem = itemView.findViewById(R.id.item_reporter_name)
            reporterRelationItem = itemView.findViewById(R.id.item_reporter_relation)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the custom layout (the profile_reporter_item_layout)
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.layout_profile_reporter_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        // Return the size of the reporter list
        return mReporterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind the reporter name and relation to the view holder
        holder.reporterNameItem.text = mReporterList[position][0]
        holder.reporterRelationItem.text = mReporterList[position][1]
        // Start the EditReporterActivity
        holder.itemView.setOnClickListener {
           val intent = Intent(mContext, EditReporterActivity::class.java)
            intent.putExtra("id", mReporterList[position][2].toInt())
            mContext.startActivity(intent)
        }

    }
}


