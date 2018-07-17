package com.example.h_mal.shift_tracker.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.h_mal.shift_tracker.Data.ShiftsContract.ShiftsEntry;

/**
 * Created by h_mal on 26/12/2017.
 */

public class ShiftsDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ShiftsDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "shifts.db";

    private static final int DATABASE_VERSION = 4;

    private static String DEFAULT_TEXT = "Hourly";


    public ShiftsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PRODUCTS_TABLE =  "CREATE TABLE " + ShiftsEntry.TABLE_NAME + " ("
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
                + ShiftsEntry.COLUMN_SHIFT_TOTALPAY + " FLOAT NOT NULL DEFAULT 0)";


        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE_2);
    }

    private static final String SQL_CREATE_PRODUCTS_TABLE_2 =  "CREATE TABLE " + ShiftsEntry.TABLE_NAME_EXPORT + " ("
            + ShiftsEntry.COLUMN_SHIFT_DESCRIPTION + " TEXT NOT NULL, "
            + ShiftsEntry.COLUMN_SHIFT_DATE + " DATE NOT NULL, "
            + ShiftsEntry.COLUMN_SHIFT_TIME_IN + " TIME NOT NULL, "
            + ShiftsEntry.COLUMN_SHIFT_TIME_OUT + " TIME NOT NULL, "
            + ShiftsEntry.COLUMN_SHIFT_BREAK + " INTEGER NOT NULL DEFAULT 0, "
            + ShiftsEntry.COLUMN_SHIFT_DURATION + " FLOAT NOT NULL DEFAULT 0, "
            + ShiftsEntry.COLUMN_SHIFT_TYPE + " TEXT NOT NULL DEFAULT " + DEFAULT_TEXT + ", "
            + ShiftsEntry.COLUMN_SHIFT_UNIT + " FLOAT NOT NULL DEFAULT 0, "
            + ShiftsEntry.COLUMN_SHIFT_PAYRATE + " FLOAT NOT NULL DEFAULT 0, "
            + ShiftsEntry.COLUMN_SHIFT_TOTALPAY + " FLOAT NOT NULL DEFAULT 0)";

    private static final String DATABASE_ALTER_1 =
            "ALTER TABLE " + ShiftsEntry.TABLE_NAME  + " ADD COLUMN "
                    + ShiftsEntry.COLUMN_SHIFT_TYPE + " TEXT NOT NULL DEFAULT " + DEFAULT_TEXT + ";";

    private static final String DATABASE_ALTER_2 =
            "ALTER TABLE " + ShiftsEntry.TABLE_NAME  + " ADD COLUMN "
                    + ShiftsEntry.COLUMN_SHIFT_UNIT + " FLOAT NOT NULL DEFAULT 0;";

    private static final String DATABASE_ALTER_3 =
            "ALTER TABLE " + ShiftsEntry.TABLE_NAME  + " ADD COLUMN "
                    + ShiftsEntry.COLUMN_SHIFT_PAYRATE + " FLOAT NOT NULL DEFAULT 0;";

    private static final String DATABASE_ALTER_4 =
            "ALTER TABLE " + ShiftsEntry.TABLE_NAME  + " ADD COLUMN "
                    + ShiftsEntry.COLUMN_SHIFT_TOTALPAY + " FLOAT NOT NULL DEFAULT 0;";

    private static final String DATABASE_UPDATE_1 =
            "UPDATE " + ShiftsEntry.TABLE_NAME  + " SET "
                    + ShiftsEntry.COLUMN_SHIFT_TYPE + " = replace( " + ShiftsEntry.COLUMN_SHIFT_TYPE + ", 'hourly', 'Hourly' )" +
    " WHERE " + ShiftsEntry.COLUMN_SHIFT_TYPE + " LIKE 'hourly%'";

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL(SQL_CREATE_PRODUCTS_TABLE_2);
        }
    }
}
