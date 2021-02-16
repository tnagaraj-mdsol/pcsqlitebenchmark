package com.mdsol.sqlitebenchmark

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["id"]), Index(value = ["zip"])])
data class Address(
    @PrimaryKey val id: Int,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val address1: String? = null,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val address2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null
)
