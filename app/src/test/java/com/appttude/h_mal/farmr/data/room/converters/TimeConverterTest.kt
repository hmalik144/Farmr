package com.appttude.h_mal.farmr.data.room.converters

import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import java.sql.Time

class TimeConverterTest {

    private val converter = TimeConverter()

    @Test
    fun toTime() {
        val str = "16:04"
        val time = converter.toTime(str)
        assertEquals(time.toString(), "$str:00")
    }

    @Test
    fun fromTime() {
        val str = "16:04"
        val time = converter.fromTime(Time.valueOf("16:04:00"))
        assertEquals(time, str)
    }
}