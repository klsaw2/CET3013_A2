package com.example.cet3013_a2.MainActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cet3013_a2.R
import com.example.cet3013_a2.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {
    lateinit var binding: FragmentGalleryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
        binding.gridGallery.adapter
    }
}