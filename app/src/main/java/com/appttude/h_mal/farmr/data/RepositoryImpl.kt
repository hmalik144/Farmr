package com.appttude.h_mal.farmr.data

import androidx.lifecycle.LiveData
import androidx.room.Room
import com.appttude.h_mal.farmr.data.legacydb.LegacyDatabase
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract
import com.appttude.h_mal.farmr.data.prefs.PreferenceProvider
import com.appttude.h_mal.farmr.data.room.AppDatabase
import com.appttude.h_mal.farmr.data.room.entity.ShiftEntity
import com.appttude.h_mal.farmr.model.Order
import com.appttude.h_mal.farmr.model.Shift
import com.appttude.h_mal.farmr.model.Sortable
import com.appttude.h_mal.farmr.utils.dateStringIsValid
import com.appttude.h_mal.farmr.utils.timeStringIsValid

class RepositoryImpl(
    roomDatabase: AppDatabase,
    private val preferenceProvider: PreferenceProvider
): Repository {

    private val shiftDao = roomDatabase.getShiftDao()

    override fun insertShiftIntoDatabase(shift: Shift): Boolean {
        val shiftEntity = shift.convertToShiftEntity()
        return shiftDao.upsertFullShift(shiftEntity) > 0
    }

    override fun updateShiftIntoDatabase(id: Long, shift: Shift): Boolean {
        if (shift.description.isBlank() || shift.description.trim().length < 3) {
            throw IllegalArgumentException("description required")
        }
        if (!shift.date.dateStringIsValid()) {
                throw IllegalArgumentException("date required")
        }
        shift.timeIn?.takeIf { !it.timeStringIsValid() }?.let {
            throw IllegalArgumentException("time in required")
        }
        shift.timeOut?.takeIf { !it.timeStringIsValid() }?.let {
            throw IllegalArgumentException("time out required")
        }
        shift.breakMins?.takeIf { it < 0 }?.let {
            throw IllegalArgumentException("break required")
        }
        if (shift.timeIn != null || shift.timeOut != null) {
            if (shift.duration == null) throw IllegalArgumentException("Duration required")
        }
        shift.units?.takeIf { it < 0 }?.let {
            throw IllegalArgumentException("Units required")
        }
        if (shift.rateOfPay < 0) {
            throw IllegalArgumentException("Rate of pay required")
        }
        if (shift.totalPay < 0) {
            throw IllegalArgumentException("Total pay required")
        }
        val shiftEntity = shift.convertToShiftEntity(id)
        return shiftDao.upsertFullShift(shiftEntity) > 0
    }

    override fun readShiftsFromDatabase(): LiveData<List<ShiftEntity>> {
        return shiftDao.getAllFullShift()
    }

    override fun readSingleShiftFromDatabase(id: Long): ShiftEntity? {
        return shiftDao.getCurrentFullShiftSingle(id)
    }

    override fun deleteSingleShiftFromDatabase(id: Long): Boolean {
        return shiftDao.deleteShift(id) == 1
    }

    override fun deleteAllShiftsFromDatabase(): Boolean {
        return shiftDao.deleteAllShifts() > 0
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