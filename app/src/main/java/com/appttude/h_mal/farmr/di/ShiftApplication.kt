package com.appttude.h_mal.farmr.di

import com.appttude.h_mal.farmr.base.BaseApplication
import com.appttude.h_mal.farmr.data.RepositoryImpl
import com.appttude.h_mal.farmr.data.legacydb.LegacyDatabase
import com.appttude.h_mal.farmr.data.prefs.PreferenceProvider
import com.appttude.h_mal.farmr.viewmodel.ApplicationViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class ShiftApplication: BaseApplication() {

    override fun createDatabase(): LegacyDatabase {
        return LegacyDatabase(contentResolver)
    }

    override fun createPrefs() = PreferenceProvider(this)
}

