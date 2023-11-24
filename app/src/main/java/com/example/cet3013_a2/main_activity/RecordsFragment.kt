package com.example.cet3013_a2.main_activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cet3013_a2.databinding.FragmentRecordsBinding
import com.example.cet3013_a2.roomdb.AppDatabase

class RecordsFragment : Fragment() {

    // Prepare record list
    private var recordList = ArrayList<String>()

    // Bindings
    private var _binding: FragmentRecordsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the binding
        _binding = FragmentRecordsBinding.inflate(inflater, container, false)

        // Setup the recycler view with layout manager and adapter
        val recyclerRecord = _binding!!.recyclerRecords
        recyclerRecord.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        recyclerRecord.adapter = RecordsAdapter(requireContext(), recordList) // Set the adapter

        // Load records from the database
        loadRecords()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadRecords()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadRecords() {
        // Get the database instance
        val recordDB = AppDatabase.getDatabase(requireContext())!!
        recordDB.getRecordDao().getAllRecords().observe(viewLifecycleOwner) { records ->
            // Clear the record list
            recordList.clear()
            // Add all the records to the record list
            for (i in records) {
                recordList.add(i.title)
            }
            // Notify the adapter that the data has changed
            binding.recyclerRecords.adapter!!.notifyDataSetChanged()
        }

    }


}