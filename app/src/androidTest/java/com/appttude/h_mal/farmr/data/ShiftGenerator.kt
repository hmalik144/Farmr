package com.appttude.h_mal.farmr.data

import android.icu.util.Calendar
import com.appttude.h_mal.farmr.model.ShiftType
import java.text.SimpleDateFormat
import java.util.UUID
import kotlin.random.Random


fun getShift() = ShiftType.values().toList().shuffled().first()
fun generateDescription() = UUID.randomUUID().toString()
fun generatePayRate() = Random.nextDouble(0.00, 50.00)

fun getRandomDate(): String {
    val calendar = generateCalendar()
    val format = SimpleDateFormat("yyyy-MM-dd")
    return format.format(calendar) ?: "2010-12-28"
}

fun getRandomTime(): String {
    val calendar = generateCalendar()
    val format = SimpleDateFormat("hh:mm")
    return format.format(calendar) ?: "11:00"
}

fun generateCalendar() = Calendar.getInstance().set(
        Random.nextInt(2000, 2023),
        Random.nextInt(12),
        Random.nextInt(28),
        Random.nextInt(0, 23),
        Random.nextInt(0, 59)
)

fun getDurationInHours(timeOne: String, timeTwo: String) {

}
