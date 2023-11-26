package com.example.cet3013_a2.main_activity

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import com.example.cet3013_a2.R
import com.example.cet3013_a2.databinding.FragmentRecordDetailBinding
import com.example.cet3013_a2.roomdb.Record
import com.example.cet3013_a2.roomdb.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class RecordDetailFragment: Fragment() {
    private var mRecord: Record? = null
    private var mImageBitmap: Bitmap? = null
    private lateinit var binding: FragmentRecordDetailBinding
    private lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordDetailBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val viewingRecord = viewModel.recordDetailFragmentRecord

        if (viewingRecord != null) {
            var imageBitmap: Bitmap? = null
            if (viewingRecord.photoUrl != null) {
                imageBitmap = getRecordImageBitmap(viewingRecord)
            }

            parentFragmentManager.fragments.map {
                Log.d("fmanager", it.tag.toString())
            }
            (parentFragmentManager.findFragmentByTag(RecordsFragment.recordDetailFragmentTag) as RecordDetailFragment?)
                ?.populateDetail(viewingRecord, imageBitmap)
        }
    }

    fun populateDetail(record: Record, bitmap: Bitmap?) {
        mRecord = record
        mImageBitmap = bitmap

        val dateFormatter = SimpleDateFormat("dd/M/yyyy", Locale.US)
        val timeFormatter = SimpleDateFormat("hh:mm a", Locale.US)
        val date = dateFormatter.format(mRecord!!.dateTime.toLong())
        val time = timeFormatter.format(mRecord!!.dateTime.toLong())

        val imgPhoto = binding!!.imgPhoto
        val txtDate = binding!!.txtDate
        val txtTime = binding!!.txtTime
        val txtTitle = binding!!.txtTitle
        val txtNotes = binding!!.txtNotes
        val txtCategory = binding!!.txtCategory
        val txtReporter = binding!!.txtReporter
        val txtLocationName = binding!!.txtInfoLocationName
        val txtLocationCoords = binding!!.txtInfoLocationCoords

        if (mImageBitmap != null) {
            imgPhoto.setImageBitmap(mImageBitmap)
        }

        txtDate.text = date
        txtTime.text = time
        txtTitle.text = mRecord!!.title
        txtNotes.text = mRecord!!.notes ?: "-"
        txtCategory.text = mRecord!!.category
        txtLocationName.text = mRecord!!.locationName
        txtLocationCoords.text = "( ${mRecord!!.locationLat}, ${mRecord!!.locationLng} )"
        viewModel.getReporter(mRecord!!.reportedBy) {
            txtReporter.text = it.name
        }
    }

    private fun getRecordImageBitmap(record: Record): Bitmap {
        val imageUri = Uri.parse(record.photoUrl)
        val source = ImageDecoder.createSource(requireContext().contentResolver, imageUri)
        return ImageDecoder.decodeBitmap(source)
    }

}