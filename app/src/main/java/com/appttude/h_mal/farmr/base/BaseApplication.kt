package com.appttude.h_mal.farmr.base

import android.app.Application
import com.appttude.h_mal.farmr.data.RepositoryImpl
import com.appttude.h_mal.farmr.data.legacydb.LegacyDatabase
import com.appttude.h_mal.farmr.data.prefs.PreferenceProvider
import com.appttude.h_mal.farmr.data.room.AppDatabase
import com.appttude.h_mal.farmr.viewmodel.ApplicationViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

abstract class BaseApplication() : Application(), KodeinAware {

    // Kodein creation of modules to be retrieve within the app
    override val kodein = Kodein.lazy {
        import(androidXModule(this@BaseApplication))

        bind() from singleton { createDatabase() }
        bind() from singleton { createPrefs() }
        bind() from singleton { RepositoryImpl(instance(), instance()) }

        bind() from provider { ApplicationViewModelFactory(instance()) }
    }

    abstract fun createDatabase(): AppDatabase
    abstract fun createPrefs(): PreferenceProvider
}