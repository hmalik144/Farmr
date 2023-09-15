package com.appttude.h_mal.farmr.ui.utils

import com.appttude.h_mal.farmr.model.Shift
import com.appttude.h_mal.farmr.model.ShiftType
import com.appttude.h_mal.farmr.utils.DATE_FORMAT
import com.appttude.h_mal.farmr.utils.TIME_FORMAT
import com.appttude.h_mal.farmr.utils.getTimeString
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Locale

fun getShifts() = listOf(
    Shift(
        ShiftType.HOURLY,
        "Day one",
        "2023-08-01",
        "12:00",
        "13:00",
        1f,
        0,
        0f,
        10f,
        10f
    ),
    Shift(
        ShiftType.HOURLY,
        "Day two",
        "2023-08-02",
        "12:00",
        "13:00",
        1f,
        0,
        0f,
        10f,
        10f
    ),
    Shift(
        ShiftType.HOURLY,
        "Day three",
        "2023-08-03",
        "12:00",
        "13:00",
        1f,
        30,
        0f,
        10f,
        5f
    ),
    Shift(
        ShiftType.HOURLY,
        "Day four",
        "2023-08-04",
        "12:00",
        "13:00",
        1f,
        30,
        0f,
        10f,
        5f
    ),
    Shift(
        ShiftType.PIECE,
        "Day five",
        "2023-08-05",
        "",
        "",
        0f,
        0,
        1f,
        10f,
        10f
    ),
    Shift(
        ShiftType.PIECE,
        "Day six",
        "2023-08-06",
        "",
        "",
        0f,
        0,
        1f,
        10f,
        10f
    ),
    Shift(
        ShiftType.PIECE,
        "Day seven",
        "2023-08-07",
        "",
        "",
        0f,
        0,
        1f,
        10f,
        10f
    ),
    Shift(
        ShiftType.PIECE,
        "Day eight",
        "2023-08-08",
        "",
        "",
        0f,
        0,
        1f,
        10f,
        10f
    )
)

fun Calendar.setDayAndGetDateString(day: Int): String {
    set(Calendar.DAY_OF_MONTH, day)
    val format = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    return format.format(time)
}

fun generateShifts(): List<Shift> {
    val calendar: Calendar = Calendar.getInstance()

    return listOf(
        Shift(
            ShiftType.HOURLY,
            "Day one",
            calendar.setDayAndGetDateString(1),
            "12:00",
            "13:00",
            1f,
            0,
            0f,
            10f,
            10f
        ),
        Shift(
            ShiftType.HOURLY,
            "Day two",
            calendar.setDayAndGetDateString(2),
            "12:00",
            "13:00",
            1f,
            0,
            0f,
            10f,
            10f
        ),
        Shift(
            ShiftType.HOURLY,
            "Day three",
            calendar.setDayAndGetDateString(3),
            "12:00",
            "13:00",
            1f,
            30,
            0f,
            10f,
            5f
        ),
        Shift(
            ShiftType.HOURLY,
            "Day four",
            calendar.setDayAndGetDateString(4),
            "12:00",
            "13:00",
            1f,
            30,
            0f,
            10f,
            5f
        ),
        Shift(
            ShiftType.PIECE,
            "Day five",
            calendar.setDayAndGetDateString(5),
            "",
            "",
            0f,
            0,
            1f,
            10f,
            10f
        ),
        Shift(
            ShiftType.PIECE,
            "Day six",
            calendar.setDayAndGetDateString(6),
            "",
            "",
            0f,
            0,
            1f,
            10f,
            10f
        ),
        Shift(
            ShiftType.PIECE,
            "Day seven",
            calendar.setDayAndGetDateString(7),
            "",
            "",
            0f,
            0,
            1f,
            10f,
            10f
        ),
        Shift(
            ShiftType.PIECE,
            "Day eight",
            calendar.setDayAndGetDateString(8),
            "",
            "",
            0f,
            0,
            1f,
            10f,
            10f
        )
    )
}