package com.appttude.h_mal.farmr.data.legacydb

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry

/**
 * Created by h_mal on 26/12/2017.
 */
class ShiftProvider : ContentProvider() {
    private var mDbHelper: ShiftsDbHelper? = null
    override fun onCreate(): Boolean {
        mDbHelper = ShiftsDbHelper(context)
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor {
        val database = mDbHelper!!.readableDatabase
        val cursor: Cursor = when (sUriMatcher.match(uri)) {
            SHIFTS -> database.query(
                ShiftsEntry.TABLE_NAME, projection, selection, selectionArgs,
                null, null, sortOrder
            )

            SHIFT_ID -> {
                val mSelection = ShiftsEntry._ID + "=?"
                val mSelectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                database.query(
                    ShiftsEntry.TABLE_NAME, projection, mSelection, mSelectionArgs,
                    null, null, sortOrder
                )
            }

            else -> throw IllegalArgumentException("Cannot query $uri")
        }
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        return when (sUriMatcher.match(uri)) {
            SHIFTS -> insertShift(uri, contentValues)
            else -> throw IllegalArgumentException("Insertion is not supported for $uri")
        }
    }

    private fun insertShift(uri: Uri, values: ContentValues?): Uri? {
        values!!.getAsString(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION)
            ?: throw IllegalArgumentException("Description required")
        values.getAsString(ShiftsEntry.COLUMN_SHIFT_DATE)
            ?: throw IllegalArgumentException("Date required")
        values.getAsString(ShiftsEntry.COLUMN_SHIFT_TIME_IN)
            ?: throw IllegalArgumentException("Time In required")
        values.getAsString(ShiftsEntry.COLUMN_SHIFT_TIME_OUT)
            ?: throw IllegalArgumentException("Time Out required")
        val duration = values.getAsFloat(ShiftsEntry.COLUMN_SHIFT_DURATION)
        require(duration >= 0) { "Duration cannot be negative" }
        values.getAsString(ShiftsEntry.COLUMN_SHIFT_TYPE)
            ?: throw IllegalArgumentException("Shift type required")
        val shiftUnits = values.getAsFloat(ShiftsEntry.COLUMN_SHIFT_UNIT)
        require(shiftUnits >= 0) { "Units cannot be negative" }
        val payRate = values.getAsFloat(ShiftsEntry.COLUMN_SHIFT_PAYRATE)
        require(payRate >= 0) { "Pay rate cannot be negative" }
        val totalPay = values.getAsFloat(ShiftsEntry.COLUMN_SHIFT_TOTALPAY)
        require(totalPay >= 0) { "Total Pay cannot be negative" }
        val breakMins = values.getAsInteger(ShiftsEntry.COLUMN_SHIFT_BREAK)
        require(breakMins >= 0) { "Break cannot be negative" }
        val database = mDbHelper!!.writableDatabase
        val id = database.insert(ShiftsEntry.TABLE_NAME, null, values)
        if (id == -1L) {
            Log.e(LOG_TAG, "row failed $uri")
            return null
        }
        context!!.contentResolver.notifyChange(uri, null)
        return ContentUris.withAppendedId(uri, id)
    }

    override fun update(
        uri: Uri, contentValues: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return when (sUriMatcher.match(uri)) {
            SHIFTS -> updateShift(uri, contentValues, selection, selectionArgs)
            SHIFT_ID -> {
                val mSelection = ShiftsEntry._ID + "=?"
                val mSelectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                updateShift(uri, contentValues, mSelection, mSelectionArgs)
            }

            else -> throw IllegalArgumentException("Update is not supported for $uri")
        }
    }

    private fun updateShift(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        if (values!!.containsKey(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION)) {
            values.getAsString(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION)
                ?: throw IllegalArgumentException("description required")
        }
        if (values.containsKey(ShiftsEntry.COLUMN_SHIFT_DATE)) {
            values.getAsString(ShiftsEntry.COLUMN_SHIFT_DATE)
                ?: throw IllegalArgumentException("date required")
        }
        if (values.containsKey(ShiftsEntry.COLUMN_SHIFT_TIME_IN)) {
            values.getAsString(ShiftsEntry.COLUMN_SHIFT_TIME_IN)
                ?: throw IllegalArgumentException("time in required")
        }
        if (values.containsKey(ShiftsEntry.COLUMN_SHIFT_TIME_OUT)) {
            values.getAsString(ShiftsEntry.COLUMN_SHIFT_TIME_OUT)
                ?: throw IllegalArgumentException("time out required")
        }
        if (values.containsKey(ShiftsEntry.COLUMN_SHIFT_BREAK)) {
            values.getAsString(ShiftsEntry.COLUMN_SHIFT_BREAK)
                ?: throw IllegalArgumentException("break required")
        }
        if (values.size() == 0) {
            return 0
        }
        val database = mDbHelper!!.writableDatabase
        val rowsUpdated = database.update(ShiftsEntry.TABLE_NAME, values, selection, selectionArgs)
        if (rowsUpdated != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return rowsUpdated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val database = mDbHelper!!.writableDatabase
        val rowsDeleted: Int = when (sUriMatcher.match(uri)) {
            SHIFTS -> database.delete(ShiftsEntry.TABLE_NAME, selection, selectionArgs)

            SHIFT_ID -> {
                val mSelection = ShiftsEntry._ID + "=?"
                val mSelectionArgs = arrayOf(ContentUris.parseId(uri).toString())

                database.delete(ShiftsEntry.TABLE_NAME, mSelection, mSelectionArgs)
            }

            else -> throw IllegalArgumentException("Deletion is not supported for $uri")
        }
        if (rowsDeleted != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return rowsDeleted
    }

    override fun getType(uri: Uri): String {
        return when (val match = sUriMatcher.match(uri)) {
            SHIFTS -> ShiftsEntry.CONTENT_LIST_TYPE
            SHIFT_ID -> ShiftsEntry.CONTENT_ITEM_TYPE
            else -> throw IllegalStateException("Unknown URI $uri with match $match")
        }
    }

    companion object {
        val LOG_TAG: String = ShiftProvider::class.java.simpleName
        private const val SHIFTS = 100
        private const val SHIFT_ID = 101
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            sUriMatcher.addURI(ShiftsContract.CONTENT_AUTHORITY, ShiftsContract.PATH_SHIFTS, SHIFTS)
            sUriMatcher.addURI(
                ShiftsContract.CONTENT_AUTHORITY,
                ShiftsContract.PATH_SHIFTS + "/#",
                SHIFT_ID
            )
        }
    }

}