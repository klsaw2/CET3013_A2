package com.example.cet3013_a2.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ReporterDao {
    @Insert
    fun addReporter(report: Reporter)

    @Update
    fun updateReporter(report: Reporter)

    @Delete
    fun deleteReporter(report: Reporter)

    @Query("SELECT * FROM reporter")
    fun getAllReporters(): LiveData<List<Reporter>>

    @Query("DELETE FROM reporter")
    fun deleteAllReporters()
}