package com.example.cet3013_a2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import androidx.lifecycle.ViewModelProvider
import com.example.cet3013_a2.databinding.ActivityMainBinding
import com.example.cet3013_a2.main_activity.ReporterFragment
import com.example.cet3013_a2.main_activity.SearchFragment
import com.example.cet3013_a2.roomdb.AppRepository
import com.example.cet3013_a2.roomdb.Report
import com.example.cet3013_a2.roomdb.Reporter
import com.example.cet3013_a2.roomdb.ViewModel
import java.text.SimpleDateFormat
import java.util.Date

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
//            val newReport = Report(
//                title = "testTitle",
//                category = "testCategory",
//                dateTime = SimpleDateFormat("dd M yyyy").format(Date()).toString(),
//                reportedBy = 1
//            )
//
//            viewModel.addReporter(newReporter)
//            viewModel.addReport(newReport)
//        }
    }
}
