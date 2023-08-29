package com.appttude.h_mal.farmr.data

import android.content.ContentResolver
import android.content.ContentValues
import androidx.test.rule.provider.ProviderTestRule
import com.appttude.h_mal.farmr.data.legacydb.ShiftProvider
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.CONTENT_AUTHORITY
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
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.After
import org.junit.Rule
import org.junit.Test

class ShiftProviderTest {
    @get:Rule
    val providerRule: ProviderTestRule = ProviderTestRule
            .Builder(ShiftProvider::class.java, CONTENT_AUTHORITY)
            .build()

    private val contentResolver: ContentResolver
        get() = providerRule.resolver

    @After
    fun tearDown() {
        contentResolver.delete(CONTENT_URI, null, null)
    }

    @Test
    fun insertEntry_queryEntry_assertEntry() {
        // Arrange
        val typeColumn = "Hourly"
        val descriptionColumn = "Description string"
        val dateColumn = "01/01/2010"
        val timeInColumn = "09:00"
        val timeOutColumn = "14:00"
        val durationColumn = 4.50f
        val breakOutColumn = 30
        val unitColumn = 0f
        val payrateColumn = 10.00f
        val totalpayColumn = 45.00f

        val values = ContentValues().apply {
            put(COLUMN_SHIFT_TYPE, typeColumn)
            put(COLUMN_SHIFT_DESCRIPTION, descriptionColumn)
            put(COLUMN_SHIFT_DATE, dateColumn)
            put(COLUMN_SHIFT_TIME_IN, timeInColumn)
            put(COLUMN_SHIFT_TIME_OUT, timeOutColumn)
            put(COLUMN_SHIFT_DURATION, durationColumn)
            put(COLUMN_SHIFT_BREAK, breakOutColumn)
            put(COLUMN_SHIFT_UNIT, unitColumn)
            put(COLUMN_SHIFT_PAYRATE, payrateColumn)
            put(COLUMN_SHIFT_TOTALPAY, totalpayColumn)
        }
        val projection = arrayOf(
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
                COLUMN_SHIFT_TOTALPAY)

        // Act
        contentResolver.insert(CONTENT_URI, values)

        // Assert
        val item = contentResolver.query(CONTENT_URI, projection, null, null, null)
        item?.takeIf { it.moveToNext() }?.run {
            val id = getLong(getColumnIndexOrThrow(_ID))

            val descriptionColumnIndex = getString(getColumnIndexOrThrow(COLUMN_SHIFT_DESCRIPTION))
            val dateColumnIndex = getString(getColumnIndexOrThrow(COLUMN_SHIFT_DATE))
            val timeInColumnIndex = getString(getColumnIndexOrThrow(COLUMN_SHIFT_TIME_IN))
            val timeOutColumnIndex = getString(getColumnIndexOrThrow(COLUMN_SHIFT_TIME_OUT))
            val durationColumnIndex = getFloat(getColumnIndexOrThrow(COLUMN_SHIFT_DURATION))
            val breakOutColumnIndex = getInt(getColumnIndexOrThrow(COLUMN_SHIFT_BREAK))
            val typeColumnIndex = getString(getColumnIndexOrThrow(COLUMN_SHIFT_TYPE))
            val unitColumnIndex = getFloat(getColumnIndexOrThrow(COLUMN_SHIFT_UNIT))
            val payrateColumnIndex = getFloat(getColumnIndexOrThrow(COLUMN_SHIFT_PAYRATE))
            val totalpayColumnIndex = getFloat(getColumnIndexOrThrow(COLUMN_SHIFT_TOTALPAY))

            assertEquals(descriptionColumnIndex, descriptionColumn)
            assertEquals(dateColumnIndex, dateColumn)
            assertEquals(timeInColumnIndex, timeInColumn)
            assertEquals(timeOutColumnIndex, timeOutColumn)
            assertEquals(durationColumnIndex, durationColumn)
            assertEquals(breakOutColumnIndex, breakOutColumn)
            assertEquals(typeColumnIndex, typeColumn)
            assertEquals(unitColumnIndex, unitColumn)
            assertEquals(payrateColumnIndex, payrateColumn)
            assertEquals(totalpayColumnIndex, totalpayColumn)
        }
    }

    @Test
    fun insertAndDeleteAllEntry_queryEntry_assertEntry() {
        // Arrange
        val typeColumn = "Hourly"
        val descriptionColumn = "Description string"
        val dateColumn = "01/01/2010"
        val timeInColumn = "09:00"
        val timeOutColumn = "14:00"
        val durationColumn = 4.50f
        val breakOutColumn = 30
        val unitColumn = 0f
        val payrateColumn = 10.00f
        val totalpayColumn = 45.00f

        val values = ContentValues().apply {
            put(COLUMN_SHIFT_TYPE, typeColumn)
            put(COLUMN_SHIFT_DESCRIPTION, descriptionColumn)
            put(COLUMN_SHIFT_DATE, dateColumn)
            put(COLUMN_SHIFT_TIME_IN, timeInColumn)
            put(COLUMN_SHIFT_TIME_OUT, timeOutColumn)
            put(COLUMN_SHIFT_DURATION, durationColumn)
            put(COLUMN_SHIFT_BREAK, breakOutColumn)
            put(COLUMN_SHIFT_UNIT, unitColumn)
            put(COLUMN_SHIFT_PAYRATE, payrateColumn)
            put(COLUMN_SHIFT_TOTALPAY, totalpayColumn)
        }
        val projection = arrayOf(
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
                COLUMN_SHIFT_TOTALPAY)

        // Act
        contentResolver.insert(CONTENT_URI, values)
        val itemFirst = contentResolver.query(CONTENT_URI, projection, null, null, null)
        contentResolver.delete(CONTENT_URI, null, null)

        // Assert

        itemFirst?.takeIf { it.moveToNext() }?.run {
            val descriptionColumnIndex = getString(getColumnIndexOrThrow(COLUMN_SHIFT_DESCRIPTION))
            val dateColumnIndex = getString(getColumnIndexOrThrow(COLUMN_SHIFT_DATE))
            val timeInColumnIndex = getString(getColumnIndexOrThrow(COLUMN_SHIFT_TIME_IN))
            val timeOutColumnIndex = getString(getColumnIndexOrThrow(COLUMN_SHIFT_TIME_OUT))
            val durationColumnIndex = getFloat(getColumnIndexOrThrow(COLUMN_SHIFT_DURATION))
            val breakOutColumnIndex = getInt(getColumnIndexOrThrow(COLUMN_SHIFT_BREAK))
            val typeColumnIndex = getString(getColumnIndexOrThrow(COLUMN_SHIFT_TYPE))
            val unitColumnIndex = getFloat(getColumnIndexOrThrow(COLUMN_SHIFT_UNIT))
            val payrateColumnIndex = getFloat(getColumnIndexOrThrow(COLUMN_SHIFT_PAYRATE))
            val totalpayColumnIndex = getFloat(getColumnIndexOrThrow(COLUMN_SHIFT_TOTALPAY))

            assertEquals(descriptionColumnIndex, descriptionColumn)
            assertEquals(dateColumnIndex, dateColumn)
            assertEquals(timeInColumnIndex, timeInColumn)
            assertEquals(timeOutColumnIndex, timeOutColumn)
            assertEquals(durationColumnIndex, durationColumn)
            assertEquals(breakOutColumnIndex, breakOutColumn)
            assertEquals(typeColumnIndex, typeColumn)
            assertEquals(unitColumnIndex, unitColumn)
            assertEquals(payrateColumnIndex, payrateColumn)
            assertEquals(totalpayColumnIndex, totalpayColumn)
        }
        assertNull(contentResolver.query(CONTENT_URI, projection, null, null, null)?.takeIf { it.moveToNext() }?.run { getLong(getColumnIndexOrThrow(_ID)) })
    }

    @Test
    fun insertAndDeleteSingleEntry_queryEntry_assertEntry() {
        // Arrange
        val typeColumn = "Hourly"
        val descriptionColumn = "Description string"
        val dateColumn = "01/01/2010"
        val timeInColumn = "09:00"
        val timeOutColumn = "14:00"
        val durationColumn = 4.50f
        val breakOutColumn = 30
        val unitColumn = 0f
        val payrateColumn = 10.00f
        val totalpayColumn = 45.00f

        val values = ContentValues().apply {
            put(COLUMN_SHIFT_TYPE, typeColumn)
            put(COLUMN_SHIFT_DESCRIPTION, descriptionColumn)
            put(COLUMN_SHIFT_DATE, dateColumn)
            put(COLUMN_SHIFT_TIME_IN, timeInColumn)
            put(COLUMN_SHIFT_TIME_OUT, timeOutColumn)
            put(COLUMN_SHIFT_DURATION, durationColumn)
            put(COLUMN_SHIFT_BREAK, breakOutColumn)
            put(COLUMN_SHIFT_UNIT, unitColumn)
            put(COLUMN_SHIFT_PAYRATE, payrateColumn)
            put(COLUMN_SHIFT_TOTALPAY, totalpayColumn)
        }
        val projection = arrayOf(
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
                COLUMN_SHIFT_TOTALPAY)

        // Act
        contentResolver.insert(CONTENT_URI, values)
        val itemFirst = contentResolver.query(CONTENT_URI, projection, null, null, null)
        val id = itemFirst?.takeIf { it.moveToNext() }?.run { getLong(getColumnIndexOrThrow(_ID)) }
        contentResolver.delete(CONTENT_URI, "$_ID=?", arrayOf(id.toString()))

        // Assert

        itemFirst?.takeIf { it.moveToNext() }?.run {
            val descriptionColumnIndex = getString(getColumnIndexOrThrow(COLUMN_SHIFT_DESCRIPTION))
            val dateColumnIndex = getString(getColumnIndexOrThrow(COLUMN_SHIFT_DATE))
            val timeInColumnIndex = getString(getColumnIndexOrThrow(COLUMN_SHIFT_TIME_IN))
            val timeOutColumnIndex = getString(getColumnIndexOrThrow(COLUMN_SHIFT_TIME_OUT))
            val durationColumnIndex = getFloat(getColumnIndexOrThrow(COLUMN_SHIFT_DURATION))
            val breakOutColumnIndex = getInt(getColumnIndexOrThrow(COLUMN_SHIFT_BREAK))
            val typeColumnIndex = getString(getColumnIndexOrThrow(COLUMN_SHIFT_TYPE))
            val unitColumnIndex = getFloat(getColumnIndexOrThrow(COLUMN_SHIFT_UNIT))
            val payrateColumnIndex = getFloat(getColumnIndexOrThrow(COLUMN_SHIFT_PAYRATE))
            val totalpayColumnIndex = getFloat(getColumnIndexOrThrow(COLUMN_SHIFT_TOTALPAY))

            assertEquals(descriptionColumnIndex, descriptionColumn)
            assertEquals(dateColumnIndex, dateColumn)
            assertEquals(timeInColumnIndex, timeInColumn)
            assertEquals(timeOutColumnIndex, timeOutColumn)
            assertEquals(durationColumnIndex, durationColumn)
            assertEquals(breakOutColumnIndex, breakOutColumn)
            assertEquals(typeColumnIndex, typeColumn)
            assertEquals(unitColumnIndex, unitColumn)
            assertEquals(payrateColumnIndex, payrateColumn)
            assertEquals(totalpayColumnIndex, totalpayColumn)
        }
        assertNull(contentResolver.query(CONTENT_URI, projection, null, null, null)?.takeIf { it.moveToNext() }?.run { getLong(getColumnIndexOrThrow(_ID))})
    }

    private fun createContentValue() = ContentValues().apply {
        put(COLUMN_SHIFT_TYPE, getShift().type)
        put(COLUMN_SHIFT_DESCRIPTION, generateDescription())
        put(COLUMN_SHIFT_DATE, getRandomDate())
        put(COLUMN_SHIFT_TIME_IN, getRandomTime())
        put(COLUMN_SHIFT_TIME_OUT, getRandomTime())
        put(COLUMN_SHIFT_DURATION, 6)
        put(COLUMN_SHIFT_BREAK, 0)
        put(COLUMN_SHIFT_UNIT, 6)
        put(COLUMN_SHIFT_PAYRATE, 11)
        put(COLUMN_SHIFT_TOTALPAY, 6 * 11)
    }
}