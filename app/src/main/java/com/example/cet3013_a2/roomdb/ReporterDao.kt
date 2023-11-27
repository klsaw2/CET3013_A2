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

    @Query("SELECT * FROM reporter WHERE id=:reporterId")
    fun getReporter(reporterId: Int): Reporter

    @Query("SELECT COUNT(*) FROM reporter")
    fun getReporterCount(): Int

    @Query("DELETE FROM reporter")
    fun deleteAllReporters()

    @Query("SELECT * FROM reporter WHERE id = :id")
    fun getReporterById(id: Int): LiveData<List<Reporter>>
}