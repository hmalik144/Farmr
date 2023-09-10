package com.appttude.h_mal.farmr.viewmodel

import android.os.Bundle
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.utils.ID
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import kotlin.test.assertIs

class InfoViewModelTest : ShiftViewModelTest<InfoViewModel>() {

    @Test
    fun retrieveData_validBundleAndId_successfulRetrieval() {
        // Arrange
        val id = anyLong()
        val shift = mockk<ShiftObject>()

        // Act
        every { repository.readSingleShiftFromDatabase(id) }.returns(shift)
        viewModel.retrieveData(id)

        // Assert
        assertIs<ShiftObject>(retrieveCurrentData())
        assertEquals(
            retrieveCurrentData(),
            shift
        )
    }

    @Test
    fun retrieveData_validBundleNoShift_successfulRetrieval() {
        // Arrange
        val id = anyLong()

        // Act
        every { repository.readSingleShiftFromDatabase(id) }.returns(null)
        viewModel.retrieveData(id)

        // Assert
        assertEquals(
            retrieveCurrentError(),
            "Failed to retrieve shift"
        )
    }

    @Test
    fun buildDurationSummary_validHourlyShift_successfulRetrieval() {
        // Arrange
        val shift = getShifts()[0]
        val shiftWithBreak = getShifts()[3]

        // Act
        val summary = viewModel.buildDurationSummary(shift)
        val summaryWithBreak = viewModel.buildDurationSummary(shiftWithBreak)

        // Assert
        assertEquals(
            "1 Hours 0 Minutes ",
            summary
        )
        assertEquals(
            "1 Hours 0 Minutes  (+ 30 minutes break)",
            summaryWithBreak
        )
    }

}