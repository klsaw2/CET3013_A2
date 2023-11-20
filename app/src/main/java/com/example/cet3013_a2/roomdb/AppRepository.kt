package com.example.cet3013_a2.roomdb

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppRepository (application: Application) {
    private var reportDao: ReportDao
    private var reporterDao: ReporterDao
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    var reports: LiveData<List<Report>>
    var reporters: LiveData<List<Reporter>>

    init {
        val db = AppDatabase.getDatabase(application)
        reportDao = db!!.getReportDao()
        reporterDao = db.getReporterDao()
        reports = reportDao.getAllReports()
        reporters = reporterDao.getAllReporters()
    }

    // Report async API
    fun addReport(report: Report) {
        coroutineScope.launch(Dispatchers.IO) {
            reportDao.addReport(report)
        }
    }

    fun updateReport(report: Report) {
        coroutineScope.launch(Dispatchers.IO) {
            reportDao.deleteReport(report)
        }
    }

    fun deleteReport(report: Report) {
        coroutineScope.launch(Dispatchers.IO) {
            reportDao.deleteReport(report)
        }
    }

    // Reporter async API
    fun addReporter(reporter: Reporter) {
        coroutineScope.launch(Dispatchers.IO) {
            reporterDao.addReporter(reporter)
        }
    }

    fun updateReporter(reporter: Reporter) {
        coroutineScope.launch(Dispatchers.IO) {
            reporterDao.deleteReporter(reporter)
        }
    }

    fun deleteReporter(reporter: Reporter) {
        coroutineScope.launch(Dispatchers.IO) {
            reporterDao.deleteReporter(reporter)
        }
    }
}