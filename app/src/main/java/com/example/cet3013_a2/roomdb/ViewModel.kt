package com.example.cet3013_a2.roomdb

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class ViewModel(application: Application): AndroidViewModel(application) {
    private var repository: AppRepository
    private var reports: LiveData<List<Report>>
    private var reporters: LiveData<List<Reporter>>

    init {
        repository = AppRepository(application)
        reports = repository.reports
        reporters = repository.reporters
    }

    // Report API
    fun addReport(report: Report) {
        repository.addReport(report)
    }

    fun updateReport(report: Report) {
        repository.updateReport(report)
    }

    fun deleteReport(report: Report) {
        repository.deleteReport(report)
    }

    fun getAllReports(): LiveData<List<Report>> {
        return reports
    }

    // Reporter API
    fun addReporter(reporter: Reporter) {
        repository.addReporter(reporter)
    }

    fun updateReporter(reporter: Reporter) {
        repository.updateReporter(reporter)
    }

    fun deleteReporter(reporter: Reporter) {
        repository.deleteReporter(reporter)
    }
    fun getAllReporters(): LiveData<List<Reporter>> {
        return reporters
    }
}