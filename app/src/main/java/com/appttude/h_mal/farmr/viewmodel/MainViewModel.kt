package com.appttude.h_mal.farmr.viewmodel

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.appttude.h_mal.farmr.base.BaseViewModel
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
import com.appttude.h_mal.farmr.data.prefs.DESCRIPTION
import com.appttude.h_mal.farmr.data.prefs.TIME_IN
import com.appttude.h_mal.farmr.data.prefs.TIME_OUT
import com.appttude.h_mal.farmr.data.prefs.TYPE
import com.appttude.h_mal.farmr.model.FilterStore
import com.appttude.h_mal.farmr.model.Order
import com.appttude.h_mal.farmr.model.Shift
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.model.Sortable
import com.appttude.h_mal.farmr.model.Success
import com.appttude.h_mal.farmr.utils.CURRENCY
import com.appttude.h_mal.farmr.utils.calculateDuration
import com.appttude.h_mal.farmr.utils.convertDateString
import com.appttude.h_mal.farmr.utils.dateStringIsValid
import com.appttude.h_mal.farmr.utils.formatToTwoDp
import com.appttude.h_mal.farmr.utils.getTimeString
import com.appttude.h_mal.farmr.utils.sortedByOrder
import com.appttude.h_mal.farmr.utils.timeStringIsValid
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.Label
import jxl.write.WritableWorkbook
import jxl.write.WriteException
import java.io.File
import java.io.IOException
import java.util.Calendar
import java.util.Locale


class MainViewModel(
    private val repository: Repository
) : BaseViewModel() {

    private val _shiftLiveData = MutableLiveData<List<ShiftObject>>()
    val shiftLiveData: LiveData<List<ShiftObject>> = _shiftLiveData

    private var mSort: Sortable = Sortable.ID
    private var mOrder: Order = Order.ASCENDING

    private var mFilterStore: FilterStore? = null

    private val observer = Observer<List<ShiftObject>> {
        val result = it.applyFilters().sortList(mSort, mOrder)
        onSuccess(result)
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
        val lines = _shiftLiveData.value?.size ?: 0
        _shiftLiveData.value?.forEach {
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

    fun getCurrentShift(id: Long) = repository.readSingleShiftFromDatabase(id)

    fun insertHourlyShift(
        description: String,
        date: String,
        rateOfPay: Float,
        timeIn: String?,
        timeOut: String?,
        breakMins: Int?,
    ) {
        // Validate inputs from the edit texts
        (description.length > 3).validateField {
            onError("Description length should be longer")
            return
        }
        date.dateStringIsValid().validateField {
            onError("Date format is invalid")
            return
        }
        (rateOfPay >= 0.00).validateField {
            onError("Rate of pay is invalid")
            return
        }
        timeIn?.timeStringIsValid()?.validateField {
            onError("Time in format is in correct")
            return
        }
        timeOut?.timeStringIsValid()?.validateField {
            onError("Time out format is in correct")
            return
        }
        breakMins?.let { it > 0 }?.validateField {
            onError("Break in minutes is invalid")
            return
        }

        doTry {
            val result = insertShiftIntoDatabase(
                ShiftType.HOURLY,
                description,
                date,
                rateOfPay.formatToTwoDp(),
                timeIn,
                timeOut,
                breakMins,
                null
            )

            if (result) onSuccess(Success("Shift successfully added"))
        }

    }

    fun insertPieceRateShift(
        description: String,
        date: String,
        units: Float,
        rateOfPay: Float
    ) {
        // Validate inputs from the edit texts
        (description.length > 3).validateField {
            onError("Description length should be longer")
            return
        }
        date.dateStringIsValid().validateField {
            onError("Date format is invalid")
            return
        }
        (rateOfPay >= 0.00).validateField {
            onError("Rate of pay is invalid")
            return
        }
        (units.toInt() >= 0).validateField {
            onError("Units cannot be below zero")
            return
        }

        doTry {
            val result = insertShiftIntoDatabase(
                type = ShiftType.PIECE,
                description = description,
                date = date,
                rateOfPay = rateOfPay.formatToTwoDp(),
                null,
                null,
                null,
                units = units
            )
            if (result) onSuccess(Success("New shift successfully added"))
        }
    }

    fun updateShift(
        id: Long,
        type: String? = null,
        description: String? = null,
        date: String? = null,
        rateOfPay: Float? = null,
        timeIn: String? = null,
        timeOut: String? = null,
        breakMins: Int? = null,
        units: Float? = null,
    ) {
        description?.let {
            (it.length > 3).validateField {
                onError("Description length should be longer")
                return
            }
        }
        date?.dateStringIsValid()?.validateField {
            onError("Date format is invalid")
            return
        }
        rateOfPay?.let {
            (it >= 0.00).validateField {
                onError("Rate of pay is invalid")
                return
            }
        }
        units?.let {
            (it.toInt() >= 0).validateField {
                onError("Units cannot be below zero")
                return
            }
        }
        timeIn?.timeStringIsValid()?.validateField {
            onError("Time in format is in correct")
            return
        }
        timeOut?.timeStringIsValid()?.validateField {
            onError("Time out format is in correct")
            return
        }
        breakMins?.let { it >= 0 }?.validateField {
            onError("Break in minutes is invalid")
            return
        }

        doTry {
            val result = updateShiftInDatabase(
                id,
                type = type?.let { ShiftType.getEnumByType(it) },
                description = description,
                date = date,
                rateOfPay = rateOfPay,
                timeIn = timeIn,
                timeOut = timeOut,
                breakMins = breakMins,
                units = units
            )

            if (result) onSuccess(Success("Shift successfully updated"))
        }
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

    private fun updateShiftInDatabase(
        id: Long,
        type: ShiftType? = null,
        description: String? = null,
        date: String? = null,
        rateOfPay: Float? = null,
        timeIn: String? = null,
        timeOut: String? = null,
        breakMins: Int? = null,
        units: Float? = null,
    ): Boolean {
        val currentShift = repository.readSingleShiftFromDatabase(id)?.copyToShift()
            ?: throw IOException("Cannot update shift as it does not exist")

        val shift = when (type) {
            ShiftType.HOURLY -> {
                // Shift type has changed so mandatory fields for hourly shift are now required as well
                val insertTimeIn =
                    (timeIn ?: currentShift.timeIn) ?: throw IOException("No time in inserted")
                val insertTimeOut =
                    (timeOut ?: currentShift.timeOut) ?: throw IOException("No time out inserted")
                Shift(
                    description = description ?: currentShift.description,
                    date = date ?: currentShift.date,
                    timeIn = insertTimeIn,
                    timeOut = insertTimeOut,
                    breakMins = breakMins ?: currentShift.breakMins,
                    rateOfPay = rateOfPay ?: currentShift.rateOfPay
                )
            }

            ShiftType.PIECE -> {
                // Shift type has changed so mandatory fields for piece rate shift are now required as well
                val insertUnits = (units ?: currentShift.units)
                    ?: throw IOException("Units must be inserted for piece rate shifts")
                Shift(
                    description = description ?: currentShift.description,
                    date = date ?: currentShift.date,
                    units = insertUnits,
                    rateOfPay = rateOfPay ?: currentShift.rateOfPay
                )
            }

            else -> {
                if (timeIn == null && timeOut == null && units == null && breakMins == null && rateOfPay == null) {
                    // Updates to description or date field
                    currentShift.copy(
                        description = description ?: currentShift.description,
                        date = date ?: currentShift.date,
                    )
                } else {
                    // Updating shifts where shift type has remained the same
                    when (currentShift.type) {
                        ShiftType.HOURLY -> {
                            val insertTimeIn = (timeIn ?: currentShift.timeIn) ?: throw IOException(
                                "No time in inserted"
                            )
                            val insertTimeOut = (timeOut ?: currentShift.timeOut)
                                ?: throw IOException("No time out inserted")
                            Shift(
                                description = description ?: currentShift.description,
                                date = date ?: currentShift.date,
                                timeIn = insertTimeIn,
                                timeOut = insertTimeOut,
                                breakMins = breakMins ?: currentShift.breakMins,
                                rateOfPay = rateOfPay ?: currentShift.rateOfPay
                            )
                        }

                        ShiftType.PIECE -> {
                            val insertUnits = (units ?: currentShift.units)
                                ?: throw IOException("Units must be inserted for piece rate shifts")
                            Shift(
                                description = description ?: currentShift.description,
                                date = date ?: currentShift.date,
                                units = insertUnits,
                                rateOfPay = rateOfPay ?: currentShift.rateOfPay
                            )
                        }
                    }
                }
            }
        }

        return repository.updateShiftIntoDatabase(id, shift)
    }

    private fun insertShiftIntoDatabase(
        type: ShiftType,
        description: String,
        date: String,
        rateOfPay: Float,
        timeIn: String?,
        timeOut: String?,
        breakMins: Int?,
        units: Float?,
    ): Boolean {
        val shift = when (type) {
            ShiftType.HOURLY -> {
                if (timeIn.isNullOrBlank() && timeOut.isNullOrBlank()) throw IOException("Time in and time out are null")
                val calendar by lazy { Calendar.getInstance() }
                val insertTimeIn = timeIn ?: calendar.getTimeString()
                val insertTimeOut = timeOut ?: calendar.getTimeString()

                Shift(
                    description = description,
                    date = date,
                    timeIn = insertTimeIn,
                    timeOut = insertTimeOut,
                    breakMins = breakMins,
                    rateOfPay = rateOfPay
                )
            }

            ShiftType.PIECE -> {
                Shift(
                    description = description,
                    date = date,
                    units = units!!,
                    rateOfPay = rateOfPay,
                )
            }
        }

        return repository.insertShiftIntoDatabase(shift)
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
            stringBuilder.append("Total Pay: ").append(CURRENCY).append(totalPay).append("\n")
        }
        return stringBuilder.toString()
    }

    fun refreshLiveData() {
        _shiftLiveData.postValue(repository.readShiftsFromDatabase())
    }

    private inline fun Boolean.validateField(failureCallback: () -> Unit) {
        if (!this) failureCallback.invoke()
    }

    /**
     * Lambda function that will invoke onError(...) on failure
     * but update live data when successful
     */
    private inline fun doTry(operation: () -> Unit) {
        try {
            operation.invoke()
            refreshLiveData()
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun setFiltrationDetails(
        description: String?,
        dateFrom: String?,
        dateTo: String?,
        type: String?
    ) {
        repository.setFilteringDetailsInPrefs(description, dateFrom, dateTo, type)
        onSuccess(Success("Filter(s) successfully applied"))
    }

    fun getFiltrationDetails(): FilterStore {
        val prefs = repository.retrieveFilteringDetailsInPrefs()
        mFilterStore = FilterStore(
            prefs[DESCRIPTION],
            prefs[TIME_IN],
            prefs[TIME_OUT],
            prefs[TYPE]
        )
        return mFilterStore!!
    }

    fun retrieveDurationText(mTimeIn: String?, mTimeOut: String?, mBreaks: Int?): Float? {
        try {
            return calculateDuration(mTimeIn, mTimeOut, mBreaks)
        } catch (e: IOException) {
            onError(e)
        }
        return null
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
            val data = shiftLiveData.value
            if (data.isNullOrEmpty()) {
                onError("No data to parse into excel file")
                return null
            }
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