package com.appttude.h_mal.farmr.data.legacydb

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.appttude.h_mal.farmr.data.legacydb.ShiftsContract.ShiftsEntry

/**
 * Created by h_mal on 26/12/2017.
 */
class ShiftsDbHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_PRODUCTS_TABLE = ("CREATE TABLE " + ShiftsEntry.TABLE_NAME + " ("
                + ShiftsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ShiftsEntry.COLUMN_SHIFT_DESCRIPTION + " TEXT NOT NULL, "
                + ShiftsEntry.COLUMN_SHIFT_DATE + " DATE NOT NULL, "
                + ShiftsEntry.COLUMN_SHIFT_TIME_IN + " TIME NOT NULL, "
                + ShiftsEntry.COLUMN_SHIFT_TIME_OUT + " TIME NOT NULL, "
                + ShiftsEntry.COLUMN_SHIFT_BREAK + " INTEGER NOT NULL DEFAULT 0, "
                + ShiftsEntry.COLUMN_SHIFT_DURATION + " FLOAT NOT NULL DEFAULT 0, "
                + ShiftsEntry.COLUMN_SHIFT_TYPE + " TEXT NOT NULL DEFAULT " + DEFAULT_TEXT + ", "
                + ShiftsEntry.COLUMN_SHIFT_UNIT + " FLOAT NOT NULL DEFAULT 0, "
                + ShiftsEntry.COLUMN_SHIFT_PAYRATE + " FLOAT NOT NULL DEFAULT 0, "
                + ShiftsEntry.COLUMN_SHIFT_TOTALPAY + " FLOAT NOT NULL DEFAULT 0)")
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE)
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE_2)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db.execSQL(SQL_CREATE_PRODUCTS_TABLE_2)
        }
    }

    companion object {
        const val DATABASE_NAME = "shifts.db"
        private const val DATABASE_VERSION = 4
        private const val DEFAULT_TEXT = "Hourly"
        const val SQL_CREATE_PRODUCTS_TABLE_2 = ("CREATE TABLE " + ShiftsEntry.TABLE_NAME_EXPORT + " ("
                + ShiftsEntry.COLUMN_SHIFT_DESCRIPTION + " TEXT NOT NULL, "
                + ShiftsEntry.COLUMN_SHIFT_DATE + " DATE NOT NULL, "
                + ShiftsEntry.COLUMN_SHIFT_TIME_IN + " TIME NOT NULL, "
                + ShiftsEntry.COLUMN_SHIFT_TIME_OUT + " TIME NOT NULL, "
                + ShiftsEntry.COLUMN_SHIFT_BREAK + " INTEGER NOT NULL DEFAULT 0, "
                + ShiftsEntry.COLUMN_SHIFT_DURATION + " FLOAT NOT NULL DEFAULT 0, "
                + ShiftsEntry.COLUMN_SHIFT_TYPE + " TEXT NOT NULL DEFAULT " + DEFAULT_TEXT + ", "
                + ShiftsEntry.COLUMN_SHIFT_UNIT + " FLOAT NOT NULL DEFAULT 0, "
                + ShiftsEntry.COLUMN_SHIFT_PAYRATE + " FLOAT NOT NULL DEFAULT 0, "
                + ShiftsEntry.COLUMN_SHIFT_TOTALPAY + " FLOAT NOT NULL DEFAULT 0)")
    }
}