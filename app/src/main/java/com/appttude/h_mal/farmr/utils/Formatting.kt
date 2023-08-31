package com.appttude.h_mal.farmr.utils

import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Currency
import java.util.Date
import java.util.Locale

fun String.formatToTwoDp(): String {
    return String.format("%.2f", this)
}

fun Float.formatToTwoDp(): Float {
    val formattedString = String.format("%.2f", this)
    return formattedString.toFloat()
}

fun Float.formatAsCurrencyString(): String? {
    val format: NumberFormat = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 2
    format.currency = Currency.getInstance("GBP")

    return format.format(this)
}

fun Float.formatToTwoDpString(): String {
    return String.format("%.2f", this)
}

fun String.dateStringIsValid(): Boolean {
    return "([0-9]{4})-([0-9]{2})-([0-9]{2})".toPattern().matcher(this).matches()
}

fun String.timeStringIsValid(): Boolean {
    return "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]\$".toPattern().matcher(this).matches()
}

fun Calendar.getTimeString(): String {
    val format = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
    return format.format(time)
}

fun String.convertDateString(format: String = DATE_FORMAT): Date? {
    val formatter = SimpleDateFormat(format, Locale.getDefault())
    return formatter.parse(this)
}

/**
 * turns "HH:mm" into an hour and minutes pair
 *
 * eg:
 * @param 13:45
 * @return Pair(13, 45)
 */
fun convertTimeStringToHourMinutesPair(timeString: String): Pair<Int, Int> {
    val split = timeString.split(":")
    if (split.size != 2) throw ArrayIndexOutOfBoundsException()
    return Pair(split.first().toInt(), split[1].toInt())
}


/**
 * calculate the duration between two 24 hour time strings minus the break in minutes
 *
 * can also calculate when time to string in past midnight eg: 23:00, 04:45, 30
 * @return 5.75
 */
fun calculateDuration(timeIn: String, timeOut: String, breaks: Int): Float {
    val timeFrom = convertTimeStringToHourMinutesPair(timeIn)
    val timeTo = convertTimeStringToHourMinutesPair(timeOut)

    val hoursIn = timeFrom.first
    val minutesIn = timeFrom.second
    val hoursOut = timeTo.first
    val minutesOut = timeTo.second

    var duration: Float = if (hoursOut > hoursIn) {
        ((hoursOut.toFloat() + (minutesOut.toFloat() / 60)) - (hoursIn.toFloat() + (minutesIn.toFloat() / 60)))
    } else {
        (((hoursOut.toFloat() + (minutesOut.toFloat() / 60)) - (hoursIn.toFloat() + (minutesIn.toFloat() / 60))) + 24)
    }
    if ((breaks.toFloat() / 60) > duration) throw IOException("Breaks duration cannot be larger than shift duration")
    duration -= (breaks.toFloat() / 60)

    return duration.formatToTwoDp()
}

fun calculateDuration(timeIn: String?, timeOut: String?, breaks: Int?): Float {
    val calendar by lazy { Calendar.getInstance() }
    val insertTimeIn = timeIn ?: calendar.getTimeString()
    val insertTimeOut = timeOut ?: calendar.getTimeString()

    return calculateDuration(insertTimeIn, insertTimeOut, breaks ?: 0)
}