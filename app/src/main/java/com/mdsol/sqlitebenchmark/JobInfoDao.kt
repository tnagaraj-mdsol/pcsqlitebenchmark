package com.mdsol.sqlitebenchmark

import androidx.room.*

@Dao
interface JobInfoDao {
    @Query("SELECT * FROM JobInfo")
    fun getAllJobs(): List<JobInfo>

    @Query("SELECT * FROM JobInfo WHERE id = :id")
    fun getById(id: Long): JobInfo

    @Query("SELECT * FROM JobInfo WHERE title LIKE :title")
    fun getByTitle(title: String): List<JobInfo>

    @Query("SELECT * FROM JobInfo WHERE dateOfHire LIKE :dateOfHire")
    fun getByDateOfHire(dateOfHire: String): List<JobInfo>

    @Query("SELECT * FROM JobInfo WHERE dateOfTermination LIKE :dateOfTerm")
    fun getByDateOfTermination(dateOfTerm: String): List<JobInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertJobs(vararg address: JobInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertJobList(jobList: List<JobInfo>)

    @Query("DELETE FROM JobInfo")
    fun deleteAll()
}

@Dao
interface JobInfoStandardDao {
    @Query("SELECT * FROM JobInfoStandard")
    fun getAllJobsStandard(): List<JobInfoStandard>

    @Query("SELECT * FROM JobInfoStandard WHERE id = :id")
    fun getById(id: Long): JobInfoStandard

    @Query("SELECT * FROM JobInfoStandard WHERE title LIKE :title")
    fun getByTitle(title: String): List<JobInfoStandard>

    @Query("SELECT * FROM JobInfoStandard WHERE dateOfHire LIKE :dateOfHire")
    fun getByDateOfHire(dateOfHire: String): List<JobInfoStandard>

    @Query("SELECT * FROM JobInfoStandard WHERE dateOfTermination LIKE :dateOfTerm")
    fun getByDateOfTermination(dateOfTerm: String): List<JobInfoStandard>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertJobsStandard(vararg jobsStandard: JobInfoStandard)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertJobListStandard(jobsStandard: List<JobInfoStandard>)

    @Query("DELETE FROM JobInfoStandard")
    fun deleteAll()

}