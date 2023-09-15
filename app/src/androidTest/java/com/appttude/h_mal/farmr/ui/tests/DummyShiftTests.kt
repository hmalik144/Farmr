package com.appttude.h_mal.farmr.ui.tests

import com.appttude.h_mal.farmr.ui.BaseTest
import com.appttude.h_mal.farmr.ui.MainActivity
import com.appttude.h_mal.farmr.ui.robots.homeScreen
import org.junit.Ignore
import org.junit.Test

@Ignore
class DummyShiftTests : BaseTest<MainActivity>(MainActivity::class.java) {

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

    // Add a shift successfully
    @Test
    fun openAddScreen_addNewHourlyShift_assertShiftDetail() {
        homeScreen {
            clickFab()
        }
    }
}