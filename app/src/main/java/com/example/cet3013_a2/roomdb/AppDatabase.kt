package com.example.cet3013_a2.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Reporter::class, Record::class], version = 1, exportSchema = false)
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
                    recordDao.deleteAllRecords()
                    reporterDao.deleteAllReporters()
                    reporterDao.addReporter(Reporter(
                        name = "Default Guardian Name",
                        age = 18,
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
                        //.addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                        .fallbackToDestructiveMigration()
                        .addCallback(dbCreationCallback)
                        .build()
                }
            }
            return dbInstance
        }

        /*private val MIGRATION_1_2 = object : Migration(1, 2) {
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
                        "FOREIGN KEY (reportedBy) REFERENCES reporter(id) ON DELETE CASCADE" +
                        ")")
                database.execSQL("CREATE TABLE IF NOT EXISTS reporter (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "`name` TEXT NOT NULL," +
                        "`relationship` TEXT NOT NULL" +
                        ")")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DELETE FROM record")
                database.execSQL("DELETE FROM reporter")
                database.execSQL("DROP TABLE IF EXISTS reporter")
                database.execSQL("DROP TABLE IF EXISTS record")
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
                        "FOREIGN KEY (reportedBy) REFERENCES reporter(id) ON DELETE CASCADE" +
                        ")")
                database.execSQL("CREATE TABLE IF NOT EXISTS reporter (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "`name` TEXT NOT NULL," +
                        "`age` INTEGER," +
                        "`relationship` TEXT NOT NULL" +
                        ")")
            }
        }*/
    }
}