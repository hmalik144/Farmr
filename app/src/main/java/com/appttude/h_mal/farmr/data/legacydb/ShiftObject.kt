package com.appttude.h_mal.farmr.data.legacydb

import com.appttude.h_mal.farmr.model.Shift
import com.appttude.h_mal.farmr.model.ShiftType
import kotlin.math.floor

data class ShiftObject(
    val id: Long,
    val type: String,
    val description: String,
    val date: String,
    val timeIn: String,
    val timeOut: String,
    val duration: Float,
    val breakMins: Int,
    val units: Float,
    val rateOfPay: Float,
    val totalPay: Float
) {
    fun copyToShift() = Shift(ShiftType.getEnumByType(type), description, date, timeIn, timeOut, duration, breakMins, units, rateOfPay, totalPay)

    fun getHoursMinutesPairFromDuration(): Pair<String, String> {
        val hours: Int = floor(duration).toInt()
        val minutes: Int = ((duration - hours) * 60).toInt()
        return Pair(hours.toString(), minutes.toString())
    }

}