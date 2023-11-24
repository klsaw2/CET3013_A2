package com.example.cet3013_a2.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Reporter::class, Record::class], version = 3, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getRecordDao(): RecordDao
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
                    val recordDao = dbInstance!!.getRecordDao()
                    reporterDao.deleteAllReporters()
                    recordDao.deleteAllRecords()
                    reporterDao.addReporter(Reporter(
                        name = "Guardian",
                        age = 1,
                        relationship = "Guardian"
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
                        .addCallback(dbCreationCallback)
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return dbInstance
        }
    }
}