package com.appttude.h_mal.farmr.data.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry
import com.appttude.h_mal.farmr.data.legacydb.ShiftsDbHelper
import com.appttude.h_mal.farmr.data.room.ROOM_DATABASE
import com.appttude.h_mal.farmr.model.ShiftType

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Drop unused table
        database.execSQL("DROP TABLE IF EXISTS '${ShiftsEntry.TABLE_NAME_EXPORT}'")
        // Create a new table for room
        database.execSQL(
            "CREATE TABLE $ROOM_DATABASE (${ShiftsEntry._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "${ShiftsEntry.COLUMN_SHIFT_DESCRIPTION} TEXT NOT NULL, " +
                    "${ShiftsEntry.COLUMN_SHIFT_DATE} TEXT NOT NULL, " +
                    "${ShiftsEntry.COLUMN_SHIFT_TIME_IN} TEXT NOT NULL, " +
                    "${ShiftsEntry.COLUMN_SHIFT_TIME_OUT}  TEXT NOT NULL, " +
                    "${ShiftsEntry.COLUMN_SHIFT_BREAK} INTEGER NOT NULL DEFAULT 0, " +
                    "${ShiftsEntry.COLUMN_SHIFT_DURATION} REAL NOT NULL DEFAULT 0, " +
                    "${ShiftsEntry.COLUMN_SHIFT_TYPE} TEXT NOT NULL DEFAULT " + ShiftType.HOURLY.type + ", " +
                    "${ShiftsEntry.COLUMN_SHIFT_UNIT} REAL NOT NULL DEFAULT 0, " +
                    "${ShiftsEntry.COLUMN_SHIFT_PAYRATE} REAL NOT NULL DEFAULT 0, " +
                    "${ShiftsEntry.COLUMN_SHIFT_TOTALPAY} REAL NOT NULL DEFAULT 0)"
        )
        // Copy data from old table to new
        database.execSQL("INSERT INTO $ROOM_DATABASE SELECT * FROM ${ShiftsEntry.TABLE_NAME}")
        // Drop old table
        database.execSQL("DROP TABLE IF EXISTS '${ShiftsEntry.TABLE_NAME}'")
    }
}