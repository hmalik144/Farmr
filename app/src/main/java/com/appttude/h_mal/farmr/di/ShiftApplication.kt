package com.appttude.h_mal.farmr.di

import androidx.room.RoomDatabase
import com.appttude.h_mal.farmr.base.BaseApplication
import com.appttude.h_mal.farmr.data.legacydb.LegacyDatabase
import com.appttude.h_mal.farmr.data.prefs.PreferenceProvider
import com.appttude.h_mal.farmr.data.room.AppDatabase

class ShiftApplication: BaseApplication() {

    override fun createDatabase(): AppDatabase {
        return AppDatabase(this)
    }

    override fun createPrefs() = PreferenceProvider(this)
}

