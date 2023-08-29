package com.appttude.h_mal.farmr.ui.tests

import com.appttude.h_mal.farmr.model.Order
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.model.Sortable
import com.appttude.h_mal.farmr.ui.BaseTest
import com.appttude.h_mal.farmr.ui.MainActivity
import com.appttude.h_mal.farmr.ui.robots.addScreen
import com.appttude.h_mal.farmr.ui.robots.filterScreen
import com.appttude.h_mal.farmr.ui.robots.homeScreen
import com.appttude.h_mal.farmr.ui.robots.viewScreen
import org.junit.Test

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
    fun openAddScreen_addNewShift_newShiftCreated() {
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
        homeScreen {
            clickOnItemWithText("This is a description")
        }
    }

    // Edit a shift successfully
    @Test
    fun test2() {
        homeScreen {
            clickOnEdit(0)
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
        homeScreen {
            clickOnItemWithText("Edited this shift")
        }
        viewScreen {
            matchDescription("Edited this shift")
            matchDuration("2 Hours 0 Minutes  (+ 30 minutes break)")
            matchTotalPay("2.0 Hours @ £20.00 per Hour\nEquals: £40.00")
        }
    }

    // filter the list with date from
    @Test
    fun test3() {
        homeScreen {
            applySort(Sortable.TYPE, Order.DESCENDING)
            clickOnItemAtPosition(0)
            viewScreen {
                matchDescription("Day five")
                matchShiftType(ShiftType.PIECE)
            }
        }
    }

    // filter the list with date to
    @Test
    fun test4() {
        homeScreen {
            clickFilterInMenu()
        }
        filterScreen {
            setDateIn(2023,8,3)
            setDateOut(2023,8,6)
            submit()
        }
        homeScreen {
            clickOnItemAtPosition(0)
        }
    }

    // Add a shift as piece rate
    @Test
    fun test5() {
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
        homeScreen {
            clickOnItemWithText("This is a description")
        }
    }

    // Validate the details screen
    @Test
    fun test6() {
    }

    // filter, sort, order and then reset
    @Test
    fun test7() {
    }
}