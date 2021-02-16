package com.mdsol.sqlitebenchmark


import androidx.room.TypeConverter
import java.util.*

class Converter {
    companion object {
        private val keyAndIV = CryptoUtil.getAesEncryptionKeyAndIV()

        // Conversion between String and ByteArray (Blob)
        @TypeConverter
        @JvmStatic
        fun stringToByteArray(value: String?): ByteArray? {
            if (value == null) return null
            val valueInByteArray = value.toByteArray()
            val result =
                CryptoUtil.encryptWithAES(valueInByteArray, keyAndIV.first, keyAndIV.second)
            return result.first + result.second
        }

        @TypeConverter
        @JvmStatic
        fun byteArrayToString(value: ByteArray?): String? {
            if (value == null) return null
            val decrypted = CryptoUtil.decryptWithAES(value, keyAndIV.first, keyAndIV.second)
            return String(decrypted)
        }

//        @TypeConverter
//        @JvmStatic
//        fun calendarToDatestamp(calendar: Calendar?): Long? = calendar?.timeInMillis
//
//        @TypeConverter
//        @JvmStatic
//        fun datestampToCalendar(value: Long?): Calendar? = value?.let {
//            Calendar.getInstance().apply { timeInMillis = it }
//        }
    }
}