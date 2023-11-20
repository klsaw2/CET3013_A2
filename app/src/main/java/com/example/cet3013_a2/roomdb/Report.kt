package com.example.cet3013_a2.roomdb

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "report",
    foreignKeys =
        [ForeignKey(
            entity = Reporter::class,
            childColumns = arrayOf("reportedBy"),
            parentColumns =  arrayOf("reporterId"),
            onDelete = ForeignKey.CASCADE
        )]
)
class Report(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="reportId")
    var reportId: Int? = null,

    @ColumnInfo(name="reportTitle")
    var title: String,

    @ColumnInfo(name="reportCategory")
    var category: String,

    @ColumnInfo(name="reportDate")
    var dateTime: String,

    @ColumnInfo(name="reportPhoto")
    var photoUrl: String? = null,

    @ColumnInfo(name="reportNotes")
    var notes: String? = null,

    @ColumnInfo(name="reportedBy")
    var reportedBy: Int
) {
}