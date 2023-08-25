package com.appttude.h_mal.farmr.data.legacydb

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
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
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry.CONTENT_URI
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry._ID
import com.appttude.h_mal.farmr.model.Shift
import com.appttude.h_mal.farmr.model.ShiftType

class LegacyDatabase(context: Context) {
    private val resolver = context.contentResolver

    private val projection = arrayOf<String?>(
        _ID,
        COLUMN_SHIFT_DESCRIPTION,
        COLUMN_SHIFT_DATE,
        COLUMN_SHIFT_TIME_IN,
        COLUMN_SHIFT_TIME_OUT,
        COLUMN_SHIFT_BREAK,
        COLUMN_SHIFT_DURATION,
        COLUMN_SHIFT_TYPE,
        COLUMN_SHIFT_UNIT,
        COLUMN_SHIFT_PAYRATE,
        COLUMN_SHIFT_TOTALPAY
    )

    // Create
    fun insertShiftDataIntoDatabase(
        shift: Shift
    ): Uri? {
        val values = ContentValues().apply {
            put(COLUMN_SHIFT_TYPE, shift.type.type)
            put(COLUMN_SHIFT_DESCRIPTION, shift.description)
            put(COLUMN_SHIFT_DATE, shift.description)
            put(COLUMN_SHIFT_TIME_IN, shift.timeIn ?: "00:00")
            put(COLUMN_SHIFT_TIME_OUT, shift.timeOut ?: "00:00")
            put(COLUMN_SHIFT_DURATION, shift.duration ?: 0.00f)
            put(COLUMN_SHIFT_BREAK, shift.breakMins ?: 0)
            put(COLUMN_SHIFT_UNIT, shift.units ?: 0.00f)
            put(COLUMN_SHIFT_PAYRATE, shift.rateOfPay)
            put(COLUMN_SHIFT_TOTALPAY, shift.totalPay)
        }
        return resolver.insert(CONTENT_URI, values)
    }

    // Read
    fun readShiftsFromDatabase(): List<ShiftObject>? {
        val cursor = resolver.query(
            CONTENT_URI,
            projection,
            null, null, null
        ) ?: return null
        cursor.moveToFirst()
        val shifts = (0..cursor.count).map { cursor.getShift() }
        // close cursor after query operations
        cursor.close()

        return shifts
    }

    fun readSingleShiftWithId(id: Long): ShiftObject? {
        val itemUri: Uri = ContentUris.withAppendedId(CONTENT_URI, id)

        val cursor = resolver.query(
            itemUri,
            projection,
            null, null, null
        ) ?: return null
        cursor.moveToFirst()

        val shift = cursor.takeIf { it.moveToFirst() }?.run { getShift() } ?: return null
        cursor.close()
        return shift
    }

    // Update
    fun updateShiftDataIntoDatabase(
        id: Long,
        typeString: String,
        descriptionString: String,
        dateString: String,
        timeInString: String,
        timeOutString: String,
        duration: Float,
        breaks: Int,
        units: Float,
        payRate: Float,
        totalPay: Float,
    ): Int {
        val itemUri: Uri = ContentUris.withAppendedId(CONTENT_URI, id)

        val values = ContentValues().apply {
            put(COLUMN_SHIFT_TYPE, typeString)
            put(COLUMN_SHIFT_DESCRIPTION, descriptionString)
            put(COLUMN_SHIFT_DATE, dateString)
            put(COLUMN_SHIFT_TIME_IN, timeInString)
            put(COLUMN_SHIFT_TIME_OUT, timeOutString)
            put(COLUMN_SHIFT_DURATION, duration)
            put(COLUMN_SHIFT_BREAK, breaks)
            put(COLUMN_SHIFT_UNIT, units)
            put(COLUMN_SHIFT_PAYRATE, payRate)
            put(COLUMN_SHIFT_TOTALPAY, totalPay)
        }
        return resolver.update(itemUri, values, null, null)
    }

    // Delete
    fun deleteAllShiftsInDatabase(): Int {
        return resolver.delete(CONTENT_URI, null, null)
    }

    fun deleteSingleShift(id: Long): Int {
        val args: Array<String> = arrayOf(id.toString())
        return resolver.delete(CONTENT_URI, "$_ID=?", args)
    }

    private fun Cursor.getShift(): ShiftObject = run {
        val id = getLong(getColumnIndexOrThrow(_ID))
        val descriptionColumnIndex = getString(
            getColumnIndexOrThrow(
                COLUMN_SHIFT_DESCRIPTION
            )
        )
        val dateColumnIndex = getString(getColumnIndexOrThrow(COLUMN_SHIFT_DATE))
        val timeInColumnIndex =
            getString(getColumnIndexOrThrow(COLUMN_SHIFT_TIME_IN))
        val timeOutColumnIndex =
            getString(getColumnIndexOrThrow(COLUMN_SHIFT_TIME_OUT))
        val durationColumnIndex =
            getFloat(getColumnIndexOrThrow(COLUMN_SHIFT_DURATION))
        val breakOutColumnIndex =
            getInt(getColumnIndexOrThrow(COLUMN_SHIFT_BREAK))
        val typeColumnIndex = getString(getColumnIndexOrThrow(COLUMN_SHIFT_TYPE))
        val unitColumnIndex = getFloat(getColumnIndexOrThrow(COLUMN_SHIFT_UNIT))
        val payrateColumnIndex =
            getFloat(getColumnIndexOrThrow(COLUMN_SHIFT_PAYRATE))
        val totalpayColumnIndex =
            getFloat(getColumnIndexOrThrow(COLUMN_SHIFT_TOTALPAY))

        ShiftObject(
            id = id,
            type = typeColumnIndex,
            description = descriptionColumnIndex,
            date = dateColumnIndex,
            timeIn = timeInColumnIndex,
            timeOut = timeOutColumnIndex,
            duration = durationColumnIndex,
            breakMins = breakOutColumnIndex,
            units = unitColumnIndex,
            rateOfPay = payrateColumnIndex,
            totalPay = totalpayColumnIndex
        )
    }
}