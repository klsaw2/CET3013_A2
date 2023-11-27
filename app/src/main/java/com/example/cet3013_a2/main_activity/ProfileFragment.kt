package com.example.cet3013_a2.main_activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cet3013_a2.AddReporterActivity
import com.example.cet3013_a2.databinding.FragmentProfileBinding
import com.example.cet3013_a2.roomdb.ViewModel

class ProfileFragment : Fragment() {

    // Prepare reporter list
    private var reporterList = ArrayList<Array<String>>()

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Setup the recycler view with layout manager and adapter
        val recyclerReporter = _binding!!.recyclerProfile
        recyclerReporter.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        recyclerReporter.adapter = ProfileAdapter(requireContext(), reporterList) // Set the adapter

        viewModel = ViewModelProvider(this).get(ViewModel::class.java)

        // Add reporter button
        binding.fabAddReporter.setOnClickListener {
            val intent = Intent(activity, AddReporterActivity::class.java)
            startActivity(intent)
        }

        // Load reporters from the database
        loadReporters()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadReporters()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun loadReporters() {
        viewModel.getAllReporters().observe(viewLifecycleOwner) { reporters ->
            reporters?.let {
                reporterList.clear()
                for (i in reporters) {
                    reporterList.add(arrayOf(i.name, i.relationship, i.id.toString()))
                }

            }
        }
        val recyclerReporter = _binding!!.recyclerProfile
        recyclerReporter.adapter?.notifyDataSetChanged()
    }

}