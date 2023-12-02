package com.example.cet3013_a2.main_activity

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.cet3013_a2.ConfirmationDialog
import com.example.cet3013_a2.EditRecordActivity
import com.example.cet3013_a2.R
import com.example.cet3013_a2.databinding.FragmentRecordDetailBinding
import com.example.cet3013_a2.roomdb.Record
import com.example.cet3013_a2.roomdb.ViewModel
import java.text.SimpleDateFormat
import java.util.Locale


class RecordDetailFragment: Fragment() {
    private var mRecord: Record? = null
    private lateinit var binding: FragmentRecordDetailBinding
    private lateinit var viewModel: ViewModel
    private val editActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { 
        result ->
            if (result.resultCode == RESULT_OK) {
                // Set new binding content
                val returnIntent = result.data!!
                val updatedRecord = Record(
                    id = returnIntent.getIntExtra("id", -1),
                    title = returnIntent.getStringExtra("title")!!,
                    notes = returnIntent.getStringExtra("notes"),
                    category = returnIntent.getStringExtra("category")!!,
                    photoUrl = returnIntent.getStringExtra("photoUrl"),
                    reportedBy = returnIntent.getIntExtra("reportedBy", -1),
                    dateTime = returnIntent.getStringExtra("dateTime")!!,
                    locationName = returnIntent.getStringExtra("locationName")!!,
                    locationLat = returnIntent.getDoubleExtra("locationLat", -1.0),
                    locationLng = returnIntent.getDoubleExtra("locationLng", -1.0),
                )
                populateDetail(updatedRecord)
                // Notify success
                Toast.makeText(this.requireContext(), "Record edited successfully", Toast.LENGTH_SHORT)
                    .show()
            }

    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordDetailBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)

        binding.btnShowLocation.setOnClickListener {
            val url = "https://maps.google.com/maps/search/" + mRecord!!.locationLat + "," + mRecord!!.locationLng
            val myMap = Uri.parse(url)
            val mapIntent = Intent(Intent.ACTION_VIEW, myMap)
            startActivity(mapIntent)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val viewingRecord = viewModel.recordDetailFragmentRecord

        if (viewingRecord != null) {
            parentFragmentManager.fragments.map {
                Log.d("fmanager", it.tag.toString())
            }
            (parentFragmentManager.findFragmentByTag(RecordsFragment.recordDetailFragmentTag) as RecordDetailFragment?)
                ?.populateDetail(viewingRecord)
        }
    }

    fun populateDetail(record: Record) {
        mRecord = record

        val imgPhoto = binding.imgPhoto as ImageView
        val txtDate = binding.txtDate
        val txtTime = binding.txtTime
        val txtTitle = binding.txtTitle
        val txtNotes = binding.txtNotes
        val txtCategory = binding.txtCategory
        val txtReporter = binding.txtReporter
        val txtLocationName = binding.txtInfoLocationName
        val txtLocationCoords = binding.txtInfoLocationCoords
        
        viewModel.getRecordById(mRecord!!.id!!).observe(viewLifecycleOwner) {
            mRecord = it.first()
            viewModel.recordDetailFragmentRecord = it.first()

            // Check if image exists
            val bitmap = getRecordImageBitmap(mRecord!!)
            if (bitmap != null) {
                // Set image
                imgPhoto.setImageBitmap(bitmap)
            } else {
                imgPhoto.setImageDrawable(
                    // Set default image
                    ContextCompat.getDrawable(requireContext(), R.drawable.icon_no_image)
                )
            }

            val dateFormatter = SimpleDateFormat("dd/M/yyyy", Locale.US)
            val timeFormatter = SimpleDateFormat("hh:mm a", Locale.US)
            val date = dateFormatter.format(mRecord!!.dateTime.toLong())
            val time = timeFormatter.format(mRecord!!.dateTime.toLong())

            txtDate.text = date
            txtTime.text = time
            txtTitle.text = mRecord!!.title
            txtNotes.text = mRecord!!.notes ?: "-"
            txtCategory.text = mRecord!!.category
            txtLocationName.text = mRecord!!.locationName
            txtLocationCoords.text = "( ${mRecord!!.locationLat}, ${mRecord!!.locationLng} )"
            viewModel.getReporterWithSuccessCallback(mRecord!!.reportedBy) {
                txtReporter.text = it.name
            }
        }

        binding.btnShowLocation.setOnClickListener {
            val url = "http://maps.google.com/maps/search/${mRecord!!.locationLat},${mRecord!!.locationLng}"
            val myMap = Uri.parse(url)
            val mapIntent = Intent(Intent.ACTION_VIEW, myMap)
            startActivity(mapIntent)
        }

        binding.btnDeleteRecord.setOnClickListener {
            val confirmationDialog = ConfirmationDialog(
                getString(R.string.confirmation_title),
                "The record will be removed permanently",
                getString(R.string.btn_confirm),
                getString(R.string.btn_cancel)
            ) { dialog, i ->
                deleteRecord(mRecord!!)
            }
            confirmationDialog.show(parentFragmentManager, "deleteConfirmation")
        }

        binding.btnEditRecord.setOnClickListener {
            val intent = Intent(context, EditRecordActivity::class.java)
            intent.putExtra(EditRecordActivity.recordIdExtra, mRecord!!.id)
            editActivityForResult.launch(intent)
        }
    }

    private fun deleteRecord(record: Record) {
        Log.d("delete", "delete")
        // Remove detailFragment
        val existingDetailFragment = parentFragmentManager.findFragmentByTag(RecordsFragment.recordDetailFragmentTag) as RecordDetailFragment?

        val isInLandscape = requireArguments().getBoolean("isInLandscape")
        if (isInLandscape) {
            parentFragmentManager.beginTransaction()
                .remove(existingDetailFragment!!)
                .commit()
        } else {
            parentFragmentManager.popBackStack()
        }

        // Remove record after system references are cleared
        viewModel.deleteRecord(record)
        viewModel.recordDetailFragmentRecord = null
        Toast.makeText(requireContext(), "Record deleted successfully", Toast.LENGTH_SHORT).show()
    }

    private fun getRecordImageBitmap(record: Record): Bitmap? {
        return if (record.photoUrl != null) {
            val imageUri = Uri.parse(record.photoUrl)
            val source = ImageDecoder.createSource(requireContext().contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            null
        }
    }

}