package com.example.cet3013_a2.roomdb

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppRepository (application: Application) {
    private var recordDao: RecordDao
    private var reporterDao: ReporterDao
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    var records: LiveData<List<Record>>
    var reporters: LiveData<List<Reporter>>

    init {
        val db = AppDatabase.getDatabase(application)
        recordDao = db!!.getRecordDao()
        reporterDao = db.getReporterDao()
        records = recordDao.getAllRecords()
        reporters = reporterDao.getAllReporters()
    }

    // Record async API
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

    fun deleteAllRecords() {
        coroutineScope.launch(Dispatchers.IO) {
            recordDao.deleteAllRecords()
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
            reporterDao.updateReporter(reporter)
        }
    }

    fun deleteReporter(reporter: Reporter) {
        coroutineScope.launch(Dispatchers.IO) {
            reporterDao.deleteReporter(reporter)
        }
    }
}