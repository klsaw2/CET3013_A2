package com.example.cet3013_a2.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ReportDao {
    @Insert
    fun addReport(report: Report)

    @Update
    fun updateReport(report: Report)

    @Delete
    fun deleteReport(report: Report)

    @Query("SELECT * FROM report")
    fun getAllReports(): LiveData<List<Report>>
}