package com.appttude.h_mal.farmr.di

import com.appttude.h_mal.farmr.base.BaseApplication
import com.appttude.h_mal.farmr.data.legacydb.LegacyDatabase
import com.appttude.h_mal.farmr.data.prefs.PreferenceProvider

class ShiftApplication: BaseApplication() {

    override fun createDatabase(): LegacyDatabase {
        return LegacyDatabase(contentResolver)
    }

    override fun createPrefs() = PreferenceProvider(this)
}

