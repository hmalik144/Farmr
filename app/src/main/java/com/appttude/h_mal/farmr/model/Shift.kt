package com.appttude.h_mal.farmr.model

import com.appttude.h_mal.farmr.utils.calculateDuration
import com.appttude.h_mal.farmr.utils.formatToTwoDp

data class Shift(
    val type: ShiftType,
    val description: String,
    val date: String,
    val timeIn: String?,
    val timeOut: String?,
    val duration: Float?,
    val breakMins: Int?,
    val units: Float?,
    val rateOfPay: Float,
    val totalPay: Float
) {
    companion object {
        // Invocation for Hourly
        operator fun invoke(
            description: String,
            date: String,
            timeIn: String,
            timeOut: String,
            breakMins: Int? = null,
            rateOfPay: Float
        ): Shift {
            val breakTime = breakMins ?: 0
            val duration = calculateDuration(timeIn, timeOut, breakTime)

            return Shift(
                ShiftType.HOURLY,
                description,
                date,
                timeIn,
                timeOut,
                duration,
                breakTime,
                0f,
                rateOfPay,
                (duration * rateOfPay).formatToTwoDp()
            )
        }

        operator fun invoke(
            description: String,
            date: String,
            units: Float,
            rateOfPay: Float
        ) = Shift(
            ShiftType.PIECE,
            description,
            date,
            "",
            "",
            0f,
            0,
            units,
            rateOfPay,
            (units * rateOfPay).formatToTwoDp()
        )
    }


}