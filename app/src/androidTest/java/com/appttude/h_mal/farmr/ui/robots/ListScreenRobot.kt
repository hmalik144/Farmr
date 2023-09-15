package com.appttude.h_mal.farmr.ui.robots

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.BaseRecyclerAdapter.CurrentViewHolder
import com.appttude.h_mal.farmr.ui.BaseTestRobot
import com.appttude.h_mal.farmr.ui.utils.EspressoHelper
import com.appttude.h_mal.farmr.ui.utils.EspressoHelper.waitFor

fun listScreen(func: ListScreenRobot.() -> Unit) = ListScreenRobot().apply { func() }
class ListScreenRobot : BaseTestRobot() {

    fun clickOnItemWithText(text: String) =
        clickOnRecyclerItemWithText<CurrentViewHolder>(R.id.list_item_view, text)

    fun clickOnItemAtPosition(position: Int) =
        clickRecyclerAtPosition<CurrentViewHolder>(R.id.list_item_view, position)

    fun clickOnEditForItem(position: Int) {
        clickViewInRecyclerAtPosition<CurrentViewHolder>(
            R.id.list_item_view,
            position,
            R.id.imageView
        )
        waitFor(800)
        EspressoHelper.waitForView(withText("Update Shift")).perform(click())
    }

    fun clickOnDeleteForItem(position: Int) {
        clickViewInRecyclerAtPosition<CurrentViewHolder>(
            R.id.list_item_view,
            position,
            R.id.imageView
        )
        waitFor(800)
        EspressoHelper.waitForView(withText("Delete Shift")).perform(click())
    }

    fun confirmDeleteItemOnDialog() {
        onView(withText("delete"))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())
    }

    fun assertListCount(count: Int) =
        matchView(R.id.list_item_view).check(matches(hasChildCount(count)))
}