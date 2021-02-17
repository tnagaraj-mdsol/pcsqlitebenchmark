package com.mdsol.sqlitebenchmark

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory


@Database(
    version = 1, exportSchema = true, entities = [
        PersonStandard::class,
        JobInfoStandard::class,
        AddressStandard::class
    ]
)


@TypeConverters(Converter::class)
abstract class DatabaseLevelEncryptedDB : RoomDatabase() {
    abstract fun personStandardDao(): PersonStandardDao
    abstract fun addressStandardDao(): AddressStandardDao
    abstract fun jobInfoStandardDao(): JobInfoStandardDao

    companion object {
        @Volatile
        private var databaseLevelEncryptedDB: DatabaseLevelEncryptedDB? = null
        private const val passphrase = "@veryC0mplic@tedPhra$$"

        fun getInstance(context: Context): DatabaseLevelEncryptedDB {

            return databaseLevelEncryptedDB ?: synchronized(this) {
                databaseLevelEncryptedDB ?: buildDatabase(context).also {
                    databaseLevelEncryptedDB = it
                }

            }
        }


        private fun buildDatabase(context: Context): DatabaseLevelEncryptedDB {
            val builder = Room.databaseBuilder(
                context,
                DatabaseLevelEncryptedDB::class.java, "databaseLevelEncryptedDB.sqlite3"
            )
            val supportFactory = SupportFactory(SQLiteDatabase.getBytes(passphrase.toCharArray()))
            builder.openHelperFactory(supportFactory)
            return builder.build()
        }
    }
}

