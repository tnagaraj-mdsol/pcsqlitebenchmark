package com.mdsol.sqlitebenchmark

import androidx.room.*

@Dao
interface PersonDao {
    @Query("SELECT * FROM person")
    fun getAll(): List<Person>

    @Query("SELECT * FROM person WHERE id = :id")
    fun getById(id: Int): Person

    @Query("SELECT * FROM person WHERE firstName = :firstName")
    fun getByFirstName(firstName: String): List<Person>

    @Query("SELECT * FROM person p JOIN ADDRESS a WHERE p.addressId = a.id AND a.zip = :zipCode")
    fun findByZip(zipCode: String): List<Person>

    @Query("SELECT * FROM person p JOIN ADDRESS a WHERE p.addressId = a.id AND a.id = :id")
    fun findByAddressId(id: Int): List<Person>

    @Query("SELECT * FROM person p JOIN JobInfo j WHERE p.jobId = j.id AND j.title = :title")
    fun findByJobTitle(title: String): List<Person>

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
    fun getAll(): List<PersonStandard>

    @Query("SELECT * FROM PersonStandard WHERE id = :id")
    fun getById(id: Int): PersonStandard

    @Query("SELECT * FROM PersonStandard WHERE firstName = :firstName")
    fun getByFirstName(firstName: String): List<PersonStandard>

    @Query("SELECT * FROM PersonStandard p JOIN AddressStandard a WHERE p.addressId = a.id AND a.id = :id")
    fun findByAddressId(id: Int): List<PersonStandard>

    @Query("SELECT * FROM PersonStandard ps JOIN AddressStandard adds WHERE ps.addressId = adds.id AND adds.zip = :zipCode")
    fun findByZip(zipCode: String): List<PersonStandard>

    @Query("SELECT * FROM PersonStandard p JOIN JobInfoStandard j WHERE p.jobId = jobId AND j.title = :title")
    fun findByJobTitleStandard(title: String): List<PersonStandard>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPersonStandardList(persons: List<PersonStandard>)

    @Update
    fun updatePersons(persons: List<PersonStandard>)

    @Delete
    fun deletePersons(persons: List<PersonStandard>)

    @Query("DELETE FROM PersonStandard")
    fun deleteAll()
}