package com.appttude.h_mal.farmr.data.room.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.sql.Date

@ProvidedTypeConverter
class DateConverter {
    @TypeConverter
    fun toDate(dateString: String): Date {
        // Convert yyyy-MM-dd into date
        return Date.valueOf(dateString)
    }

    @TypeConverter
    fun fromDate(date: Date): String {
        return date.toString()
    }
}