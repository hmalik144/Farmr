package com.appttude.h_mal.farmr.application

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.platform.app.InstrumentationRegistry
import com.appttude.h_mal.farmr.base.BaseApplication
import com.appttude.h_mal.farmr.data.legacydb.LegacyDatabase
import com.appttude.h_mal.farmr.data.prefs.PreferenceProvider
import com.appttude.h_mal.farmr.model.Shift

class TestAppClass : BaseApplication() {
    private val idlingResources = CountingIdlingResource("Data_loader")

    lateinit var database: LegacyDatabase
    lateinit var preferenceProvider: PreferenceProvider

    override fun onCreate() {
        super.onCreate()
        IdlingRegistry.getInstance().register(idlingResources)
    }

    override fun createDatabase(): LegacyDatabase {
        database =
            LegacyDatabase(InstrumentationRegistry.getInstrumentation().context.contentResolver)
        return database
    }

    override fun createPrefs(): PreferenceProvider {
        preferenceProvider = PreferenceProvider(this)
        return preferenceProvider
    }

    fun addToDatabase(shift: Shift) = database.insertShiftDataIntoDatabase(shift)
    fun addShiftsToDatabase(shifts: List<Shift>) = shifts.forEach { addToDatabase(it) }
    fun clearDatabase() = database.deleteAllShiftsInDatabase()
    fun cleanPrefs() = preferenceProvider.clearPrefs()

}