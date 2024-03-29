package com.appttude.h_mal.farmr.data

import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.model.Order
import com.appttude.h_mal.farmr.model.Shift
import com.appttude.h_mal.farmr.model.Sortable

interface Repository {
    fun insertShiftIntoDatabase(shift: Shift): Boolean
    fun updateShiftIntoDatabase(id: Long, shift: Shift): Boolean
    fun readShiftsFromDatabase(): List<ShiftObject>?
    fun readSingleShiftFromDatabase(id: Long): ShiftObject?
    fun deleteSingleShiftFromDatabase(id: Long): Boolean
    fun deleteAllShiftsFromDatabase(): Boolean
    fun retrieveSortAndOrderFromPref(): Pair<Sortable?, Order?>
    fun setSortAndOrderFromPref(sortable: Sortable, order: Order)
    fun retrieveFilteringDetailsInPrefs(): Map<String, String?>
    fun setFilteringDetailsInPrefs(
        description: String?,
        timeIn: String?,
        timeOut: String?,
        type: String?
    ): Boolean
}