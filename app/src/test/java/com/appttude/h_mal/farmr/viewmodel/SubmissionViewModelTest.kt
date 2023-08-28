package com.appttude.h_mal.farmr.viewmodel

import com.appttude.h_mal.farmr.model.Success
import io.mockk.every
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertIs

class SubmissionViewModelTest : ShiftViewModelTest<SubmissionViewModel>() {

    @Test
    fun insertHourlyShifts_validParameters_successfulInsertions() {
        // Arrange
        val hourly = getHourlyShift()

        // Act
        every { repository.insertShiftIntoDatabase(hourly.copyToShift()) }.returns(true)
        hourly.run {
            viewModel.insertHourlyShift(description, date, rateOfPay, timeIn, timeOut, breakMins)
        }

        // Assert
        assertIs<Success>(retrieveCurrentData())
        assertEquals(
            (retrieveCurrentData() as Success).successMessage,
            "New shift successfully added"
        )
    }

    @Test
    fun insertPieceShifts_validParameters_successfulInsertions() {
        // Arrange
        val piece = getPieceRateShift()

        // Act
        every { repository.insertShiftIntoDatabase(piece.copyToShift()) }.returns(true)
        piece.run {
            viewModel.insertPieceRateShift(description, date, units, rateOfPay)
        }

        // Assert
        assertIs<Success>(retrieveCurrentData())
        assertEquals(
            (retrieveCurrentData() as Success).successMessage,
            "New shift successfully added"
        )
    }

    @Test
    fun insertHourlyShifts_validParameters_unsuccessfulInsertions() {
        // Arrange
        val hourly = getHourlyShift()

        // Act
        every { repository.insertShiftIntoDatabase(hourly.copyToShift()) }.returns(false)
        hourly.run {
            viewModel.insertHourlyShift(description, date, rateOfPay, timeIn, timeOut, breakMins)
        }

        // Assert
        assertEquals(
            retrieveCurrentError(),
            "Cannot insert shift"
        )
    }

    @Test
    fun insertPieceShifts_validParameters_unsuccessfulInsertions() {
        // Arrange
        val piece = getPieceRateShift()

        // Act
        every { repository.insertShiftIntoDatabase(piece.copyToShift()) }.returns(false)
        piece.run {
            viewModel.insertPieceRateShift(description, date, units, rateOfPay)
        }

        // Assert
        assertEquals(
            retrieveCurrentError(),
            "Cannot insert shift"
        )
    }

}