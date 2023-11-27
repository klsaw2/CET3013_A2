package com.example.cet3013_a2.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecordDao {
    @Insert
    fun addRecord(record: Record)

    @Update
    fun updateRecord(record: Record)

    @Delete
    fun deleteRecord(record: Record)

    @Query("SELECT * FROM record WHERE id=:recordId")
    fun getRecord(recordId: Int): LiveData<List<Record>>

    @Query("SELECT * FROM record")
    fun getAllRecords(): LiveData<List<Record>>

    @Query("DELETE FROM record")
    fun deleteAllRecords()
}