package com.appttude.h_mal.farmr.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.appttude.h_mal.farmr.data.Repository
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.data.prefs.DATE_IN
import com.appttude.h_mal.farmr.data.prefs.DATE_OUT
import com.appttude.h_mal.farmr.data.prefs.DESCRIPTION
import com.appttude.h_mal.farmr.data.prefs.TYPE
import com.appttude.h_mal.farmr.data.room.entity.ShiftEntity
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.model.ViewState
import com.appttude.h_mal.farmr.utils.getOrAwaitValue
import com.appttude.h_mal.farmr.utils.getShifts
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
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

        val mutableLiveData = MutableLiveData<List<ShiftEntity>>()
        val liveData: LiveData<List<ShiftEntity>> = mutableLiveData
        every { repository.readShiftsFromDatabase() }.returns(liveData)
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
        val mutableLiveData = MutableLiveData<List<ShiftEntity>>()
        val shifts = anyList<ShiftEntity>()
        mutableLiveData.postValue(shifts)
        val liveData: LiveData<List<ShiftEntity>> = mutableLiveData

        // Act
        every { repository.readShiftsFromDatabase() }.returns(liveData)
        viewModel = MainViewModel(repository)
        viewModel.refreshLiveData()

        // Assert
        assertEquals(retrieveCurrentData(), shifts)
    }

    @Test
    fun getShiftsFromRepository_liveDataIsShown_defaultFiltersAndSortsValid() {
        // Arrange
        val mutableLiveData = MutableLiveData<List<ShiftEntity>>()
        val shifts = getShifts()
        mutableLiveData.postValue(shifts)
        val liveData: LiveData<List<ShiftEntity>> = mutableLiveData

        // Act
        every { repository.readShiftsFromDatabase() }.returns(liveData)
        viewModel = MainViewModel(repository)
        viewModel.refreshLiveData()
        val retrievedShifts = retrieveCurrentData()
        val description = viewModel.getInformation()

        // Assert
        assertEquals(retrievedShifts, shifts.map { it.convertToShiftObject() })
        assertEquals(
            description, "8 Shifts\n" +
                    " (4 Hourly/4 Piece Rate)\n" +
                    "Total Hours: 6.0\n" +
                    "Total Units: 4.0\n" +
                    "Total Pay: £100.00"
        )
    }

    @Test
    fun getShiftsFromRepository_applyFiltersThenClearFilters_descriptionIsValid() {
        // Arrange
        val mutableLiveData = MutableLiveData<List<ShiftEntity>>()
        val shifts = getShifts()
        mutableLiveData.postValue(shifts)
        val liveData: LiveData<List<ShiftEntity>> = mutableLiveData

        val filteredShifts = getShifts().filter { it.type == ShiftType.HOURLY.type }

        // Act
        every { repository.readShiftsFromDatabase() }.returns(liveData)
        every { repository.retrieveFilteringDetailsInPrefs() }.returns(getFilter(type = ShiftType.HOURLY.type))
        viewModel = MainViewModel(repository)
        viewModel.refreshLiveData()
        val retrievedShifts = retrieveCurrentData()
        val description = viewModel.getInformation()

        every { repository.setFilteringDetailsInPrefs(null, null, null, null) }.returns(Unit)
        every { repository.retrieveFilteringDetailsInPrefs() }.returns(getFilter())
        viewModel.clearFilters()
        val descriptionAfterClearedFilter = viewModel.getInformation()

        // Assert
        assertEquals(retrievedShifts, filteredShifts.map { it.convertToShiftObject() })
        assertEquals(
            description, "4 Shifts\n" +
                    "Total Hours: 6.0\n" +
                    "Total Pay: £60.00"
        )
        assertEquals(
            descriptionAfterClearedFilter, "8 Shifts\n" +
                    " (4 Hourly/4 Piece Rate)\n" +
                    "Total Hours: 6.0\n" +
                    "Total Units: 4.0\n" +
                    "Total Pay: £100.00"
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