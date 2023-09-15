package com.appttude.h_mal.farmr.ui.robots

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.hasTextColor
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.BaseRecyclerAdapter.CurrentViewHolder
import com.appttude.h_mal.farmr.ui.BaseTestRobot
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.IsNot.not

fun calendarScreen(func: CalendarScreenRobot.() -> Unit) = CalendarScreenRobot().apply { func() }
class CalendarScreenRobot : BaseTestRobot() {

    fun clickOnListItemWithText(text: String) =
        clickOnRecyclerItemWithText<CurrentViewHolder>(R.id.shifts_available_recycler, text)

    fun clickOnListItemAtPosition(position: Int) =
        clickRecyclerAtPosition<CurrentViewHolder>(R.id.shifts_available_recycler, position)

    fun clickOnEditForItem(position: Int) {
        clickViewInRecyclerAtPosition<CurrentViewHolder>(
            R.id.shifts_available_recycler,
            position,
            R.id.imageView
        )
        onView(withId(R.id.update)).perform(click())
    }

    fun clickOnDeleteForItem(position: Int) {
        clickViewInRecyclerAtPosition<CurrentViewHolder>(
            R.id.shifts_available_recycler,
            position,
            R.id.imageView
        )
        onView(withId(R.id.delete)).perform(click())
    }

    fun clickOnCalendarDay(day: Int) {
        onView(
            allOf(
                withId(R.id.dayLabel),
                not(hasTextColor(com.applandeo.materialcalendarview.R.color.nextMonthDayColor)),
                withText("$day"),
                isDisplayed()
            )
        ).perform(click())
    }

    fun clickNextMonth() = clickButton(com.applandeo.materialcalendarview.R.id.forwardButton)
    fun clickPreviousMonth() = clickButton(com.applandeo.materialcalendarview.R.id.previousButton)
}