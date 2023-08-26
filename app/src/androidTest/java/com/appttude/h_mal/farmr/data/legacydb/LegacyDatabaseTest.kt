package com.appttude.h_mal.farmr.data.legacydb

import androidx.test.rule.provider.ProviderTestRule
import com.appttude.h_mal.farmr.model.Shift
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LegacyDatabaseTest {
    @get:Rule
    val providerRule: ProviderTestRule = ProviderTestRule
        .Builder(ShiftProvider::class.java, ShiftsContract.CONTENT_AUTHORITY)
        .build()

    private lateinit var database: LegacyDatabase

    @Before
    fun setup() {
        database = LegacyDatabase(providerRule.resolver)
    }

    @After
    fun tearDown() {
        database.deleteAllShiftsInDatabase()
    }

    @Test
    fun insertShift_readShift_successfulRead() {
        // Arrange
        val shift = Shift("adsfadsf", "2020-12-12", 12f, 12f)

        // Act
        database.insertShiftDataIntoDatabase(shift)
        val retrievedShift = database.readShiftsFromDatabase()?.first()

        // Assert
        assertEquals(retrievedShift?.description, shift.description)
        assertEquals(retrievedShift?.date, shift.date)
        assertEquals(retrievedShift?.units, shift.units)
        assertEquals(retrievedShift?.rateOfPay, shift.rateOfPay)
    }

    @Test
    fun insertShift_updateShift_successfulRead() {
        // Arrange
        val shift = Shift("adsfadsf", "2020-12-12", 12f, 12f)
        val updateShift = Shift("dasdads", "2020-11-12", 10f, 10f)

        // Act
        database.insertShiftDataIntoDatabase(shift)
        val id = database.readShiftsFromDatabase()?.first()!!.id
        database.updateShiftDataIntoDatabase(
            id = id,
            typeString = updateShift.type.type,
            descriptionString = updateShift.description,
            dateString = updateShift.date,
            timeInString = updateShift.timeIn ?: "",
            timeOutString = updateShift.timeOut ?: "",
            duration = updateShift.duration ?: 0f,
            breaks = updateShift.breakMins ?: 0,
            units = updateShift.units!!,
            payRate = updateShift.rateOfPay,
            totalPay = updateShift.totalPay
        )
        val retrievedShift = database.readSingleShiftWithId(id)

        // Assert
        assertEquals(retrievedShift?.description, updateShift.description)
        assertEquals(retrievedShift?.date, updateShift.date)
        assertEquals(retrievedShift?.units, updateShift.units)
        assertEquals(retrievedShift?.rateOfPay, updateShift.rateOfPay)
    }

    @Test
    fun insertShift_deleteShift_databaseEmpty() {
        // Arrange
        val shift = Shift("adsfadsf", "2020-12-12", 12f, 12f)
        val updateShift = Shift("dasdads", "2020-11-12", 10f, 10f)

        // Act
        database.insertShiftDataIntoDatabase(shift)
        database.insertShiftDataIntoDatabase(updateShift)
        val id = database.readShiftsFromDatabase()?.first()!!.id
        database.deleteSingleShift(id)

        // Assert
        assertEquals(database.readShiftsFromDatabase()?.size, 1)
    }
}