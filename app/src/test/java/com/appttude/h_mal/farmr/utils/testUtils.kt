package com.appttude.h_mal.farmr.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.model.ShiftType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.mockito.ArgumentMatchers
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

fun sleep(millis: Long = 1000) {
    runBlocking(Dispatchers.Default) { delay(millis) }
}

fun getShifts() = listOf(
    ShiftObject(
        ArgumentMatchers.anyLong(),
        ShiftType.HOURLY.type,
        "Day one",
        "2023-08-01",
        "12:00",
        "13:00",
        1f,
        ArgumentMatchers.anyInt(),
        ArgumentMatchers.anyFloat(),
        10f,
        10f
    ),
    ShiftObject(
        ArgumentMatchers.anyLong(),
        ShiftType.HOURLY.type,
        "Day two",
        "2023-08-02",
        "12:00",
        "13:00",
        1f,
        ArgumentMatchers.anyInt(),
        ArgumentMatchers.anyFloat(),
        10f,
        10f
    ),
    ShiftObject(
        ArgumentMatchers.anyLong(),
        ShiftType.HOURLY.type,
        "Day three",
        "2023-08-03",
        "12:00",
        "13:00",
        1f,
        30,
        ArgumentMatchers.anyFloat(),
        10f,
        5f
    ),
    ShiftObject(
        ArgumentMatchers.anyLong(),
        ShiftType.HOURLY.type,
        "Day four",
        "2023-08-04",
        "12:00",
        "13:00",
        1f,
        30,
        ArgumentMatchers.anyFloat(),
        10f,
        5f
    ),
    ShiftObject(
        ArgumentMatchers.anyLong(),
        ShiftType.PIECE.type,
        "Day five",
        "2023-08-05",
        ArgumentMatchers.anyString(),
        ArgumentMatchers.anyString(),
        ArgumentMatchers.anyFloat(),
        ArgumentMatchers.anyInt(),
        1f,
        10f,
        10f
    ),
    ShiftObject(
        ArgumentMatchers.anyLong(),
        ShiftType.PIECE.type,
        "Day six",
        "2023-08-06",
        ArgumentMatchers.anyString(),
        ArgumentMatchers.anyString(),
        ArgumentMatchers.anyFloat(),
        ArgumentMatchers.anyInt(),
        1f,
        10f,
        10f
    ),
    ShiftObject(
        ArgumentMatchers.anyLong(),
        ShiftType.PIECE.type,
        "Day seven",
        "2023-08-07",
        ArgumentMatchers.anyString(),
        ArgumentMatchers.anyString(),
        ArgumentMatchers.anyFloat(),
        ArgumentMatchers.anyInt(),
        1f,
        10f,
        10f
    ),
    ShiftObject(
        ArgumentMatchers.anyLong(),
        ShiftType.PIECE.type,
        "Day eight",
        "2023-08-08",
        ArgumentMatchers.anyString(),
        ArgumentMatchers.anyString(),
        ArgumentMatchers.anyFloat(),
        ArgumentMatchers.anyInt(),
        1f,
        10f,
        10f
    ),
)