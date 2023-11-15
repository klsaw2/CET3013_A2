package com.example.cet3013_a2.main_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cet3013_a2.R
import com.example.cet3013_a2.databinding.FragmentEditReporterBinding

class EditReporter : Fragment() {

    private var _binding: FragmentEditReporterBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditReporterBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.cancelButton.setOnClickListener {
            //view.findNavController().navigate()
        }
        binding.confirmButton.setOnClickListener {
            TODO()
        }
        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}