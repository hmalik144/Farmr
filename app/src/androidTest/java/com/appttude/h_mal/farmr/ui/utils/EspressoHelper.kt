package com.appttude.h_mal.farmr.ui.utils

import android.os.SystemClock.sleep
import android.view.View
import android.widget.CheckBox
import android.widget.Checkable
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.util.TreeIterables
import org.hamcrest.BaseMatcher
import org.hamcrest.CoreMatchers.isA
import org.hamcrest.Description
import org.hamcrest.Matcher


object EspressoHelper {

    /**
     * Perform action of waiting for a certain view within a single root view
     * @param viewMatcher Generic Matcher used to find our view
     */
    fun searchFor(viewMatcher: Matcher<View>): ViewAction {

        return object : ViewAction {

            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String {
                return "searching for view $this in the root view"
            }

            override fun perform(uiController: UiController, view: View) {
                var tries = 0
                val childViews: Iterable<View> = TreeIterables.breadthFirstViewTraversal(view)

                // Look for the match in the tree of child views
                childViews.forEach {
                    tries++
                    if (viewMatcher.matches(it)) {
                        // found the view
                        return
                    }
                }

                throw NoMatchingViewException.Builder()
                    .withRootView(view)
                    .withViewMatcher(viewMatcher)
                    .build()
            }
        }
    }

    /**
     * Performs an action to check/uncheck a checkbox
     *
     */
    fun setChecked(checked: Boolean): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): BaseMatcher<View> {
                return object : BaseMatcher<View>() {
                    override fun describeTo(description: Description?) {}

                    override fun matches(actual: Any?): Boolean {
                        return isA(CheckBox::class.java).matches(actual)
                    }
                }
            }

            override fun getDescription(): String {
                return ""
            }

            override fun perform(uiController: UiController, view: View) {
                val checkableView = view as Checkable
                checkableView.isChecked = checked
            }
        }
    }

    /**
     * Perform action of implicitly waiting for a certain view.
     * This differs from EspressoExtensions.searchFor in that,
     * upon failure to locate an element, it will fetch a new root view
     * in which to traverse searching for our @param match
     *
     * @param viewMatcher ViewMatcher used to find our view
     */
    fun waitForView(
        viewMatcher: Matcher<View>,
        waitMillis: Int = 5000,
        waitMillisPerTry: Long = 100
    ): ViewInteraction {

        // Derive the max tries
        val maxTries = waitMillis / waitMillisPerTry.toInt()

        var tries = 0

        for (i in 0..maxTries)
            try {
                // Track the amount of times we've tried
                tries++

                // Search the root for the view
                onView(isRoot()).perform(searchFor(viewMatcher))

                // If we're here, we found our view. Now return it
                return onView(viewMatcher)

            } catch (e: Exception) {

                if (tries == maxTries) {
                    throw e
                }
                sleep(waitMillisPerTry)
            }

        throw Exception("Error finding a view matching $viewMatcher")
    }
}