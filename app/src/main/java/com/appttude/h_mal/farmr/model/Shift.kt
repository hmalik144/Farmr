package com.appttude.h_mal.farmr.model

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
)