package com.appttude.h_mal.farmr.data.ui

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
import androidx.test.rule.GrantPermissionRule
import com.appttude.h_mal.farmr.di.ShiftApplication
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule

@Suppress("EmptyMethod")
open class BaseTest<A : Activity>(
    private val activity: Class<A>,
    private val intentBundle: Bundle? = null,
) {

    lateinit var scenario: ActivityScenario<A>
    private lateinit var testApp: ShiftApplication
    private lateinit var testActivity: Activity
    private lateinit var decorView: View

    @get:Rule
    var permissionRule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE)

    @Before
    fun setUp() {
        val startIntent =
            Intent(InstrumentationRegistry.getInstrumentation().targetContext, activity)
        if (intentBundle != null) {
            startIntent.replaceExtras(intentBundle)
        }

        testApp =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as ShiftApplication
        runBlocking {
            beforeLaunch()
        }

        scenario = ActivityScenario.launch(startIntent)
        scenario.onActivity {
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
}