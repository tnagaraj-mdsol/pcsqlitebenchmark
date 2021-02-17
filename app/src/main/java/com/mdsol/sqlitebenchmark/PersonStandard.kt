package com.mdsol.sqlitebenchmark

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["id"])],
    foreignKeys = [
        ForeignKey(
            entity = AddressStandard::class,
            parentColumns = ["id"],
            childColumns = ["addressId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = JobInfoStandard::class,
            parentColumns = ["id"],
            childColumns = ["jobId"],
            onDelete = ForeignKey.CASCADE
        )

    ]
)
data class PersonStandard(
    @PrimaryKey val id: Int,
    val firstName: String? = null,
    val lastName: String? = null,
    val dob: String? = null,
    val addressId: Int?,
    val jobId: Int?

)
