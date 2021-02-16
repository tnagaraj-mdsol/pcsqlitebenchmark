package com.mdsol.sqlitebenchmark

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    version = 1, exportSchema = true, entities = [
        Person::class,
        JobInfo::class,
        Address::class
    ]
)


@TypeConverters(Converter::class)
abstract class ColumnLevelEncryptedDB : RoomDatabase() {
    abstract fun personDao(): PersonDao
    abstract fun addressDao(): AddressDao
    abstract fun jobInfoDao(): JobInfoDao

    companion object {
        @Volatile
        private var columnLevelEncryptedDB: ColumnLevelEncryptedDB? = null

        fun getInstance(context: Context): ColumnLevelEncryptedDB {

            return columnLevelEncryptedDB ?: synchronized(this) {
                columnLevelEncryptedDB ?: buildDatabase(context).also {
                    columnLevelEncryptedDB = it
                }

            }
        }


        private fun buildDatabase(context: Context): ColumnLevelEncryptedDB {
            val builder = Room.databaseBuilder(
                context,
                ColumnLevelEncryptedDB::class.java, "columnLevelEncryptedDB.sqlite3"
            )
            return builder.build()
        }
    }
}

