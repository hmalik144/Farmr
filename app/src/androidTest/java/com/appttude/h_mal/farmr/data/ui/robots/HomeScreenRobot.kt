package com.appttude.h_mal.farmr.data.ui.robots

import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.base.BaseRecyclerAdapter.CurrentViewHolder
import com.appttude.h_mal.farmr.data.ui.BaseTestRobot

fun homeScreen(func: HomeScreenRobot.() -> Unit) = HomeScreenRobot().apply { func() }
class HomeScreenRobot : BaseTestRobot() {

    fun clickOnItem(position: Int) = clickViewInRecyclerAtPosition<CurrentViewHolder>(R.id.list_item_view, position)
    fun clickOnItemWithText(text: String) = clickOnRecyclerItemWithText<CurrentViewHolder>(R.id.list_item_view, text)
    fun clickOnEdit(position: Int) = clickViewInRecycler<CurrentViewHolder>(R.id.list_item_view, R.id.imageView)
    fun clickFab() = clickButton(R.id.fab1)
    fun clickOnInfo() = clickButton(R.id.action_favorite)
//    fun clearFilter() =
//    fun applySort() =


//    fun verifyCurrentLocation(location: String) = matchText(R.id.location_main_4, location)
//    fun refresh() = pullToRefresh(R.id.swipe_refresh)
//    fun isDisplayed() = matchViewWaitFor(R.id.temp_main_4)
//    fun verifyUnableToRetrieve() {
//        matchText(R.id.header_text, R.string.retrieve_warning)
//        matchText(R.id.body_text, R.string.empty_retrieve_warning)
//    }
}