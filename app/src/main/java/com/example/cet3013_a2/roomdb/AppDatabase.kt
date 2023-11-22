package com.example.cet3013_a2.roomdb

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Reporter::class, Record::class], version = 2, exportSchema = false)
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
                    reporterDao.deleteAllReporters()
                    reporterDao.addReporter(Reporter(
                        name = "Guardian",
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
                        .addMigrations(MIGRATION_1_2)
                        .addCallback(dbCreationCallback)
                        .build()
                }
            }
            return dbInstance
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DELETE FROM report")
                database.execSQL("DELETE FROM reporter")
                database.execSQL("DROP TABLE IF EXISTS reporter")
                database.execSQL("DROP TABLE IF EXISTS report")
                database.execSQL("CREATE TABLE IF NOT EXISTS record (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "`title` TEXT NOT NULL," +
                        "`category` TEXT NOT NULL," +
                        "`locationLat` REAL NOT NULL," +
                        "`locationLng` REAL NOT NULL," +
                        "`dateTime` TEXT NOT NULL," +
                        "`photoUrl` TEXT," +
                        "`notes` TEXT," +
                        "`reportedBy` INTEGER NOT NULL," +
                        "FOREIGN KEY (reportedBy) REFERENCES record(id)" +
                        ")")
                database.execSQL("CREATE TABLE IF NOT EXISTS reporter (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "`name` TEXT NOT NULL," +
                        "`relationship` TEXT NOT NULL" +
                        ")")
            }
        }
    }
}