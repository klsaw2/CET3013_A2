package com.example.cet3013_a2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import androidx.lifecycle.ViewModelProvider
import com.example.cet3013_a2.databinding.ActivityMainBinding
import com.example.cet3013_a2.main_activity.GalleryAdapter
import com.example.cet3013_a2.main_activity.GalleryFragment
import com.example.cet3013_a2.main_activity.ProfileFragment
import com.example.cet3013_a2.main_activity.RecordsFragment
import com.example.cet3013_a2.roomdb.ViewModel

class MainActivity : AppCompatActivity(), GalleryAdapter.OnImageClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ViewModel
    private var startAddReportActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "New record added successfully", Toast.LENGTH_SHORT).show()
        }
    }

    // Create a companion object to store the fragment tags
    companion object {
        const val recordsFragmentTag = "MainActivity_RecordsFragment"
        const val profileFragmentTag = "MainActivity_ProfileFragment"
        const val galleryFragmentTag = "MainActivity_GalleryFragment"
    }

    // GalleryAdapter.OnImageClickListener implementation =====================
    override fun onImageClick(imagePath: String) {
        // Launch add new activity with selected image
        val addReportIntent = Intent(this, AddRecordActivity::class.java)
        // Pass the image path to the AddRecordActivity
        addReportIntent.putExtra("imagePath", imagePath)
        // Start the activity for result
        startAddReportActivityForResult.launch(addReportIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ViewModel::class.java)

        // Check if is tablet mode
        if (binding.fragmentDetailContainer != null) {
            // Tablet mode
            // Landscape mode
            binding.fragmentDetailContainer!!.isGone =
                resources.configuration.orientation != android.content.res.Configuration.ORIENTATION_LANDSCAPE
        } else {
            // Phone mode

        }

        // Set status bar color to match with app bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue3)
        // Reset title to Gallery when returning from other tabs
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount < 1) {
                val titleID = R.string.t_gallery
                val subtitleID = R.string.st_gallery

                // Set title text
                binding.tbMain.title = getString(titleID)
                // Set subtitle text
                binding.tbMain.subtitle = getString(subtitleID)

                // Set view model
                viewModel.titleId = titleID
                viewModel.subtitleId = subtitleID

                // Set bottom navigation button color
                binding.btnNavGallery.setBackgroundColor(getColor(R.color.blue1))
                binding.btnNavRecords.setBackgroundColor(getColor(R.color.white))
                binding.btnNavProfile.setBackgroundColor(getColor(R.color.white))

                // Clear all saved viewModel variables from previous pages
                clearFragmentViewModel()
            }
        }

        // Button click listeners ==============================================
        binding.btnNavGallery.setOnClickListener {
            // Switch to GalleryFragment
            switchFragment(
                ::GalleryFragment, // Fragment constructor
                galleryFragmentTag, // Fragment tag
                R.string.t_gallery, // Tittle text
                R.string.st_gallery // Subtitle text
            )
        }
        binding.btnNavRecords.setOnClickListener {
            // Switch to RecordsFragment
            switchFragment(
                ::RecordsFragment, // Fragment constructor
                recordsFragmentTag, // Fragment tag
                R.string.t_records // Tittle text
            )
        }
        binding.btnNavProfile.setOnClickListener {
            // Switch to ProfileFragment
            switchFragment(
                ::ProfileFragment, // Fragment constructor
                profileFragmentTag, // Fragment tag
                R.string.t_profiles // Tittle text
            )
        }
        binding.btnNavAdd.setOnClickListener {
            val addReportIntent = Intent(this, AddRecordActivity::class.java)
            startAddReportActivityForResult.launch(addReportIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.titleId != null) {
            setNavigationUI(viewModel.titleId!!, viewModel.subtitleId!!)
        }
    }

    // Fragment Management Functions ==========================================
    private fun switchFragment(
        fragment: () -> Fragment,
        fragmentTag: String = "",
        titleID: Int = 0,
        subtitleID: Int = 0
    ) {
        // Clear all saved viewModel variables from previous pages
        clearFragmentViewModel()

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        // Replace container with desire fragment (function)
        transaction.replace(binding.mainFragmentContainer.id, fragment())
        // Set transition animation
        transaction.setTransition(TRANSIT_FRAGMENT_FADE)
        // Add to back stack
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        transaction.addToBackStack(fragmentTag)
        // Commit the changes
        transaction.commit()

        viewModel.titleId = titleID
        viewModel.subtitleId = subtitleID

        setNavigationUI(titleID, subtitleID)
    }

    fun clearFragmentViewModel() {
        // Clear all saved viewModel variables from previous pages
        // Records
        viewModel.recordDetailFragmentRecord = null
        viewModel.recordSearchKey = null
    }

    fun setNavigationUI(titleID: Int, subtitleID: Int) {
        // Set tittle text
        if (titleID != 0) {
            binding.tbMain.title = getString(titleID)
        } else {
            binding.tbMain.title = ""
        }

        // Set subtitle text
        if (subtitleID != 0) {
            binding.tbMain.subtitle = getString(subtitleID)
        } else {
            binding.tbMain.subtitle = ""
        }

        // Set bottom navigation button color after switching fragment
        when (titleID) {
            R.string.t_gallery -> {
                // Set bottom navigation button color
                binding.btnNavRecords.setBackgroundColor(getColor(R.color.white))
                binding.btnNavProfile.setBackgroundColor(getColor(R.color.white))
                binding.btnNavGallery.setBackgroundColor(getColor(R.color.blue1))
            }

            R.string.t_records -> {
                // Set bottom navigation button color
                binding.btnNavRecords.setBackgroundColor(getColor(R.color.blue1))
                binding.btnNavProfile.setBackgroundColor(getColor(R.color.white))
                binding.btnNavGallery.setBackgroundColor(getColor(R.color.white))
            }

            R.string.t_profiles -> {
                // Set bottom navigation button color
                binding.btnNavRecords.setBackgroundColor(getColor(R.color.white))
                binding.btnNavProfile.setBackgroundColor(getColor(R.color.blue1))
                binding.btnNavGallery.setBackgroundColor(getColor(R.color.white))
            }
        }
    }
}
