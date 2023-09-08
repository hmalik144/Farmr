package com.appttude.h_mal.farmr.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.appttude.h_mal.farmr.data.legacydb.ShiftObject
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry.TABLE_NAME
import com.appttude.h_mal.farmr.model.ShiftType
import java.sql.Date
import java.sql.Time


@Entity(tableName = TABLE_NAME)
data class ShiftEntity(
    @ColumnInfo(name = ShiftsEntry.COLUMN_SHIFT_DESCRIPTION) val description: String,
    @ColumnInfo(name = ShiftsEntry.COLUMN_SHIFT_DATE, typeAffinity = ColumnInfo.UNDEFINED) val date: Date,
    @ColumnInfo(name = ShiftsEntry.COLUMN_SHIFT_TIME_IN, typeAffinity = ColumnInfo.TEXT) val timeIn: Time,
    @ColumnInfo(name = ShiftsEntry.COLUMN_SHIFT_TIME_OUT, typeAffinity = ColumnInfo.TEXT) val timeOut: Time,
    @ColumnInfo(name = ShiftsEntry.COLUMN_SHIFT_BREAK) val breakMins: Int? = 0,
    @ColumnInfo(name = ShiftsEntry.COLUMN_SHIFT_DURATION, typeAffinity = ColumnInfo.REAL, defaultValue = "0") val duration: Float = 0f,
    @ColumnInfo(name = ShiftsEntry.COLUMN_SHIFT_TYPE) val type: String,
    @ColumnInfo(name = ShiftsEntry.COLUMN_SHIFT_UNIT, typeAffinity = ColumnInfo.REAL) val units: Float? = 0f,
    @ColumnInfo(name = ShiftsEntry.COLUMN_SHIFT_PAYRATE, typeAffinity = ColumnInfo.REAL) val payRate: Float? = 0f,
    @ColumnInfo(name = ShiftsEntry.COLUMN_SHIFT_TOTALPAY, typeAffinity = ColumnInfo.REAL) val totalPay: Float? = 0f,
    @PrimaryKey
    @ColumnInfo(name = ShiftsEntry._ID) val id: Long? = 0,
) {
    fun convertToShiftObject(): ShiftObject {
        return ShiftObject(
            id ?: 0, type, description, date.toString(), timeIn.toString(), timeOut.toString(), duration, breakMins ?: 0, units ?: 0f, payRate ?: 0f, totalPay ?: 0f
        )
    }
}