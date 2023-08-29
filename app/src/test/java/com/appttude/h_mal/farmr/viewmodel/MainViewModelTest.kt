package com.appttude.h_mal.farmr.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.appttude.h_mal.farmr.data.Repository
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.data.prefs.DATE_IN
import com.appttude.h_mal.farmr.data.prefs.DATE_OUT
import com.appttude.h_mal.farmr.data.prefs.DESCRIPTION
import com.appttude.h_mal.farmr.data.prefs.TYPE
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.model.ViewState
import com.appttude.h_mal.farmr.utils.getOrAwaitValue
import com.appttude.h_mal.farmr.utils.getShifts
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyList
import java.util.concurrent.TimeoutException
import kotlin.test.assertEquals

class MainViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var repository: Repository
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        repository = mockk()
        every { repository.readShiftsFromDatabase() }.returns(null)
        every { repository.retrieveFilteringDetailsInPrefs() }.returns(getFilter())
        viewModel = MainViewModel(repository)
    }

    @Test
    fun initViewModel_liveDataIsEmpty() {
        // Assert
        assertThrows(TimeoutException::class.java) { viewModel.uiState.getOrAwaitValue() }
    }

    @Test
    fun getShiftsFromRepository_liveDataIsShown() {
        // Arrange
        val listOfShifts = anyList<ShiftObject>()

        // Act
        every { repository.readShiftsFromDatabase() }.returns(listOfShifts)
        viewModel.refreshLiveData()

        // Assert
        assertEquals(retrieveCurrentData(), listOfShifts)
    }

    @Test
    fun getShiftsFromRepository_liveDataIsShown_defaultFiltersAndSortsValid() {
        // Arrange
        val listOfShifts = getShifts()

        // Act
        every { repository.readShiftsFromDatabase() }.returns(listOfShifts)
        viewModel.refreshLiveData()
        val retrievedShifts = retrieveCurrentData()
        val description = viewModel.getInformation()

        // Assert
        assertEquals(retrievedShifts, listOfShifts)
        assertEquals(
            description, "8 Shifts\n" +
                    " (4 Hourly/4 Piece Rate)\n" +
                    "Total Hours: 4.0\n" +
                    "Total Units: 4.0\n" +
                    "Total Pay: £70.00"
        )
    }

    @Test
    fun getShiftsFromRepository_applyFiltersThenClearFilters_descriptionIsValid() {
        // Arrange
        val listOfShifts = getShifts()
        val filteredShifts = getShifts().filter { it.type == ShiftType.HOURLY.type }

        // Act
        every { repository.readShiftsFromDatabase() }.returns(listOfShifts)
        every { repository.retrieveFilteringDetailsInPrefs() }.returns(getFilter(type = ShiftType.HOURLY.type))
        viewModel.refreshLiveData()
        val retrievedShifts = retrieveCurrentData()
        val description = viewModel.getInformation()

        every { repository.setFilteringDetailsInPrefs(null, null, null, null) }.returns(Unit)
        every { repository.retrieveFilteringDetailsInPrefs() }.returns(getFilter())
        viewModel.clearFilters()
        val descriptionAfterClearedFilter = viewModel.getInformation()

        // Assert
        assertEquals(retrievedShifts, filteredShifts)
        assertEquals(
            description, "4 Shifts\n" +
                    "Total Hours: 4.0\n" +
                    "Total Pay: £30.00"
        )
        assertEquals(
            descriptionAfterClearedFilter, "8 Shifts\n" +
                    " (4 Hourly/4 Piece Rate)\n" +
                    "Total Hours: 4.0\n" +
                    "Total Units: 4.0\n" +
                    "Total Pay: £70.00"
        )
    }

    private fun retrieveCurrentData() =
        (viewModel.uiState.getOrAwaitValue() as ViewState.HasData<*>).data

    private fun getFilter(
        description: String? = null,
        type: String? = null,
        dateIn: String? = null,
        dateOut: String? = null
    ): Map<String, String?> =
        mapOf(
            Pair(DESCRIPTION, description),
            Pair(DATE_IN, dateIn),
            Pair(DATE_OUT, dateOut),
            Pair(TYPE, type)
        )

}