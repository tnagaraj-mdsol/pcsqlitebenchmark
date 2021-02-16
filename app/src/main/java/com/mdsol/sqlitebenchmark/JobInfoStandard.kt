package com.mdsol.sqlitebenchmark

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(indices = [Index(value = ["id"])])

data class JobInfoStandard(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String? = null,
    val dateOfHire: String? = null,
    val dateOfTermination: String? = null,
    val level: Int
)

