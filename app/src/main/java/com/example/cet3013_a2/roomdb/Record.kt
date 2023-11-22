package com.example.cet3013_a2.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(
    tableName = "record",
    foreignKeys =
        [ForeignKey(
            entity = Reporter::class,
            childColumns = arrayOf("reportedBy"),
            parentColumns =  arrayOf("reporterId"),
            onDelete = ForeignKey.CASCADE
        )]
)
class Record(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    var id: Int? = null,

    @ColumnInfo(name="title")
    var title: String,

    @ColumnInfo(name="category")
    var category: String,

    @ColumnInfo(name="createdAt")
    var createdAt: Calendar,

    @ColumnInfo(name="photoUrl")
    var photoUrl: String? = null,

    @ColumnInfo(name="notes")
    var notes: String? = null,

    @ColumnInfo(name="reportedBy")
    var reportedBy: Int
) {
}