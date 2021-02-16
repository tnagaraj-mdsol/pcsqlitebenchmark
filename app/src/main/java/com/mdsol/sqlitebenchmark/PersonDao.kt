package com.mdsol.sqlitebenchmark

import androidx.room.*

@Dao
interface PersonDao {
    @Query("SELECT * FROM person")
    fun getAllPersons(): List<Person>

    @Query("SELECT * FROM person WHERE id = :id")
    fun getById(id: Int): Person

    @Query("SELECT * FROM person WHERE firstName LIKE :firstName")
    fun getByFirstName(firstName: String): List<Person>

    @Query("SELECT * FROM person p JOIN ADDRESS a WHERE p.addressId = addressId AND a.zip = :zipCode")
    fun findByZip(zipCode: String): List<Person>

    @Query("SELECT * FROM person p JOIN JobInfo j WHERE p.jobId = jobId AND j.dateOfHire = :dateOfHire")
    fun findByJobId(dateOfHire: String): List<Person>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPersonList(persons: List<Person>)

    @Update
    fun updatePersons(persons: List<Person>)

    @Delete
    fun deletePersons(persons: List<Person>)

    @Query("DELETE FROM person")
    fun deleteAll()
}

@Dao
interface PersonStandardDao {
    @Query("SELECT * FROM PersonStandard")
    fun getAllPersons(): List<PersonStandard>

    @Query("SELECT * FROM PersonStandard WHERE id = :id")
    fun getById(id: Int): PersonStandard

    @Query("SELECT * FROM PersonStandard WHERE firstName LIKE :firstName")
    fun getByFirstName(firstName: String): List<PersonStandard>

    @Query("SELECT * FROM PersonStandard p JOIN AddressStandard adds WHERE p.addressId = addressId AND adds.zip = :zipCode")
    fun findByZip(zipCode: String): List<PersonStandard>

    @Query("SELECT * FROM PersonStandard p JOIN JobInfoStandard j WHERE p.jobId = jobId AND j.dateOfHire = :dateOfHire")
    fun findByJobIdStandard(dateOfHire: String): List<PersonStandard>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPersonStandardList(persons: List<PersonStandard>)

    @Update
    fun updatePersons(persons: List<PersonStandard>)

    @Delete
    fun deletePersons(persons: List<PersonStandard>)

    @Query("DELETE FROM PersonStandard")
    fun deleteAll()
}