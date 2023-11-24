package com.example.cet3013_a2.main_activity

import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.cet3013_a2.R

// Ref: https://developer.android.com/training/data-storage/shared/media
// Ref: https://www.geeksforgeeks.org/how-to-build-an-image-gallery-android-app-with-recyclerview-and-glide/
// Ref: https://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
@Suppress("DEPRECATION", "PrivatePropertyName")
@SuppressLint("InlinedApi")
class GalleryFragment : Fragment() {
    // Prepare the images list
    private var images = ArrayList<String>()

    // Declare permission request code
    private val PERMISSIONREQUESTCODE = 100

    // Permission request launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
            } else {
                Log.i("Permission: ", "Denied")
            }
        }

    override fun onResume() {
        super.onResume()
        loadImages()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layoutView = inflater.inflate(R.layout.fragment_gallery, container, false)
        // Set up the recycler view with layout manager and adapter
        val recyclerGallery = layoutView.findViewById<RecyclerView>(R.id.recycler_gallery)
        recyclerGallery.layoutManager = GridLayoutManager(requireContext(), 4) // 4 columns
        recyclerGallery.addItemDecoration(GridSpacingItemDecoration(4, 20, true))
        recyclerGallery.adapter = GalleryAdapter(
            requireContext(),
            images,
            activity as GalleryAdapter.OnImageClickListener
        ) // Set the adapter
        checkPermissions()

        return layoutView
    }

    private fun checkPermissions() {
        // Check if the permission is granted
        val result = ContextCompat.checkSelfPermission(requireContext(), READ_MEDIA_IMAGES)
        // If the permission is granted, load the images
        when {
            result == PackageManager.PERMISSION_GRANTED -> {
//              Toast.makeText(requireContext(), "Permission Granted!", Toast.LENGTH_SHORT).show() // For Debugging
                loadImages()
            }
            // If the permission is not granted, request the permission
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                READ_MEDIA_IMAGES
            ) -> {
                // Request permission method 1
                requestPermissions(
                    arrayOf(READ_MEDIA_IMAGES),
                    PERMISSIONREQUESTCODE
                )
                // Launch the permission request dialog method 2
                requestPermissionLauncher.launch(READ_MEDIA_IMAGES)
            }

            else -> {
                // Request permission method 1
                requestPermissions(
                    arrayOf(READ_MEDIA_IMAGES),
                    PERMISSIONREQUESTCODE
                )
                // Launch the permission request dialog method 2
                requestPermissionLauncher.launch(READ_MEDIA_IMAGES)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONREQUESTCODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // Permission is granted
                    Toast.makeText(requireContext(), "Permission Granted!", Toast.LENGTH_SHORT)
                        .show()
                    loadImages()
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

    @SuppressLint("NotifyDataSetChanged")
    private fun loadImages() {
        // Clear the images list
        images.clear()

        // Declare query condition
        val projection = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
        val selection = null
        val selectionArgs = null
        val sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC"

        // Query the external storage for images
        requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor -> // Iterate over the cursor
            while (cursor.moveToNext()) { // Through all the images
                val colIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) // Get the image path
                if (!images.contains(cursor.getString(colIndex))) // If the image path is not in the images list
                    images.add(cursor.getString(colIndex)) // Add the image path to the images list
            }
        }
        // Get the RecyclerView from the view
        val recyclerGallery = view?.findViewById<RecyclerView>(R.id.recycler_gallery)
        // Update the recycler view if there is any changes
        recyclerGallery?.adapter?.notifyDataSetChanged()
    }

    inner class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int,
        private val includeEdge: Boolean
    ) :
        ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount // item column
            if (includeEdge) {
                outRect.left =
                    spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right =
                    (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)
                if (position < spanCount) { // top edge
                    outRect.top = spacing
                }
                outRect.bottom = spacing // item bottom
            } else {
                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right =
                    spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        }
    }
}