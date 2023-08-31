package com.appttude.h_mal.farmr.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import com.appttude.h_mal.farmr.application.TestAppClass
import com.appttude.h_mal.farmr.ui.utils.getShifts
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.kodein.di.android.kodein

@Suppress("EmptyMethod")
open class BaseTest<A : Activity>(
    private val activity: Class<A>,
    private val intentBundle: Bundle? = null,
) {

    lateinit var scenario: ActivityScenario<A>
    private lateinit var testApp: TestAppClass
    private lateinit var testActivity: Activity
    private lateinit var decorView: View

    @Before
    open fun setUp() {
        val startIntent =
            Intent(InstrumentationRegistry.getInstrumentation().targetContext, activity)
        if (intentBundle != null) {
            startIntent.replaceExtras(intentBundle)
        }

        testApp =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestAppClass
        kodein(testApp)
        runBlocking {
            beforeLaunch()
        }

        scenario = ActivityScenario.launch(startIntent)
        scenario.onActivity {
            testApp =
                InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestAppClass
            onLaunch()
            decorView = it.window.decorView
            testActivity = it
        }
        afterLaunch()
    }

    fun getActivity() = testActivity

    @After
    fun tearDown() {
        testFinished()
    }

    open fun beforeLaunch() {}
    open fun onLaunch() {}
    open fun afterLaunch() {}
    open fun testFinished() {}

    fun waitFor(delay: Long) {
        Espresso.onView(ViewMatchers.isRoot()).perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> = ViewMatchers.isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        })
    }

    @Suppress("DEPRECATION")
    fun checkToastMessage(message: String) {
        Espresso.onView(ViewMatchers.withText(message)).inRoot(withDecorView(Matchers.not(decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            waitFor(3500)
        }
    }

    fun navigateBack() = Espresso.pressBack()

    fun addRandomShifts() {
        testApp.addShiftsToDatabase(getShifts())
    }

    fun clearDataBase() = testApp.clearDatabase()
    fun clearPrefs() = testApp.cleanPrefs()
}