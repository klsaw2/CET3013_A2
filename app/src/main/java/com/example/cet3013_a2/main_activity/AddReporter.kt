package com.example.cet3013_a2.main_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.cet3013_a2.databinding.FragmentAddReporterBinding

class AddReporter  : Fragment() {

    private var _binding: FragmentAddReporterBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddReporterBinding.inflate(inflater, container, false)
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