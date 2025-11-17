package com.momotracker.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun txDao(): TxDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val passphrase = "momo-secret-2025".toCharArray()
                val factory = SupportFactory(SQLiteDatabase.getBytes(passphrase))
                Room.databaseBuilder(context.applicationContext,
                    AppDatabase::class.java, "momo.db")
                    .openHelperFactory(factory)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
