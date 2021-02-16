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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {
    private var ioScope = CoroutineScope(Dispatchers.IO)

    private lateinit var columnEncryptedDB: ColumnLevelEncryptedDB
    private lateinit var dbEncryptedDB: DatabaseLevelEncryptedDB
    private var size: String? = null
    private val COLUMNTAG = "ColumnEncryptedDB"
    private val DBTAG = "EncryptedDB"


    private val db: ColumnLevelEncryptedDB
        get() {
            CryptoUtil.generateAesKeyAndIVForDataEncryption(this.applicationContext)
            return ColumnLevelEncryptedDB.getInstance(this)
        }

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
        insertData()
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

    private fun insertData() {

        // Build datasets first, then calculate SQL time

        val personList = mutableListOf<Person>()
        val personStandardList = mutableListOf<PersonStandard>()

        val addressList = mutableListOf<Address>()
        val addressStandardList = mutableListOf<AddressStandard>()

        val jobList = mutableListOf<JobInfo>()
        val jobListStandard = mutableListOf<JobInfoStandard>()

        for (i in 1..1000) {
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
            Log.i(COLUMNTAG, "INSERTION: $time ms")

            start = SystemClock.elapsedRealtime()
            dbEncryptedDB.addressStandardDao().insertStandardAddressList(addressStandardList)
            dbEncryptedDB.jobInfoStandardDao().insertJobListStandard(jobListStandard)
            dbEncryptedDB.personStandardDao().insertPersonStandardList(personStandardList)
            stop = SystemClock.elapsedRealtime()
            time = (stop - start)
            Log.i(DBTAG, "INSERTION: $time ms")
            val isActive = ioScope.isActive
        }.invokeOnCompletion { selectQueriesIndexed() }


    }

    private fun selectQueriesIndexed() {
        //At this point data has already been inserted in both DBs
        ioScope.launch {
            var start = SystemClock.elapsedRealtime()
            for (i in 1..1000) {
                columnEncryptedDB.personDao().getById(i)
            }
            var end = SystemClock.elapsedRealtime()
            var time = end - start
            Log.i(COLUMNTAG, "INDEXED SELECT QUERIES: $time ms")

            start = SystemClock.elapsedRealtime()

            for (i in 1..1000) {
                dbEncryptedDB.personStandardDao().getById(i)
            }
            end = SystemClock.elapsedRealtime()
            time = (end - start)
            Log.i(DBTAG, "INDEXED SELECT QUERIES: $time ms")
            val isActive = ioScope.isActive
        }.invokeOnCompletion { selectQueriesUnIndexed() }
    }

    private fun selectQueriesUnIndexed() {

        ioScope.launch {
            var start = SystemClock.elapsedRealtime()
            for (i in 1..1000) {
                val random = (3..10).random()
                val s = getRandomString(random)
                columnEncryptedDB.personDao().getByFirstName(s)
            }
            var end = SystemClock.elapsedRealtime()
            var time = end - start
            Log.i(COLUMNTAG, "UNINDEXED SELECT QUERIES: $time ms")

            start = SystemClock.elapsedRealtime()
            for (i in 1..1000) {
                val random = (3..10).random()
                val s = getRandomString(random)
                dbEncryptedDB.personStandardDao().getByFirstName(s)
            }
            end = SystemClock.elapsedRealtime()
            time = end - start
            Log.i(DBTAG, "UNINDEXED SELECT QUERIES: $time ms")
            val isActive = ioScope.isActive
        }.invokeOnCompletion { joinQueries() }

    }

    private fun joinQueries() {
        ioScope.launch {
            var start = SystemClock.elapsedRealtime()

            for (i in 1..1000) {
                val zip = (11111..99999).random().toString()
                columnEncryptedDB.personDao().findByZip(zip)
            }
            var end = SystemClock.elapsedRealtime()
            var time = end - start

            Log.i(COLUMNTAG, "JOIN QUERIES: $time ms")

            start = SystemClock.elapsedRealtime()

            for (i in 1..1000) {
                val zip = (11111..99999).random().toString()
                dbEncryptedDB.personStandardDao().findByZip(zip)
            }
            end = SystemClock.elapsedRealtime()
            time = end - start

            Log.i(DBTAG, "JOIN QUERIES: $time ms")


        }.invokeOnCompletion { updateQueries() }
    }


    private fun updateQueries() {
//        ioScope =
        ioScope.launch {
            // get about a 1000 random records
            val updateList = mutableListOf<Person>()
            val updateListStandard = mutableListOf<PersonStandard>()

            for (i in 1..1000) {
                val id = (1..1000).random()
                val person = columnEncryptedDB.personDao().getById(id)
                updateList.add(person)
                val personStandard = dbEncryptedDB.personStandardDao().getById(id)
                updateListStandard.add(personStandard)
            }

            var start = SystemClock.elapsedRealtime()
            columnEncryptedDB.personDao().updatePersons(updateList)
            var end = SystemClock.elapsedRealtime()
            var time = end - start
            Log.i(COLUMNTAG, "UPDATE QUERIES: $time ms")

            start = SystemClock.elapsedRealtime()
            dbEncryptedDB.personStandardDao().updatePersons(updateListStandard)
            end = SystemClock.elapsedRealtime()
            time = end - start
            Log.i(DBTAG, "UPDATE QUERIES: $time ms")
        }.invokeOnCompletion { deleteQueries() }
    }

    private fun deleteQueries() {
        // get about a 1000 random records
        val deleteList = mutableListOf<Person>()
        val deleteListStandard = mutableListOf<PersonStandard>()

        for (i in 1..1000) {
            val id = (1..1000).random()
            val person = columnEncryptedDB.personDao().getById(id)
            deleteList.add(person)
            val personStandard = dbEncryptedDB.personStandardDao().getById(id)
            deleteListStandard.add(personStandard)
        }

        var start = SystemClock.elapsedRealtime()
        columnEncryptedDB.personDao().deletePersons(deleteList)
        var end = SystemClock.elapsedRealtime()
        var time = end - start
        Log.i(COLUMNTAG, "DELETE QUERIES: $time ms")

        start = SystemClock.elapsedRealtime()
        dbEncryptedDB.personStandardDao().deletePersons(deleteListStandard)
        end = SystemClock.elapsedRealtime()
        time = end - start
        Log.i(DBTAG, "DELETE QUERIES: $time ms")
        this.runOnUiThread { Toast.makeText(this, "COMPLETED!!!", Toast.LENGTH_LONG).show() }
    }


    private fun getRandomString(length: Int): String {
        val charset = "abcdefghijklmnopqrstuvwxyz"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }


}