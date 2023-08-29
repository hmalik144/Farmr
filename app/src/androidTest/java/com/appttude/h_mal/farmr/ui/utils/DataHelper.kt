package com.appttude.h_mal.farmr.ui.utils

import com.appttude.h_mal.farmr.model.Shift
import com.appttude.h_mal.farmr.model.ShiftType

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