package com.example.cet3013_a2.roomdb

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppRepository (application: Application) {
    private var recordDao: RecordDao
    private var reporterDao: ReporterDao
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    var records: LiveData<List<Record>>
    var reporters: LiveData<List<Reporter>>

    init {
        val db = AppDatabase.getDatabase(application)
        recordDao = db!!.getRecordDao()
        reporterDao = db.getReporterDao()
        records = recordDao.getAllRecords()
        reporters = reporterDao.getAllReporters()
    }

    // Record async CUD ops
    fun addRecord(record: Record) {
        coroutineScope.launch(Dispatchers.IO) {
            recordDao.addRecord(record)
        }
    }

    fun updateRecord(record: Record) {
        coroutineScope.launch(Dispatchers.IO) {
            recordDao.updateRecord(record)
        }
    }

    fun deleteRecord(record: Record) {
        coroutineScope.launch(Dispatchers.IO) {
            recordDao.deleteRecord(record)
        }
    }

    fun getRecordById(recordId: Int): LiveData<List<Record>> {
        return recordDao.getRecord(recordId)
    }

    fun deleteAllRecords() {
        coroutineScope.launch(Dispatchers.IO) {
            recordDao.deleteAllRecords()
        }
    }

    // Reporter async CUD ops
    fun addReporter(reporter: Reporter) {
        coroutineScope.launch(Dispatchers.IO) {
            reporterDao.addReporter(reporter)
        }
    }

    fun updateReporter(reporter: Reporter) {
        coroutineScope.launch(Dispatchers.IO) {
            reporterDao.updateReporter(reporter)
        }
    }

    fun deleteReporter(reporter: Reporter) {
        coroutineScope.launch(Dispatchers.IO) {
            reporterDao.deleteReporter(reporter)
        }
    }

    fun getReporterWithSuccessCallback(reporterId: Int, onSuccessCallback: (reporter: Reporter) -> Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            val mReporter = reporterDao.getReporter(reporterId)
            onSuccessCallback(mReporter)
        }
    }

    // Get reporter by id
    fun getReporterById(id: Int): LiveData<List<Reporter>> {
        return reporterDao.getReporterById(id)
    }
}