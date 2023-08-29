package com.appttude.h_mal.farmr.ui.robots

import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.BaseRecyclerAdapter.CurrentViewHolder
import com.appttude.h_mal.farmr.model.Order
import com.appttude.h_mal.farmr.model.Sortable
import com.appttude.h_mal.farmr.ui.BaseTestRobot

fun homeScreen(func: HomeScreenRobot.() -> Unit) = HomeScreenRobot().apply { func() }
class HomeScreenRobot : BaseTestRobot() {

    fun clickOnItemWithText(text: String) = clickOnRecyclerItemWithText<CurrentViewHolder>(R.id.list_item_view, text)
    fun clickOnItemAtPosition(position: Int) = clickRecyclerAtPosition<CurrentViewHolder>(R.id.list_item_view, position)
    fun clickOnEdit(position: Int) = clickViewInRecyclerAtPosition<CurrentViewHolder>(R.id.list_item_view, position, R.id.imageView)
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
}