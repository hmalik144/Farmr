package com.appttude.h_mal.farmr.ui.robots

import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.ui.BaseTestRobot

fun addScreen(func: AddItemScreenRobot.() -> Unit) = AddItemScreenRobot().apply { func() }
class AddItemScreenRobot : BaseTestRobot() {

    fun clickShiftType(type: ShiftType) {
        when (type) {
            ShiftType.HOURLY -> clickButton(R.id.hourly)
            ShiftType.PIECE -> clickButton(R.id.piecerate)
        }
    }

    fun setDescription(text: String?) = fillEditText(R.id.locationEditText, text)
    fun setDate(year: Int, month: Int, day: Int) {
        clickButton(R.id.dateEditText)
        selectDateInPicker(year, month, day)
    }

    fun setTimeIn(hour: Int, minutes: Int) {
        clickButton(R.id.timeInEditText)
        selectTimeInPicker(hour, minutes)
    }

    fun setTimeOut(hour: Int, minutes: Int) {
        clickButton(R.id.timeOutEditText)
        selectTimeInPicker(hour, minutes)
    }

    fun setBreakTime(mins: Int) = fillEditText(R.id.breakEditText, mins.toString())
    fun setUnits(units: Float) = fillEditText(R.id.unitET, units.toString())
    fun setRateOfPay(rateOfPay: Float) = fillEditText(R.id.payrateET, rateOfPay.toString())
    fun submit() = clickButton(R.id.submit)

    fun assertTotalPay(pay: String) = matchText(R.id.totalpayval, pay)
    fun assertDuration(duration: String) = matchText(R.id.ShiftDuration, duration)

}