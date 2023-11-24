package com.example.cet3013_a2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import com.example.cet3013_a2.databinding.ActivityMainBinding
import com.example.cet3013_a2.main_activity.GalleryAdapter
import com.example.cet3013_a2.main_activity.ProfileFragment
import com.example.cet3013_a2.main_activity.RecordsFragment

class MainActivity : AppCompatActivity(), GalleryAdapter.OnImageClickListener {
    private lateinit var binding: ActivityMainBinding
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

        // Set status bar color to match with app bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue3)
        // Reset title to Gallery when returning from other tabs
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount < 1) {
                // Set title text
                binding.tbMain.title = getString(R.string.t_gallery)
                // Set subtitle text
                binding.tbMain.subtitle = getString(R.string.st_gallery)
                // Set bottom navigation button color
                binding.btnNavRecords.setBackgroundColor(getColor(R.color.white))
                binding.btnNavProfile.setBackgroundColor(getColor(R.color.white))
            }
        }

        // Button click listeners ==============================================
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

    // Fragment Management Functions ==========================================
    private fun switchFragment(
        fragment: () -> Fragment,
        fragmentTag: String = "",
        titleID: Int = 0,
        subtitleID: Int = 0
    ) {
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
           R.string.t_records -> {
                // Set bottom navigation button color
               binding.btnNavRecords.setBackgroundColor(getColor(R.color.blue1))
               binding.btnNavProfile.setBackgroundColor(getColor(R.color.white))
            }
            R.string.t_profiles -> {
                // Set bottom navigation button color
                binding.btnNavRecords.setBackgroundColor(getColor(R.color.white))
                binding.btnNavProfile.setBackgroundColor(getColor(R.color.blue1))
            }
        }

        //DEMO Room
//        val viewModel = ViewModelProvider(this).get(ViewModel::class.java)
//        viewModel.getAllReporters().observe(this) {
//            reporters:List<Reporter> ->
//
//            Log.d("viewModel", "ViewModel:")
//            for (i in reporters.listIterator()) {
//                Log.d("viewModel", "${i.reporterId}" +
//                        "${i.reporterName}, " +
//                        "${i.reporterAge}" +
//                        ", ${i.reporterRelationship}")
//            }
//        }
//
//        binding.btnAdd.setOnClickListener {
//            val newReporter = Reporter(
//                reporterName = "testTitle",
//                reporterAge = 23,
//                reporterRelationship = "Mom"
//            )
//            val newRecord = Record(
//                title = "testTitle",
//                category = "testCategory",
//                dateTime = SimpleDateFormat("dd M yyyy").format(Date()).toString(),
//                reportedBy = 1
//            )
//
//            viewModel.addReporter(newReporter)
//            viewModel.addRecord(newRecord)
//        }
    }
}
