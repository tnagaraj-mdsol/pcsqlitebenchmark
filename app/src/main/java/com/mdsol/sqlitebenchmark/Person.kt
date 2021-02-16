package com.mdsol.sqlitebenchmark

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(indices = [Index(value = ["id"])])
data class Person(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val firstName: String? = null,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val lastName: String? = null,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val dob: String? = null,
    val addressId: Int? = null,
    val jobId: Int? = null
)

