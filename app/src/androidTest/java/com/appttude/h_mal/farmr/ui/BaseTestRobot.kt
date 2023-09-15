package com.appttude.h_mal.farmr.ui

import android.content.res.Resources
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.ui.utils.EspressoHelper.waitForView
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matcher
import org.hamcrest.Matchers

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

    fun scrollTo(viewId: Int): ViewInteraction = matchView(viewId).perform(ViewActions.scrollTo())

    fun clickListItem(listRes: Int, position: Int) {
        onData(anything())
            .inAdapterView(allOf(withId(listRes)))
            .atPosition(position).perform(click())
    }

    fun clickOnMenuItem(menuId: Int) {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().context)
        onView(withText(menuId)).perform(click())
    }

    fun clickDialogButton(text: String) {
        onView(withText(text)).inRoot(isDialog())
        .check(matches(isDisplayed()))
            .perform(click());
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

    fun <VH : ViewHolder> clickRecyclerAtPosition(recyclerId: Int, position: Int) {
        matchView(recyclerId)
            .perform(
                // scrollTo will fail the test if no item matches.
                RecyclerViewActions.scrollToPosition<VH>(position),
                RecyclerViewActions.actionOnItemAtPosition<VH>(position, click()),
            )
    }

    fun <VH : ViewHolder> clickViewInRecyclerAtPosition(recyclerId: Int, position: Int, subViewId: Int) {
        matchView(recyclerId)
            .perform(
                // scrollTo will fail the test if no item matches.
                RecyclerViewActions.scrollToPosition<VH>(position),
                RecyclerViewActions.actionOnItemAtPosition<VH>(position, object : ViewAction {
                    override fun getDescription(): String {
                        return "click on subview in RecyclerView at position: $position"
                    }

                    override fun getConstraints(): Matcher<View> {
                        return Matchers.allOf(
                            isAssignableFrom(
                                RecyclerView::class.java
                            ), isDisplayed()
                        )
                    }

                    override fun perform(uiController: UiController?, view: View?) {
                        view?.findViewById<View>(subViewId)?.performClick()
                    }

                }),
            )
    }

    fun <VH : ViewHolder> clickOnRecyclerItemWithText(recyclerId: Int, text: String) {
        matchView(recyclerId).perform(
            // scrollTo will fail the test if no item matches.
            RecyclerViewActions.scrollTo<VH>(
                hasDescendant(withText(text))
            ),
            RecyclerViewActions.actionOnItem<VH>(
                hasDescendant(withText(text)),
                click()
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
        onView(
            allOf(
                withClassName(equalTo(AppCompatButton::class.java.name)),
                withText("OK")
            )
        ).perform(
            click()
        )
    }

    fun selectTextInSpinner(id: Int, text: String) {
        clickButton(id)
        onView(withSpinnerText(text)).perform(click())
    }

    fun selectTimeInPicker(hours: Int, minutes: Int) {
        onView(withClassName(equalTo(TimePicker::class.java.name))).perform(
            PickerActions.setTime(
                hours, minutes
            )
        )
        onView(
            allOf(
                withClassName(equalTo(AppCompatButton::class.java.name)),
                withText("OK")
            )
        ).perform(
            click()
        )
    }
}