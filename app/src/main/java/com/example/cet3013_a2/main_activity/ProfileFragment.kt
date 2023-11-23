package com.example.cet3013_a2.main_activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.example.cet3013_a2.AddReporterActivity
import com.example.cet3013_a2.R
import com.example.cet3013_a2.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(), MenuProvider {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.fabAddReporter.setOnClickListener {
            val intent = Intent(activity,AddReporterActivity::class.java)
            startActivity(intent)
        }
        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.simple_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId){
//            R.id.back ->{
//                TODO()
            }
//            else -> return true
        return true
    }
}