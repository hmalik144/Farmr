package com.appttude.h_mal.farmr.data.room.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.sql.Date
import java.sql.Time
import java.time.Instant

@ProvidedTypeConverter
class TimeConverter {
    @TypeConverter
    fun toTime(timeString: String?): Time {
        // Convert HH:mm into date
        timeString?.let { return Time.valueOf("$timeString:00") }
        return Time.valueOf("00:00:00")
    }

    @TypeConverter
    fun fromTime(time: Time): String {
        // Return string in format of HH:mm
        return time.toString().substring(0,5)
    }
}