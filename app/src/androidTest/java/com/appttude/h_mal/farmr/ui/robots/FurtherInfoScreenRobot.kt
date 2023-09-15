package com.appttude.h_mal.farmr.ui.robots

import androidx.test.espresso.action.ViewActions.scrollTo
import com.appttude.h_mal.farmr.R
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.ui.BaseTestRobot

fun furtherInfoScreen(func: FurtherInfoScreenRobot.() -> Unit) = FurtherInfoScreenRobot().apply { func() }
class FurtherInfoScreenRobot : BaseTestRobot() {
    fun assertShiftType(type: ShiftType) {
        matchText(R.id.details_shift, type.type)
    }

    fun assertDescription(details: String) = matchText(R.id.details_desc, details)

    fun assertDate(date: String) = matchText(R.id.details_date, date)

    fun assertTime(time: String) = matchText(R.id.details_time, time)

    fun assertBreak(breakSummary: String) = matchText(R.id.details_breaks, breakSummary)

    fun assertDuration(duration: String) = matchText(R.id.details_duration, duration)

    fun assertUnits(units: String) = fillEditText(R.id.details_units, units)
    fun assertRateOfPay(rateOfPay: String) = matchText(R.id.details_pay_rate, rateOfPay)

    fun assertTotalPay(text: String?) = fillEditText(R.id.details_totalpay, text)

    fun update() {
        matchView(R.id.details_edit).perform(scrollTo())
        clickButton(R.id.details_edit)
    }
}