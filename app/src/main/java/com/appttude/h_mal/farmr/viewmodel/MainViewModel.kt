package com.appttude.h_mal.farmr.viewmodel

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.appttude.h_mal.farmr.data.Repository
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry.COLUMN_SHIFT_BREAK
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DATE
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DESCRIPTION
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry.COLUMN_SHIFT_DURATION
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry.COLUMN_SHIFT_PAYRATE
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TIME_IN
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TIME_OUT
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TOTALPAY
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TYPE
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry.COLUMN_SHIFT_UNIT
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry._ID
import com.appttude.h_mal.farmr.model.Order
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.model.Sortable
import com.appttude.h_mal.farmr.model.Success
import com.appttude.h_mal.farmr.utils.CURRENCY
import com.appttude.h_mal.farmr.utils.convertDateString
import com.appttude.h_mal.farmr.utils.formatAsCurrencyString
import com.appttude.h_mal.farmr.utils.sortedByOrder
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.Label
import jxl.write.WritableWorkbook
import jxl.write.WriteException
import java.io.File
import java.io.IOException
import java.util.Locale


class MainViewModel(
    private val repository: Repository
) : ShiftViewModel(repository) {

    private val _shiftLiveData = MutableLiveData<List<ShiftObject>>()
    private val shiftLiveData: LiveData<List<ShiftObject>> = _shiftLiveData

    private var mSort: Sortable = Sortable.ID
    private var mOrder: Order = Order.ASCENDING

    private val observer = Observer<List<ShiftObject>> {
        it?.let {
            val result = it.applyFilters().sortList(mSort, mOrder)
            onSuccess(result)
        }
    }

    init {
        // Load shifts into live data when view model has been instantiated
        refreshLiveData()
        shiftLiveData.observeForever(observer)
    }

    private fun List<ShiftObject>.applyFilters(): List<ShiftObject> {
        val filter = getFiltrationDetails()

        return filter { s ->
            comparedStrings(filter.type, s.type) &&
                    comparedStringsContains(filter.description, s.description) &&
                    (isBetween(filter.dateFrom, filter.dateTo, s.date) ?: true)
        }
    }

    private fun comparedStrings(first: String?, second: String?): Boolean {
        return when (compareValues(first, second)) {
            -1, 0, 1 -> true
            else -> {
                false
            }
        }
    }

    private fun comparedStringsContains(first: String?, second: String?): Boolean {
        first?.let {
            (second?.contains(it))?.let { c -> return c }
        }

        return comparedStrings(first, second)
    }

    private fun isBetween(fromDate: String?, toDate: String?, compareWith: String): Boolean? {
        val first = fromDate?.convertDateString()
        val second = toDate?.convertDateString()

        if (first == null && second == null) return null
        val compareDate = compareWith.convertDateString() ?: return null

        if (second == null) return compareDate.after(first)
        if (first == null) return compareDate.before(second)

        return compareDate.after(first) && compareDate.before(second)
    }


    override fun onCleared() {
        shiftLiveData.removeObserver(observer)
        super.onCleared()
    }

    private fun List<ShiftObject>.sortList(sort: Sortable, order: Order): List<ShiftObject> {
        return when (sort) {
            Sortable.ID -> sortedByOrder(order) { it.id }
            Sortable.TYPE -> sortedByOrder(order) { it.type }
            Sortable.DATE -> sortedByOrder(order) { it.date }
            Sortable.DESCRIPTION -> sortedByOrder(order) { it.description }
            Sortable.DURATION -> sortedByOrder(order) { it.duration }
            Sortable.UNITS -> sortedByOrder(order) { it.units }
            Sortable.RATEOFPAY -> sortedByOrder(order) { it.rateOfPay }
            Sortable.TOTALPAY -> sortedByOrder(order) { it.totalPay }
        }
    }

    fun getSortAndOrder(): Pair<Sortable, Order> {
        return Pair(mSort, mOrder)
    }

    fun setSortAndOrder(sort: Sortable, order: Order = Order.ASCENDING) {
        mSort = sort
        mOrder = order
        refreshLiveData()
    }

    fun getInformation(): String {
        var totalDuration = 0.0f
        var countOfTypeH = 0
        var countOfTypeP = 0
        var totalUnits = 0f
        var totalPay = 0f
        var lines = 0
        _shiftLiveData.value?.applyFilters()?.forEach {
            lines += 1
            totalDuration += it.duration
            when (ShiftType.getEnumByType(it.type)) {
                ShiftType.HOURLY -> countOfTypeH += 1
                ShiftType.PIECE -> countOfTypeP += 1
            }
            totalUnits += it.units
            totalPay += it.totalPay
        }

        return buildInfoString(
            totalDuration,
            countOfTypeH,
            countOfTypeP,
            totalUnits,
            totalPay,
            lines
        )
    }

    fun deleteShift(id: Long) {
        if (!repository.deleteSingleShiftFromDatabase(id)) {
            onError("Failed to delete shift")
        } else {
            refreshLiveData()
        }
    }

    fun deleteAllShifts() {
        if (!repository.deleteAllShiftsFromDatabase()) {
            onError("Failed to delete all shifts from database")
        } else {
            refreshLiveData()
        }
    }

    private fun buildInfoString(
        totalDuration: Float,
        countOfHourly: Int,
        countOfPiece: Int,
        totalUnits: Float,
        totalPay: Float,
        lines: Int
    ): String {
        val stringBuilder = StringBuilder("$lines Shifts").append("\n")

        if (countOfHourly != 0 && countOfPiece != 0) {
            stringBuilder.append(" ($countOfHourly Hourly/$countOfPiece Piece Rate)").append("\n")
        }
        if (countOfHourly != 0) {
            stringBuilder.append("Total Hours: ").append(totalDuration).append("\n")
        }
        if (countOfPiece != 0) {
            stringBuilder.append("Total Units: ").append(totalUnits).append("\n")
        }
        if (totalPay != 0f) {
            stringBuilder.append("Total Pay: ").append(totalPay.formatAsCurrencyString())
        }
        return stringBuilder.toString()
    }

    fun refreshLiveData() {
        repository.readShiftsFromDatabase()?.let { _shiftLiveData.postValue(it) }
    }

    fun clearFilters() {
        super.setFiltrationDetails(null, null, null, null)
        onSuccess(Success("Filters have been cleared"))
        refreshLiveData()
    }

    @RequiresPermission(WRITE_EXTERNAL_STORAGE)
    fun createExcelSheet(file: File): File? {
        val wbSettings = WorkbookSettings().apply {
            locale = Locale("en", "EN")
        }

        try {
            val workbook: WritableWorkbook = Workbook.createWorkbook(file, wbSettings)
            val sheet = workbook.createSheet("Shifts", 0)
            // Write column headers
            val headers = listOf(
                Label(0, 0, _ID),
                Label(1, 0, COLUMN_SHIFT_TYPE),
                Label(2, 0, COLUMN_SHIFT_DESCRIPTION),
                Label(3, 0, COLUMN_SHIFT_DATE),
                Label(4, 0, COLUMN_SHIFT_TIME_IN),
                Label(5, 0, COLUMN_SHIFT_TIME_OUT),
                Label(6, 0, "$COLUMN_SHIFT_BREAK (in mins)"),
                Label(7, 0, COLUMN_SHIFT_DURATION),
                Label(8, 0, COLUMN_SHIFT_UNIT),
                Label(9, 0, COLUMN_SHIFT_PAYRATE),
                Label(10, 0, COLUMN_SHIFT_TOTALPAY)
            )
            // table content
            if (shiftLiveData.value.isNullOrEmpty()) {
                onError("No data to parse into excel file")
                return null
            }
            val sortAndOrder = getSortAndOrder()
            val data = shiftLiveData.value!!.applyFilters()
                .sortList(sortAndOrder.first, sortAndOrder.second)
            var currentRow = 0
            val cells = data.mapIndexed { index, shift ->
                currentRow += 1
                listOf(
                    Label(0, currentRow, shift.id.toString()),
                    Label(1, currentRow, shift.type),
                    Label(2, currentRow, shift.description),
                    Label(3, currentRow, shift.date),
                    Label(4, currentRow, shift.timeIn),
                    Label(5, currentRow, shift.timeOut),
                    Label(6, currentRow, shift.breakMins.toString()),
                    Label(7, currentRow, shift.duration.toString()),
                    Label(8, currentRow, shift.units.toString()),
                    Label(9, currentRow, shift.rateOfPay.toString()),
                    Label(10, currentRow, shift.totalPay.toString())
                )
            }.flatten()

            currentRow += 1
            val footer = listOf(
                Label(0, currentRow, "Total:"),
                Label(7, currentRow, data.sumOf { it.duration.toDouble() }.toString()),
                Label(8, currentRow, data.sumOf { it.units.toDouble() }.toString()),
                Label(10, currentRow, data.sumOf { it.totalPay.toDouble() }.toString())
            )
            val content = listOf(headers, cells, footer).flatten()

            // Write content to sheet
            try {
                content.forEach { c -> sheet.addCell(c) }
            } catch (e: WriteException) {
                onError("Failed to write excel sheet")
                return null
            } catch (e: WriteException) {
                onError("Failed to write excel sheet")
                return null
            }

            workbook.write()
            workbook.close()

            return file
        } catch (e: IOException) {
            e.printStackTrace()
            onError("Failed to generate excel sheet of shifts")
        }
        return null
    }

}