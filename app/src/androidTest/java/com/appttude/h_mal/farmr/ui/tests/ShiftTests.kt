package com.appttude.h_mal.farmr.ui.tests

import androidx.test.espresso.action.ViewActions
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.model.Order
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.model.Sortable
import com.appttude.h_mal.farmr.ui.BaseTest
import com.appttude.h_mal.farmr.ui.MainActivity
import com.appttude.h_mal.farmr.ui.robots.HomeScreenRobot
import com.appttude.h_mal.farmr.ui.robots.addScreen
import com.appttude.h_mal.farmr.ui.robots.calendarScreen
import com.appttude.h_mal.farmr.ui.robots.filterScreen
import com.appttude.h_mal.farmr.ui.robots.homeScreen
import com.appttude.h_mal.farmr.ui.robots.listScreen
import com.appttude.h_mal.farmr.ui.robots.viewScreen
import com.appttude.h_mal.farmr.ui.utils.EspressoHelper.waitFor
import org.junit.Ignore
import org.junit.Test
import java.util.Calendar
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR

class ShiftTests : BaseTest<MainActivity>(MainActivity::class.java) {

    override fun afterLaunch() {
        super.afterLaunch()
        addRandomShifts()

        // Content resolver hard to mock
        // Dirty technique to have a populated list
        homeScreen {
            clickFab()
            navigateBack()
        }
    }

    override fun testFinished() {
        super.testFinished()
        clearDataBase()
        clearPrefs()
    }

    // Add a shift successfully
    @Test
    fun openAddScreen_addNewHourlyShift_assertShiftDetail() {
        homeScreen {
            clickFab()
        }
        addScreen {
            setDescription("This is a description")
            setDate(2023, 2, 11)
            clickShiftType(ShiftType.HOURLY)
            setTimeIn(12, 0)
            setTimeOut(14, 30)
            setBreakTime(30)
            setRateOfPay(10f)
            assertDuration("2.0 hours")
            assertTotalPay("£20.00")
            submit()
        }
        listScreen {
            clickOnItemWithText("This is a description")
        }
        viewScreen {
            matchDescription("This is a description")
            matchDate("2023-02-11")
            matchShiftType(ShiftType.HOURLY)
            matchTime("12:00", "14:30")
            matchBreakTime(30)
            matchRateOfPay(10.0f)
            matchDuration("2 Hours 0 Minutes  (+ 30 minutes break)")
            matchTotalPay("2.0 Hours @ £10.00 per Hour\nEquals: £20.00")
        }
    }

    @Test
    fun editShift_newDetailsAdded_assertShiftDetail() {
        listScreen {
            clickOnEditForItem(0)
        }
        addScreen {
            setDescription("Edited this shift")
            setTimeIn(12, 0)
            setTimeOut(14, 30)
            setBreakTime(30)
            setRateOfPay(20f)
            assertDuration("2.0 hours")
            assertTotalPay("£40.00")
            submit()
        }
        listScreen {
            clickOnItemWithText("Edited this shift")
        }
        viewScreen {
            matchDescription("Edited this shift")
            matchDuration("2 Hours 0 Minutes  (+ 30 minutes break)")
            matchTotalPay("2.0 Hours @ £20.00 per Hour\nEquals: £40.00")
        }
    }

    @Test
    fun applySort_listIsSorted_assertShiftsSortedCorrectly() {
        homeScreen {
            applySort(Sortable.TYPE, Order.DESCENDING)
            listScreen {
                clickOnItemAtPosition(0)
            }
            viewScreen {
                matchDescription("Day five")
                matchShiftType(ShiftType.PIECE)
            }
        }
    }

    @Test
    fun applyDateBetweenFilterAndClear_listIsFilteredByDate_assertFilteredResultsCorrectly() {
        homeScreen {
            clickFilterInMenu()
        }
        filterScreen {
            val calendar = Calendar.getInstance()
            val year = calendar.get(YEAR)
            val month = calendar.get(MONTH) + 1
            setDateIn(year, month, 3)
            setDateOut(year, month, 6)
            submit()
        }
        listScreen {
            assertListCount(4)
        }
    }

    @Test
    fun openAddScreen_addNewPieceShift_assertShiftDetail() {
        homeScreen {
            clickFab()
        }
        addScreen {
            setDescription("This is a description")
            setDate(2023, 2, 11)
            clickShiftType(ShiftType.PIECE)
            setRateOfPay(10f)
            setUnits(1f)
            assertTotalPay("£10.00")
            submit()
        }
        listScreen {
            clickOnItemWithText("This is a description")
        }
        viewScreen {
            matchDescription("This is a description")
            matchDate("2023-02-11")
            matchShiftType(ShiftType.PIECE)
            matchUnits(1f)
            matchRateOfPay(10.0f)
            matchTotalPay("1.0 Units @ £10.00 per Unit\nEquals: £10.00")
        }
    }

    @Ignore("Fails in circleci - device size")
    @Test
    fun openCalendarTab_clickOnFirstActiveDay_assertShiftDetails() {
        homeScreen {
            clickTab(HomeScreenRobot.Tab.CALENDAR)
        }
        calendarScreen {
            clickOnCalendarDay(1)
            scrollTo(R.id.shifts_available_recycler)
            clickOnListItemAtPosition(0)
        }
        viewScreen {
            matchDate("2023-09-01")
        }
    }

    @Test
    fun deleteShift_confirmDelete_assertShiftDeleted() {
        listScreen {
            clickOnDeleteForItem(0)
            confirmDeleteItemOnDialog()
            clickOnItemAtPosition(0)
        }
        viewScreen {
            matchDescription("Day two")
        }
    }
}