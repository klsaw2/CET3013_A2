package com.example.cet3013_a2.main_activity

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.cet3013_a2.R
import java.util.LinkedList

//  Ref: https://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
class GalleryFragment : Fragment() {
    private var imgPaths = LinkedList<String>()

//    DEMO image only
    init {
        for (i in 1..5) {
            imgPaths.addLast("pizza$i.jpg")
        }
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layoutView = inflater.inflate(R.layout.fragment_gallery, container, false)
        val recyclerGallery = layoutView.findViewById<RecyclerView>(R.id.recycler_gallery)

        recyclerGallery.layoutManager = GridLayoutManager(requireContext(), 4)
        recyclerGallery.addItemDecoration(GridSpacingItemDecoration(4,20, true))
        recyclerGallery.adapter = GalleryAdapter(requireContext(), imgPaths)
        return layoutView
    }
}