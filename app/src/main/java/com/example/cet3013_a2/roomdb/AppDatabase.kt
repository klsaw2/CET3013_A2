package com.example.cet3013_a2.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Reporter::class, Report::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getReportDao(): ReportDao
    abstract fun getReporterDao(): ReporterDao

    companion object {
        @Volatile
        private var dbInstance: AppDatabase? = null
        private val coroutineScope = CoroutineScope(Dispatchers.IO)

        val dbCreationCallback = object : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)

                coroutineScope.launch {
                    val reporterDao = dbInstance!!.getReporterDao()
                    reporterDao.deleteAllReporters()
                    reporterDao.addReporter(Reporter(
                        reporterName = "Guardian",
                        reporterAge = 42,
                        reporterRelationship = "Guardian"
                    ))
                }
            }
        }

        fun getDatabase(context: Context): AppDatabase? {
            if (dbInstance == null) {
                synchronized(AppDatabase::class.java) {
                    dbInstance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "app_database"
                    )
                        .fallbackToDestructiveMigration()
                        .addCallback(dbCreationCallback)
                        .build()
                }
            }
            return dbInstance
        }
    }
}