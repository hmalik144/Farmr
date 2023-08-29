package com.appttude.h_mal.farmr.ui.robots

import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.ui.BaseTestRobot

fun viewScreen(func: ViewItemScreenRobot.() -> Unit) = ViewItemScreenRobot().apply { func() }
class ViewItemScreenRobot : BaseTestRobot() {

    fun matchShiftType(type: ShiftType) {
        when (type) {
            ShiftType.HOURLY -> matchText(R.id.details_shift, type.type)
            ShiftType.PIECE -> matchText(R.id.details_shift, type.type)
        }
    }

    fun matchDescription(text: String) = matchText(R.id.details_desc, text)
    fun matchDate(date: String) {
        matchText(R.id.details_date, date)
    }

    fun matchTime(timeIn: String, timeOut: String) {
        matchText(R.id.details_time, "$timeIn-$timeOut")
    }

    fun matchBreakTime(mins: Int) = matchText(R.id.details_breaks, mins.toString())
    fun matchUnits(units: Float) = fillEditText(R.id.details_units, units.toString())
    fun matchRateOfPay(rateOfPay: Float) = fillEditText(R.id.details_pay_rate, rateOfPay.toString())
    fun matchTotalPay(pay: String) = matchText(R.id.details_totalpay, pay)
    fun matchDuration(duration: String) = matchText(R.id.details_duration, duration)

    fun clickEdit() = clickButton(R.id.details_edit)
}