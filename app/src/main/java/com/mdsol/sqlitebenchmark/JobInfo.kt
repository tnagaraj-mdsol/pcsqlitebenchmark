package com.mdsol.sqlitebenchmark

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*


@Entity(indices = [Index(value = ["id"])])

data class JobInfo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val title: String? = null,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val dateOfHire: String?  = null,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val dateOfTermination: String? = null,
    val level: Int
)
