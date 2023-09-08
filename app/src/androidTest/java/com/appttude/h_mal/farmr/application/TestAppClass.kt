package com.appttude.h_mal.farmr.application

import androidx.room.Room
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.platform.app.InstrumentationRegistry
import com.appttude.h_mal.farmr.base.BaseApplication
import com.appttude.h_mal.farmr.data.legacydb.LegacyDatabase
import com.appttude.h_mal.farmr.data.prefs.PreferenceProvider
import com.appttude.h_mal.farmr.data.room.AppDatabase
import com.appttude.h_mal.farmr.model.Shift

class TestAppClass : BaseApplication() {
    private val idlingResources = CountingIdlingResource("Data_loader")

    lateinit var database: AppDatabase
    lateinit var preferenceProvider: PreferenceProvider

    override fun onCreate() {
        super.onCreate()
        IdlingRegistry.getInstance().register(idlingResources)
    }

    override fun createDatabase(): AppDatabase {
        database = Room.inMemoryDatabaseBuilder(this, AppDatabase::class.java)
            .build()
        return database
    }

    override fun createPrefs(): PreferenceProvider {
        preferenceProvider = PreferenceProvider(this)
        return preferenceProvider
    }

    fun addToDatabase(shift: Shift) = database.getShiftDao().upsertFullShift(shift.convertToShiftEntity())
    fun addShiftsToDatabase(shifts: List<Shift>) = shifts.map { it.convertToShiftEntity() }.let { database.getShiftDao().upsertListOfFullShift(it) }
    fun clearDatabase() = database.getShiftDao().deleteAllShifts()
    fun cleanPrefs() = preferenceProvider.clearPrefs()

}