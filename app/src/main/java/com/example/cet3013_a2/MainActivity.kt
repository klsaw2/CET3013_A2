package com.example.cet3013_a2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import com.example.cet3013_a2.databinding.ActivityMainBinding
import com.example.cet3013_a2.main_activity.ProfileFragment
import com.example.cet3013_a2.main_activity.RecordsFragment
import com.example.cet3013_a2.main_activity.GalleryFragment
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val recordsFragmentTag = "MainActivity_RecordsFragment"
        const val profileFragmentTag = "MainActivity_ProfileFragment"
        const val galleryFragmentTag = "MainActivity_GalleryFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnNavRecords.setOnClickListener {
            // Switch to RecordsFragment
            switchFragment(
                binding.mainFragmentContainer.id, // containerID
                ::RecordsFragment, // Fragment constructor
                recordsFragmentTag, // Fragment tag
                R.string.lbl_records // Tittle text
            )

        }
        binding.btnNavProfile.setOnClickListener {
            // Switch to ProfileFragment
            switchFragment(
                binding.mainFragmentContainer.id, // containerID
                ::ProfileFragment, // Fragment constructor
                profileFragmentTag, // Fragment tag
                R.string.lbl_profile // Tittle text
            )
        }

    }

    private fun switchFragment(
        containerID: Int,
        fragment: () -> Fragment,
        fragmentTag: String = "",
        titleID: Int = 0
    ) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        // Replace container with desire fragment (function)
        transaction.replace(containerID, fragment())
        // Set transition animation
        transaction.setTransition(TRANSIT_FRAGMENT_FADE)
        // Add to back stack
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        transaction.addToBackStack(fragmentTag)
        // Commit the changes
        transaction.commit()

        // Set tittle text
        binding.tittleText.text = getString(titleID)
    }

}
