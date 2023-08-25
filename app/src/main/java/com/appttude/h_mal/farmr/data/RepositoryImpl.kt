package com.appttude.h_mal.farmr.data

import com.appttude.h_mal.farmr.data.legacydb.LegacyDatabase
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.data.prefs.PreferenceProvider
import com.appttude.h_mal.farmr.model.Order
import com.appttude.h_mal.farmr.model.Shift
import com.appttude.h_mal.farmr.model.Sortable

class RepositoryImpl(
    private val legacyDatabase: LegacyDatabase,
    private val preferenceProvider: PreferenceProvider
): Repository {
    override fun insertShiftIntoDatabase(shift: Shift): Boolean {
        return legacyDatabase.insertShiftDataIntoDatabase(shift) != null
    }

    override fun updateShiftIntoDatabase(id: Long, shift: Shift): Boolean {
        return legacyDatabase.updateShiftDataIntoDatabase(
            id = id,
            typeString = shift.type.type,
            descriptionString = shift.description,
            dateString = shift.date,
            timeInString = shift.timeIn ?: "",
            timeOutString = shift.timeOut ?: "",
            duration = shift.duration ?: 0f,
            breaks = shift.breakMins ?: 0,
            units = shift.units ?: 0f,
            payRate = shift.rateOfPay,
            totalPay = shift.totalPay
        ) == 1
    }

    override fun readShiftsFromDatabase(): List<ShiftObject>? {
        return legacyDatabase.readShiftsFromDatabase()
    }

    override fun readSingleShiftFromDatabase(id: Long): ShiftObject? {
        return legacyDatabase.readSingleShiftWithId(id)
    }

    override fun deleteSingleShiftFromDatabase(id: Long): Boolean {
        return legacyDatabase.deleteSingleShift(id) == 1
    }

    override fun deleteAllShiftsFromDatabase(): Boolean {
        return legacyDatabase.deleteAllShiftsInDatabase() > 0
    }

    override fun retrieveSortAndOrderFromPref(): Pair<Sortable?, Order?> {
        return preferenceProvider.getSortableAndOrder()
    }

    override fun setSortAndOrderFromPref(sortable: Sortable, order: Order) {
        preferenceProvider.saveSortableAndOrder(sortable, order)
    }

    override fun retrieveFilteringDetailsInPrefs(): Map<String, String?> {
        return preferenceProvider.getFilteringDetails()
    }

    override fun setFilteringDetailsInPrefs(
        description: String?,
        timeIn: String?,
        timeOut: String?,
        type: String?
    ) {
        preferenceProvider.saveFilteringDetails(description, timeIn, timeOut, type)
    }

}