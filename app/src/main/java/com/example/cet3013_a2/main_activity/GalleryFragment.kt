package com.example.cet3013_a2.main_activity

import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cet3013_a2.R
import java.util.LinkedList


//  Ref: https://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
class GalleryFragment : Fragment() {
    private var imgPaths = LinkedList<Int>()
    private var images = ArrayList<String>()

    // Test image ===============================================
    private var PERMISSION_REQUEST_CODE = 100
    private var recyclerView: RecyclerView? = null
    private var adapter: GalleryAdapter? = null
    private var manager: GridLayoutManager? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
            } else {
                Log.i("Permission: ", "Denied")
            }
        }
    // Test image ===============================================

    //    DEMO image only
    init {
        imgPaths.addLast(R.drawable.pizza1)
        imgPaths.addLast(R.drawable.people)
        imgPaths.addLast(R.drawable.pizza1)
        imgPaths.addLast(R.drawable.people)
        imgPaths.addLast(R.drawable.people)
        imgPaths.addLast(R.drawable.pizza1)
        imgPaths.addLast(R.drawable.pizza1)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layoutView = inflater.inflate(R.layout.fragment_gallery, container, false)
        val recyclerGallery = layoutView.findViewById<RecyclerView>(R.id.recycler_gallery)

        recyclerGallery.layoutManager = GridLayoutManager(requireContext(), 4)
        recyclerGallery.adapter = GalleryAdapter(requireContext(), imgPaths, images)
        checkPermissions()

        return layoutView
    }


    private fun checkPermissions() {
        val result =
            ContextCompat.checkSelfPermission(requireContext(), READ_MEDIA_IMAGES)
        when {
            result == PackageManager.PERMISSION_GRANTED -> {

//                Toast.makeText(requireContext(), "Permission Granted!", Toast.LENGTH_SHORT).show() // For Debugging
                //            loadImages()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                READ_MEDIA_IMAGES
            ) -> {
                requestPermissions(
                    arrayOf(READ_MEDIA_IMAGES),
                    PERMISSION_REQUEST_CODE)
                requestPermissionLauncher.launch(READ_MEDIA_IMAGES)
            }

            else -> {
                requestPermissions(
                    arrayOf(READ_MEDIA_IMAGES),
                    PERMISSION_REQUEST_CODE)
                requestPermissionLauncher.launch(READ_MEDIA_IMAGES)
            }
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // Permission is granted
                    Toast.makeText(requireContext(), "Permission Granted!", Toast.LENGTH_SHORT)
                        .show()
//                    loadImages()
                } else {
                    // Permission is denied
                    Toast.makeText(
                        requireContext(),
                        "Permission is not Granted, unable to load your images.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }

            else -> {
                // Ignore all other requests.
            }
        }
    }
}