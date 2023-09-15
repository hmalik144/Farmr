package com.appttude.h_mal.farmr.ui.robots

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.model.Order
import com.appttude.h_mal.farmr.model.Sortable
import com.appttude.h_mal.farmr.ui.BaseTestRobot

fun homeScreen(func: HomeScreenRobot.() -> Unit) = HomeScreenRobot().apply { func() }
class HomeScreenRobot : BaseTestRobot() {

    fun clickFab() = clickButton(R.id.fab1)
    fun clickOnInfoIcon() = clickButton(R.id.action_favorite)
    fun clickFilterInMenu() = clickOnMenuItem(R.string.filter)
    fun clickClearFilterInMenu() = clickOnMenuItem(R.string.clear)
    fun clickSortInMenu() = clickOnMenuItem(R.string.sort)

    fun applySort(sortable: Sortable, order: Order = Order.ASCENDING) {
        clickSortInMenu()
        val label = sortable.label
        clickDialogButton(label)
        val orderLabel = order.label
        clickDialogButton(orderLabel)
    }

    fun clickTab(tab: Tab) {
        val id = when (tab) {
            Tab.LIST -> R.id.nav_list
            Tab.CALENDAR -> R.id.nav_calendar
        }
        Espresso.onView(ViewMatchers.withId(id)).perform(ViewActions.click())
    }

    enum class Tab {
        LIST, CALENDAR
    }
}