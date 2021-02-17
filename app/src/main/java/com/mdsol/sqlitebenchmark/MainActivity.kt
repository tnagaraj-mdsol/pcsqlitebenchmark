package com.mdsol.sqlitebenchmark

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val job: Job = SupervisorJob()
    private var ioScope = CoroutineScope(Dispatchers.IO + job)

    private lateinit var columnEncryptedDB: ColumnLevelEncryptedDB
    private lateinit var dbEncryptedDB: DatabaseLevelEncryptedDB
    private var size: String? = null
    private val COLUMNTAG = "ColumnEncryptedDB"
    private val DBTAG = "EncryptedDB"


//    private val db: ColumnLevelEncryptedDB
//        get() {
//            CryptoUtil.generateAesKeyAndIVForDataEncryption(this.applicationContext)
//            return ColumnLevelEncryptedDB.getInstance(this)
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "$size", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        ioScope = CoroutineScope(Dispatchers.IO)
        createDatabases()
        runAllQueries()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createDatabases() {
        CryptoUtil.generateAesKeyAndIVForDataEncryption(this.applicationContext)
        columnEncryptedDB = ColumnLevelEncryptedDB.getInstance(this)
        dbEncryptedDB = DatabaseLevelEncryptedDB.getInstance(this)
    }

    private fun runAllQueries() {

        // Build datasets first, then calculate SQL time

        val personList = mutableListOf<Person>()
        val personStandardList = mutableListOf<PersonStandard>()

        val addressList = mutableListOf<Address>()
        val addressStandardList = mutableListOf<AddressStandard>()

        val jobList = mutableListOf<JobInfo>()
        val jobListStandard = mutableListOf<JobInfoStandard>()

        for (i in 1..10000) {
            // Generate random values
            val firstName = getRandomString(10)
            val lastName = getRandomString(10)

            val title = getRandomString(5)

            val address1 = getRandomString(25)
            val address2 = getRandomString(35)
            val city = getRandomString(12)
            val state = getRandomString(2)

            val zip = (11111..99999).random().toString()
            val dob = Calendar.getInstance().toString()
            val dateOfHire = Calendar.getInstance().toString()
            val dateOfTerm = Calendar.getInstance().toString()

            personList.add(Person(i, firstName, lastName, dob, i, i))
            personStandardList.add(PersonStandard(i, firstName, lastName, dob, i, i))

            jobList.add(JobInfo(i, title, dateOfHire, dateOfTerm, i % 3))
            jobListStandard.add(JobInfoStandard(i, title, dateOfHire, dateOfTerm, i % 3))

            addressList.add(Address(i, address1, address2, city, state, zip))
            addressStandardList.add(AddressStandard(i, address1, address2, city, state, zip))
        }

        ioScope.launch {

            var start = SystemClock.elapsedRealtime()
            columnEncryptedDB.jobInfoDao().insertJobList(jobList)
            columnEncryptedDB.addressDao().insertAddressList(addressList)
            columnEncryptedDB.personDao().insertPersonList(personList)

            var stop = SystemClock.elapsedRealtime()
            var time = (stop - start)
            Log.i(COLUMNTAG, "INSERT QUERY: $time ms")

            start = SystemClock.elapsedRealtime()
            dbEncryptedDB.addressStandardDao().insertStandardAddressList(addressStandardList)
            dbEncryptedDB.jobInfoStandardDao().insertJobListStandard(jobListStandard)
            dbEncryptedDB.personStandardDao().insertPersonStandardList(personStandardList)
            stop = SystemClock.elapsedRealtime()
            time = (stop - start)
            Log.i(DBTAG, "INSERT QUERY: $time ms")


            //SELECT INDEXED
            start = SystemClock.elapsedRealtime()
            for (i in 1..5000) {
                columnEncryptedDB.personDao().getById(i)
            }
            stop = SystemClock.elapsedRealtime()
            time = stop - start
            Log.i(COLUMNTAG, "INDEXED SELECT QUERIES: $time ms")

            start = SystemClock.elapsedRealtime()

            for (i in 1..5000) {
                dbEncryptedDB.personStandardDao().getById(i)
            }
            stop = SystemClock.elapsedRealtime()
            time = (stop - start)
            Log.i(DBTAG, "INDEXED SELECT QUERIES: $time ms")


            // SELECT UNINDEXED
            val randomPersonList = mutableListOf<Person>()
            val randomPersonListStandard = mutableListOf<PersonStandard>()
            val random = Random()

            // Populate list with random data used to construct the db
            for (i in 0..999) {
                val randomPerson = personList[random.nextInt(personList.size)]
                val randomPersonStandard =
                    personStandardList[random.nextInt(personStandardList.size)]
                randomPersonList.add(randomPerson)
                randomPersonListStandard.add(randomPersonStandard)
            }
            start = SystemClock.elapsedRealtime()
            for (randomPerson in randomPersonList) {
                val person = columnEncryptedDB.personDao().getByFirstName(randomPerson.firstName!!)
            }


            stop = SystemClock.elapsedRealtime()
            time = stop - start
            Log.i(COLUMNTAG, "UNINDEXED SELECT QUERIES: $time ms")

            start = SystemClock.elapsedRealtime()
            for (randomPerson in randomPersonListStandard) {
                val person =
                    dbEncryptedDB.personStandardDao().getByFirstName(randomPerson.firstName!!)
            }
            stop = SystemClock.elapsedRealtime()
            time = stop - start
            Log.i(DBTAG, "UNINDEXED SELECT QUERIES: $time ms")


            //JOIN ON UNINDEXED
            val zipList = mutableListOf<String>()

            for (i in 0..999) {
                val zip = (11111..99999).random().toString()
                zipList.add(zip)
            }
            start = SystemClock.elapsedRealtime()

            for (zip in zipList) {
                columnEncryptedDB.personDao().findByZip(zip)
            }
            stop = SystemClock.elapsedRealtime()
            time = stop - start

            Log.i(COLUMNTAG, "JOIN ON UNINDEXED QUERIES: $time ms")

            start = SystemClock.elapsedRealtime()

            for (zip in zipList) {
                dbEncryptedDB.personStandardDao().findByZip(zip)
            }
            stop = SystemClock.elapsedRealtime()
            time = stop - start

            Log.i(DBTAG, "JOIN ON UNINDEXED QUERIES: $time ms")


            // JOIN ON INDEXED

            for (i in 0..999) {
                start = SystemClock.elapsedRealtime()
                columnEncryptedDB.personDao().findByAddressId(i)
                stop = SystemClock.elapsedRealtime()
            }

            time = stop - start

            Log.i(COLUMNTAG, "JOIN ON INDEXED QUERIES: $time ms")


            start = SystemClock.elapsedRealtime()

            for (i in 0..999) {
                start = SystemClock.elapsedRealtime()
                columnEncryptedDB.personDao().findByAddressId(i)
                stop = SystemClock.elapsedRealtime()
            }
            time = stop - start

            Log.i(DBTAG, "JOIN ON INDEXED QUERIES: $time ms")


            //UPDATE
            start = SystemClock.elapsedRealtime()
            columnEncryptedDB.personDao().updatePersons(randomPersonList)
            stop = SystemClock.elapsedRealtime()
            time = stop - start
            Log.i(COLUMNTAG, "UPDATE QUERIES: $time ms")

            start = SystemClock.elapsedRealtime()
            dbEncryptedDB.personStandardDao().updatePersons(randomPersonListStandard)
            stop = SystemClock.elapsedRealtime()
            time = stop - start
            Log.i(DBTAG, "UPDATE QUERIES: $time ms")

            //DELETE WITH CLAUSE
            start = SystemClock.elapsedRealtime()
            columnEncryptedDB.personDao().deletePersons(randomPersonList)
            stop = SystemClock.elapsedRealtime()
            time = stop - start
            Log.i(COLUMNTAG, "DELETE QUERIES: $time ms")

            start = SystemClock.elapsedRealtime()
            dbEncryptedDB.personStandardDao().deletePersons(randomPersonListStandard)
            stop = SystemClock.elapsedRealtime()
            time = stop - start
            Log.i(DBTAG, "DELETE QUERIES: $time ms")


            //SELECT ALL
            start = SystemClock.elapsedRealtime()
            val list = columnEncryptedDB.personDao().getAll()
            stop = SystemClock.elapsedRealtime()
            time = stop - start
            Log.i(COLUMNTAG, "SELECT ALL QUERY: $time ms")

            start = SystemClock.elapsedRealtime()
            val listStandard = dbEncryptedDB.personStandardDao().getAll()
            stop = SystemClock.elapsedRealtime()
            time = stop - start
            Log.i(DBTAG, "SELECT ALL QUERY: $time ms")


            //DELETE ALL
            start = SystemClock.elapsedRealtime()
            columnEncryptedDB.personDao().deleteAll()
            stop = SystemClock.elapsedRealtime()
            time = stop - start
            Log.i(COLUMNTAG, "DELETE ALL QUERY: $time ms")

            start = SystemClock.elapsedRealtime()
            dbEncryptedDB.personStandardDao().deleteAll()
            stop = SystemClock.elapsedRealtime()
            time = stop - start
            Log.i(DBTAG, "DELETE ALL QUERY: $time ms")
        }
    }

    private fun getRandomString(length: Int): String {
        val charset = "abcdefghijklmnopqrstuvwxyz"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }


}