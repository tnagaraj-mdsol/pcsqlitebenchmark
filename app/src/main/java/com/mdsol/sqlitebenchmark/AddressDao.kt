package com.mdsol.sqlitebenchmark

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AddressDao {
    @Query("SELECT * FROM Address")
    fun getAllPersons(): List<Address>

    @Query("SELECT * FROM address WHERE id = :id")
    fun getById(id: Long): Address

    @Query("SELECT * FROM Address WHERE zip LIKE :zip")
    fun getAddressByZip(zip: String): List<Address>

    @Query("SELECT * FROM Address WHERE city LIKE :city")
    fun getAddressByCity(city: String): List<Address>

    @Query("SELECT * FROM Address WHERE city LIKE :city AND state LIKE :state ")
    fun getAddressByCityAndState(city: String, state: String): List<Address>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAddresses(vararg address: Address)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAddressList(address: List<Address>)

    @Query("DELETE FROM Address")
    fun deleteAll()
}

@Dao
interface AddressStandardDao {
    @Query("SELECT * FROM AddressStandard")
    fun getAllPersons(): List<AddressStandard>

    @Query("SELECT * FROM AddressStandard WHERE id = :id")
    fun getById(id: Long): AddressStandard

    @Query("SELECT * FROM AddressStandard WHERE zip LIKE :zip")
    fun getAddressByZip(zip: String): List<AddressStandard>

    @Query("SELECT * FROM AddressStandard WHERE city LIKE :city")
    fun getAddressByCity(city: String): List<AddressStandard>

    @Query("SELECT * FROM AddressStandard WHERE city LIKE :city AND state LIKE :state ")
    fun getAddressByCityAndState(city: String, state: String): List<AddressStandard>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStandardAddresses(vararg address: AddressStandard)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStandardAddressList(address: List<AddressStandard>)

    @Query("DELETE FROM AddressStandard")
    fun deleteAll()
}