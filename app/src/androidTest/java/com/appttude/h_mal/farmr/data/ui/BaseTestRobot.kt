package com.appttude.h_mal.farmr.data.ui

import android.content.res.Resources
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.appttude.h_mal.farmr.data.ui.utils.EspressoHelper.waitForView
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.CoreMatchers.equalTo

@SuppressWarnings("unused")
open class BaseTestRobot {

    fun fillEditText(resId: Int, text: String?): ViewInteraction =
        onView(withId(resId)).perform(
            ViewActions.replaceText(text),
            ViewActions.closeSoftKeyboard()
        )

    fun clickButton(resId: Int): ViewInteraction =
        onView((withId(resId))).perform(click())

//    fun clickMenu(menuId: Int): ViewInteraction = onView()

    fun matchView(resId: Int): ViewInteraction = onView(withId(resId))

    fun matchViewWaitFor(resId: Int): ViewInteraction = waitForView(withId(resId))

    fun matchDisplayed(resId: Int): ViewInteraction = matchView(resId).check(matches(isDisplayed()))

    fun matchText(viewInteraction: ViewInteraction, text: String): ViewInteraction = viewInteraction
        .check(matches(withText(text)))

    fun matchText(viewId: Int, textId: Int): ViewInteraction = onView(withId(viewId))
        .check(matches(withText(textId)))

    fun matchText(resId: Int, text: String): ViewInteraction = matchText(matchView(resId), text)

    fun clickListItem(listRes: Int, position: Int) {
        onData(anything())
            .inAdapterView(allOf(withId(listRes)))
            .atPosition(position).perform(click())
    }

    fun <VH : ViewHolder> scrollToRecyclerItem(recyclerId: Int, text: String): ViewInteraction? {
        return matchView(recyclerId)
            .perform(
                // scrollTo will fail the test if no item matches.
                RecyclerViewActions.scrollTo<VH>(
                    hasDescendant(withText(text))
                )
            )
    }

    fun <VH : ViewHolder> scrollToRecyclerItem(
        recyclerId: Int,
        resIdForString: Int
    ): ViewInteraction? {
        return matchView(recyclerId)
            .perform(
                // scrollTo will fail the test if no item matches.
                RecyclerViewActions.scrollTo<VH>(
                    hasDescendant(withText(resIdForString))
                )
            )
    }

    fun <VH : ViewHolder> scrollToRecyclerItemByPosition(
        recyclerId: Int,
        position: Int
    ): ViewInteraction? {
        return matchView(recyclerId)
            .perform(
                // scrollTo will fail the test if no item matches.
                RecyclerViewActions.scrollToPosition<VH>(position)
            )
    }

    fun <VH : ViewHolder> clickViewInRecycler(recyclerId: Int, text: String) {
        matchView(recyclerId)
            .perform(
                // scrollTo will fail the test if no item matches.
                RecyclerViewActions.actionOnItem<VH>(hasDescendant(withText(text)), click())
            )
    }

    fun <VH : ViewHolder> clickViewInRecycler(recyclerId: Int, resIdForString: Int) {
        matchView(recyclerId)
            .perform(
                // scrollTo will fail the test if no item matches.
                RecyclerViewActions.actionOnItem<VH>(
                    hasDescendant(withText(resIdForString)),
                    click()
                )
            )
    }

    fun <VH : ViewHolder> clickViewInRecyclerAtPosition(recyclerId: Int, position: Int) {
        matchView(recyclerId)
            .perform(
                // scrollTo will fail the test if no item matches.
                RecyclerViewActions.scrollToPosition<VH>(position),
                RecyclerViewActions.actionOnItemAtPosition<VH>(position, click())
            )
    }

    fun <VH : ViewHolder> clickOnRecyclerItemWithText(recyclerId: Int, text: String) {
        scrollToRecyclerItem<VH>(recyclerId, text)
            ?.perform(
                // scrollTo will fail the test if no item matches.
                RecyclerViewActions.actionOnItem<VH>(
                    withChild(withText(text)), click()
                )
            )
    }

    fun swipeDown(resId: Int): ViewInteraction =
        onView(withId(resId)).perform(swipeDown())

    fun getStringFromResource(@StringRes resId: Int): String =
        Resources.getSystem().getString(resId)

    fun pullToRefresh(resId: Int) {
        onView(allOf(withId(resId), isDisplayed())).perform(swipeDown())
    }

    fun selectDateInPicker(year: Int, month: Int, day: Int) {
        onView(withClassName(equalTo(DatePicker::class.java.name))).perform(
            PickerActions.setDate(
                year,
                month,
                day
            )
        )
    }

    fun selectTextInSpinner(id: Int, text:String) {
        clickButton(id)
        onView(withSpinnerText(text)).perform(click())
    }

    fun selectTimeInPicker(hours: Int, minutes: Int) {
        onView(withClassName(equalTo(TimePicker::class.java.name))).perform(
            PickerActions.setTime(
                hours, minutes
            )
        )
    }
}