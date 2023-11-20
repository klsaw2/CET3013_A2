package com.example.cet3013_a2.roomdb

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reporter")
class Reporter(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="reporterId")
    var reporterId: Int? = null,

    @ColumnInfo(name="reporterName")
    var reporterName: String,

    @ColumnInfo(name="reporterAge")
    var reporterAge: Int,

    @ColumnInfo(name="reporterRelationship")
    var reporterRelationship: String
) {

}