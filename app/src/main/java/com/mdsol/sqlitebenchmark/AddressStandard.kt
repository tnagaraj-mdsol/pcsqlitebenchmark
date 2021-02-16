package com.mdsol.sqlitebenchmark

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["id"]), Index(value = ["zip"])])
data class AddressStandard(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val address1: String? = null,
    val address2: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null
)

