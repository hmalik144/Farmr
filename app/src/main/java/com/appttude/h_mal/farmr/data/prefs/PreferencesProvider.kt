package com.appttude.h_mal.farmr.data.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.appttude.h_mal.farmr.model.Order
import com.appttude.h_mal.farmr.model.Sortable

/**
 * Shared preferences to save & load last timestamp
 */
const val SORT = "SORT"
const val ORDER = "ORDER"

const val DESCRIPTION = "DESCRIPTION"
const val DATE_IN = "TIME_IN"
const val DATE_OUT = "TIME_OUT"
const val TYPE = "TYPE"

class PreferenceProvider(
    context: Context
) {

    private val appContext = context.applicationContext

    private val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun saveSortableAndOrder(sortable: Sortable, order: Order) {
        preference.edit()
            .putString(SORT, sortable.label)
            .putString(ORDER, order.label)
            .apply()
    }

    fun getSortableAndOrder(): Pair<Sortable?, Order?> {
        val sort = preference.getString(SORT, null)?.let {  Sortable.valueOf(it) }
        val order = preference.getString(ORDER, null)?.let {  Order.valueOf(it) }

        return Pair(sort, order)
    }
    fun saveFilteringDetails(
        description: String?,
        timeIn: String?,
        timeOut: String?,
        type: String?
    ): Boolean {
        return preference.edit()
            .putString(DESCRIPTION, description)
            .putString(DATE_IN, timeIn)
            .putString(DATE_OUT, timeOut)
            .putString(TYPE, type)
            .commit()
    }

    fun getFilteringDetails(): Map<String, String?> {
        return mapOf(
            Pair(DESCRIPTION, preference.getString(DESCRIPTION, null)),
            Pair(DATE_IN, preference.getString(DATE_IN, null)),
            Pair(DATE_OUT, preference.getString(DATE_OUT, null)),
            Pair(TYPE, preference.getString(TYPE, null))
        )
    }

    fun clearPrefs() {
        preference.edit().clear().apply()
    }

}