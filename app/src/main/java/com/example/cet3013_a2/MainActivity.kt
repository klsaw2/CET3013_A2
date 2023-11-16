package com.example.cet3013_a2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import com.example.cet3013_a2.databinding.ActivityMainBinding
import com.example.cet3013_a2.main_activity.ReporterFragment
import com.example.cet3013_a2.main_activity.SearchFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    companion object {
        const val searchFragmentTag = "MainActivity_SearchFragment"
        const val reportersFragmentTag = "MainActivity_ReportersFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchFragment = SearchFragment()
        val reporterFragment = ReporterFragment()

        binding.btnNavSearch.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(binding.mainFragmentContainer.id, searchFragment)
            transaction.setTransition(TRANSIT_FRAGMENT_FADE)

            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            transaction.addToBackStack(searchFragmentTag)
            transaction.commit()
        }
        binding.btnNavReporters.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(binding.mainFragmentContainer.id, reporterFragment)
            transaction.setTransition(TRANSIT_FRAGMENT_FADE)

            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            transaction.addToBackStack(reportersFragmentTag)
            transaction.commit()
        }
    }
}
