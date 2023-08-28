package com.appttude.h_mal.farmr.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.appttude.h_mal.farmr.data.Repository
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.model.ViewState
import com.appttude.h_mal.farmr.utils.getOrAwaitValue
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.Before
import org.junit.Rule
import org.mockito.ArgumentMatchers

open class ShiftViewModelTest<V : ShiftViewModel> {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @RelaxedMockK
    lateinit var repository: Repository

    @InjectMockKs
    lateinit var viewModel: V

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    fun retrieveCurrentData() =
        (viewModel.uiState.getOrAwaitValue() as ViewState.HasData<*>).data

    fun retrieveCurrentError() =
        (viewModel.uiState.getOrAwaitValue() as ViewState.HasError<*>).error

    fun getHourlyShift() = ShiftObject(
        ArgumentMatchers.anyLong(),
        ShiftType.HOURLY.type,
        "Day one",
        "2023-08-01",
        "12:00",
        "13:00",
        1f,
        ArgumentMatchers.anyInt(),
        ArgumentMatchers.anyFloat(),
        10f,
        10f
    )

    fun getPieceRateShift() = ShiftObject(
        ArgumentMatchers.anyLong(),
        ShiftType.PIECE.type,
        "Day five",
        "2023-08-05",
        ArgumentMatchers.anyString(),
        ArgumentMatchers.anyString(),
        ArgumentMatchers.anyFloat(),
        ArgumentMatchers.anyInt(),
        1f,
        10f,
        10f
    )

    fun getShifts() = listOf(
        ShiftObject(
            ArgumentMatchers.anyLong(),
            ShiftType.HOURLY.type,
            "Day one",
            "2023-08-01",
            "12:00",
            "13:00",
            1f,
            ArgumentMatchers.anyInt(),
            ArgumentMatchers.anyFloat(),
            10f,
            10f
        ),
        ShiftObject(
            ArgumentMatchers.anyLong(),
            ShiftType.HOURLY.type,
            "Day two",
            "2023-08-02",
            "12:00",
            "13:00",
            1f,
            ArgumentMatchers.anyInt(),
            ArgumentMatchers.anyFloat(),
            10f,
            10f
        ),
        ShiftObject(
            ArgumentMatchers.anyLong(),
            ShiftType.HOURLY.type,
            "Day three",
            "2023-08-03",
            "12:00",
            "13:00",
            1f,
            30,
            ArgumentMatchers.anyFloat(),
            10f,
            5f
        ),
        ShiftObject(
            ArgumentMatchers.anyLong(),
            ShiftType.HOURLY.type,
            "Day four",
            "2023-08-04",
            "12:00",
            "13:00",
            1f,
            30,
            ArgumentMatchers.anyFloat(),
            10f,
            5f
        ),
        ShiftObject(
            ArgumentMatchers.anyLong(),
            ShiftType.PIECE.type,
            "Day five",
            "2023-08-05",
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyFloat(),
            ArgumentMatchers.anyInt(),
            1f,
            10f,
            10f
        ),
        ShiftObject(
            ArgumentMatchers.anyLong(),
            ShiftType.PIECE.type,
            "Day six",
            "2023-08-06",
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyFloat(),
            ArgumentMatchers.anyInt(),
            1f,
            10f,
            10f
        ),
        ShiftObject(
            ArgumentMatchers.anyLong(),
            ShiftType.PIECE.type,
            "Day seven",
            "2023-08-07",
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyFloat(),
            ArgumentMatchers.anyInt(),
            1f,
            10f,
            10f
        ),
        ShiftObject(
            ArgumentMatchers.anyLong(),
            ShiftType.PIECE.type,
            "Day eight",
            "2023-08-08",
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyFloat(),
            ArgumentMatchers.anyInt(),
            1f,
            10f,
            10f
        ),
    )
}