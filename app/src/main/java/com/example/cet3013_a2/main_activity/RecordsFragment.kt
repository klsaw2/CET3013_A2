package com.example.cet3013_a2.main_activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN
import androidx.lifecycle.ViewModelProvider
import com.example.cet3013_a2.R
import com.example.cet3013_a2.databinding.FragmentRecordsBinding
import com.example.cet3013_a2.roomdb.Record
import com.example.cet3013_a2.roomdb.ViewModel

class RecordsFragment : Fragment() {

    // Prepare record list
    private var recordList = ArrayList<Record>()
    private var fullRecordList = ArrayList<Record>()

    // Bindings
    private var _binding: FragmentRecordsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ViewModel
    private var recordDetailFragment: RecordDetailFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the binding
         _binding = FragmentRecordsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)

        val landscapeDetailFragmentContainer = _binding?.recordDetailFragmentContainer

        // Setup the recycler view with layout manager and adapter
        val recyclerRecords = _binding!!.recyclerRecords
        recyclerRecords.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        if (landscapeDetailFragmentContainer != null) {
            recyclerRecords.adapter =
                RecordsAdapter(
                    requireContext(),
                    recordList
                ) {
                    onItemClick(it, true)
                }
        } else {
            recyclerRecords.adapter =
                RecordsAdapter(
                    requireContext(),
                    recordList
                ) {
                    onItemClick(it, false)
                }
        }

        _binding!!.btnSearch.setOnClickListener { v ->
            val searchKey = _binding!!.etvSearch.text.toString()
            viewModel.recordSearchKey = searchKey
            searchRecords(searchKey)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun searchRecords(searchKey: String) {
        val filteredRecordList = fullRecordList.filter {
            it.title.replace(" ", "").lowercase()
                .contains(searchKey.replace(" ", "").lowercase())
        }
        var sortedRecordList = if (!searchKey.isEmpty()) {
            filteredRecordList.sortedWith(
                compareBy({ it -> it.title },
                    { it -> it.title.length })
            )
        } else {
            fullRecordList
        }
        loadRecords(sortedRecordList.toMutableList() as ArrayList<Record>)
    }

    override fun onResume() {
        super.onResume()
        loadRecords()

        //  Attempt to restore saved viewModel state
        val viewModelRecord = viewModel.recordDetailFragmentRecord
        val viewModelSearchKey = viewModel.recordSearchKey
        if (viewModelRecord != null) {
            var imageBitmap: Bitmap? = null
            if (viewModelRecord.photoUrl != null) {
                imageBitmap = getRecordImageBitmap(viewModelRecord)
            }

            parentFragmentManager.fragments.map {
                Log.d("fmanager", it.tag.toString())
            }
            (parentFragmentManager.findFragmentByTag(recordDetailFragmentTag) as RecordDetailFragment?)
                ?.populateDetail(viewModelRecord, imageBitmap)
        }

        if (viewModelSearchKey != null) {
            searchRecords(viewModelSearchKey)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadRecords(searchedRecords: ArrayList<Record>? = null) {
        if (searchedRecords != null) {
            recordList.clear()
            searchedRecords.map {
                recordList.add(it)
            }
            // Notify the adapter that the data has changed
            binding.recyclerRecords.adapter!!.notifyDataSetChanged()
            return
        }

        viewModel.getAllRecords().observe(viewLifecycleOwner) { records ->
            // Add all the records to the record list
            recordList.clear()
            fullRecordList.clear()
            records.map {
                recordList.add(it)
                fullRecordList.add(it)
            }
            // Notify the adapter that the data has changed
            binding.recyclerRecords.adapter!!.notifyDataSetChanged()
        }
    }

    private fun onItemClick(position: Int, isInLandscape: Boolean) {
        var imageBitmap: Bitmap? = null
        val recordImageUrl = recordList[position].photoUrl

        if (recordImageUrl != null) {
            imageBitmap = getRecordImageBitmap(recordList[position])
        }
        viewModel.recordDetailFragmentRecord = recordList[position]

        recordDetailFragment = RecordDetailFragment()
        if (isInLandscape) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.recordDetailFragmentContainer, recordDetailFragment!!, recordDetailFragmentTag)
                .setTransition(TRANSIT_FRAGMENT_FADE)
                .commit()

        } else {
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, recordDetailFragment!!, recordDetailFragmentTag)
                .setTransition(TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(recordDetailFragmentTag)
                .commit()
        }

        parentFragmentManager.executePendingTransactions()
        (parentFragmentManager.findFragmentByTag(recordDetailFragmentTag) as RecordDetailFragment
            ).populateDetail(recordList[position], imageBitmap)

    }

    private fun getRecordImageBitmap(record: Record): Bitmap {
        val imageUri = Uri.parse(record.photoUrl)
        val source = ImageDecoder.createSource(requireContext().contentResolver, imageUri)
        return ImageDecoder.decodeBitmap(source)
    }

    companion object {
        const val recordDetailFragmentTag = "Records_DetailFragment"
    }
}