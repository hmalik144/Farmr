package com.appttude.h_mal.farmr.data.ui.tests

import com.appttude.h_mal.farmr.data.ui.BaseTest
import com.appttude.h_mal.farmr.data.ui.robots.addScreen
import com.appttude.h_mal.farmr.data.ui.robots.homeScreen
import com.appttude.h_mal.farmr.data.ui.robots.viewScreen
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.ui.MainActivity
import org.junit.Test

class ShiftTests: BaseTest<MainActivity>(MainActivity::class.java) {

    // Add a shift successfully
    @Test
    fun test1() {
        homeScreen {
            clickFab()
        }
        addScreen {
            setDescription("This is a description")
            setDate(2023, 2, 11)
            clickShiftType(ShiftType.HOURLY)
            setTimeIn(12,0)
            setTimeOut(14, 30)
            setBreakTime(30)
            setRateOfPay(10f)
            assertDuration("2.0 hours")
            assertTotalPay("£20.00")
            submit()
        }
        homeScreen {
            sc("This is a description")
        }
    }

    // Edit a shift successfully
    @Test
    fun test2() {
        homeScreen {
            clickOnItemWithText("Edit this shift")
        }
        addScreen {
            setRateOfPay(20f)
            assertDuration("2.0 hours")
            assertTotalPay("£40.00")
            submit()
        }
        homeScreen {
            clickOnItemWithText("Edit this shift")
        }
        viewScreen {
            matchDescription("Edit this shift")
            matchDuration("2 Hours 0 minutes")
            matchTotalPay("2.0 hours @ £20.00 per Hour\nEquals:£40.00")
        }
    }

    // filter the list with date from
    @Test
    fun test3() {}

    // filter the list with date to
    @Test
    fun test4() {}

    // Add a shift as piece rate
    @Test
    fun test5() {}

    // Validate the details screen
    @Test
    fun test6() {}

    // filter, sort, order and then reset
    @Test
    fun test7() {}
}