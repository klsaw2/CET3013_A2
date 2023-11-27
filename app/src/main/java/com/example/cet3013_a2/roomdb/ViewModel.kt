package com.example.cet3013_a2.roomdb

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class ViewModel(application: Application): AndroidViewModel(application) {
    // UI data
    // Navigation
    var titleId: Int? = null
    var subtitleId: Int? = null

    // Records
    var recordDetailFragmentRecord: Record? = null
    var recordSearchKey: String? = null

    // DB data
    private var repository: AppRepository
    private var records: LiveData<List<Record>>
    private var reporters: LiveData<List<Reporter>>

    init {
        repository = AppRepository(application)
        records = repository.records
        reporters = repository.reporters
    }

    // Report API
    fun addRecord(record:Record) {
        repository.addRecord(record)
    }

    fun updateRecord(record: Record) {
        repository.updateRecord(record)
    }

    fun deleteRecord(record: Record) {
        repository.deleteRecord(record)
    }

    fun getAllRecords(): LiveData<List<Record>> {
        return records
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

     fun getReporter(reporterId: Int, onSuccessCallback: (reporter: Reporter) -> Unit) {
         repository.getReporter(reporterId, onSuccessCallback)
     }

    fun getAllReporters(): LiveData<List<Reporter>> {
        return reporters
    }
}