package com.appttude.h_mal.farmr.ui.robots

import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.ui.BaseTestRobot

fun filterScreen(func: FilterScreenRobot.() -> Unit) = FilterScreenRobot().apply { func() }
class FilterScreenRobot : BaseTestRobot() {

    fun setDescription(text: String?) = fillEditText(R.id.filterLocationEditText, text)

    fun setDateIn(year: Int, month: Int, day: Int) {
        clickButton(R.id.fromdateInEditText)
        selectDateInPicker(year, month, day)
    }

    fun setDateOut(year: Int, month: Int, day: Int) {
        clickButton(R.id.filterDateOutEditText)
        selectDateInPicker(year, month, day)
    }

    fun setType(type: ShiftType?) = when(type) {
        ShiftType.HOURLY -> selectTextInSpinner(R.id.TypeFilterEditText, type.type)
        ShiftType.PIECE -> selectTextInSpinner(R.id.TypeFilterEditText, type.type)
        null -> selectTextInSpinner(R.id.TypeFilterEditText, "")
    }
    fun submit() = clickButton(R.id.submitFiltered)

}