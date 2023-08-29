package com.appttude.h_mal.farmr.viewmodel

import com.appttude.h_mal.farmr.base.BaseViewModel
import com.appttude.h_mal.farmr.data.Repository
import com.appttude.h_mal.farmr.data.prefs.DATE_IN
import com.appttude.h_mal.farmr.data.prefs.DATE_OUT
import com.appttude.h_mal.farmr.data.prefs.DESCRIPTION
import com.appttude.h_mal.farmr.data.prefs.TYPE
import com.appttude.h_mal.farmr.model.FilterStore


open class ShiftViewModel(
    private val repository: Repository
) : BaseViewModel() {

    /*
     * Add Item & Further info
     */
    fun getCurrentShift(id: Long) = repository.readSingleShiftFromDatabase(id)

    /**
     * Lambda function that will invoke onError(...) on failure
     * but update live data when successful
     */
    private inline fun doTry(operation: () -> Unit) {
        try {
            operation.invoke()
        } catch (e: Exception) {
            onError(e)
        }
    }

    open fun setFiltrationDetails(
        description: String?,
        dateFrom: String?,
        dateTo: String?,
        type: String?
    ) {
        repository.setFilteringDetailsInPrefs(description, dateFrom, dateTo, type)
    }

    open fun getFiltrationDetails(): FilterStore {
        val prefs = repository.retrieveFilteringDetailsInPrefs()
        return FilterStore(
            prefs[DESCRIPTION],
            prefs[DATE_IN],
            prefs[DATE_OUT],
            prefs[TYPE]
        )
    }

}