package com.example.h_mal.shift_tracker.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.h_mal.shift_tracker.Data.ShiftsContract.ShiftsEntry;

/**
 * Created by h_mal on 26/12/2017.
 */

public class ShiftProvider extends ContentProvider {


    public static final String LOG_TAG = ShiftProvider.class.getSimpleName();

    private static final int SHIFTS = 100;
    private static final int SHIFT_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {

        sUriMatcher.addURI(ShiftsContract.CONTENT_AUTHORITY, ShiftsContract.PATH_SHIFTS, SHIFTS);

        sUriMatcher.addURI(ShiftsContract.CONTENT_AUTHORITY, ShiftsContract.PATH_SHIFTS + "/#", SHIFT_ID);
    }

    ShiftsDbHelper mDbHelper;

    public ShiftProvider() {
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ShiftsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case SHIFTS:

                cursor = database.query(ShiftsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SHIFT_ID:

                selection = ShiftsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(ShiftsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SHIFTS:
                return insertShift(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertShift(Uri uri, ContentValues values) {

        String description = values.getAsString(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION);
        if (description == null) {
            throw new IllegalArgumentException("Description required");
        }

        String date = values.getAsString(ShiftsEntry.COLUMN_SHIFT_DATE);
        if (date == null) {
            throw new IllegalArgumentException("Date required");
        }

        String timeIn = values.getAsString(ShiftsEntry.COLUMN_SHIFT_TIME_IN);
        if (timeIn == null) {
            throw new IllegalArgumentException("Time In required");
        }

        String timeOut = values.getAsString(ShiftsEntry.COLUMN_SHIFT_TIME_OUT);
        if (timeOut == null) {
            throw new IllegalArgumentException("Time Out required");
        }

        Float duration = values.getAsFloat(ShiftsEntry.COLUMN_SHIFT_DURATION);
        if (duration < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }

        String shiftType = values.getAsString(ShiftsEntry.COLUMN_SHIFT_TYPE);
        if (shiftType == null) {
            throw new IllegalArgumentException("Shift type required");
        }

        Float shiftUnits = values.getAsFloat(ShiftsEntry.COLUMN_SHIFT_UNIT);
        if (shiftUnits < 0) {
            throw new IllegalArgumentException("Units cannot be negative");
        }

        Float payRate = values.getAsFloat(ShiftsEntry.COLUMN_SHIFT_PAYRATE);
        if (payRate < 0) {
            throw new IllegalArgumentException("Pay rate cannot be negative");
        }

        Float totalPay = values.getAsFloat(ShiftsEntry.COLUMN_SHIFT_TOTALPAY);
        if (totalPay < 0) {
            throw new IllegalArgumentException("Total Pay cannot be negative");
        }

        Integer breakMins = values.getAsInteger(ShiftsEntry.COLUMN_SHIFT_BREAK);
        if (breakMins < 0) {
            throw new IllegalArgumentException("Break cannot be negative");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(ShiftsEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "row failed " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SHIFTS:
                return updateShift(uri, contentValues, selection, selectionArgs);
            case SHIFT_ID:

                selection = ShiftsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateShift(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    private int updateShift(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION)) {
            String description = values.getAsString(ShiftsEntry.COLUMN_SHIFT_DESCRIPTION);
            if (description == null) {
                throw new IllegalArgumentException("description required");
            }
        }

        if (values.containsKey(ShiftsEntry.COLUMN_SHIFT_DATE)) {
            String date = values.getAsString(ShiftsEntry.COLUMN_SHIFT_DATE);
            if (date == null) {
                throw new IllegalArgumentException("date required");
            }
        }

        if (values.containsKey(ShiftsEntry.COLUMN_SHIFT_TIME_IN)) {
            String timeIn = values.getAsString(ShiftsEntry.COLUMN_SHIFT_TIME_IN);
            if (timeIn == null) {
                throw new IllegalArgumentException("time in required");
            }
        }

        if (values.containsKey(ShiftsEntry.COLUMN_SHIFT_TIME_OUT)) {
            String timeOut = values.getAsString(ShiftsEntry.COLUMN_SHIFT_TIME_OUT);
            if (timeOut == null) {
                throw new IllegalArgumentException("time out required");
            }
        }

        if (values.containsKey(ShiftsEntry.COLUMN_SHIFT_BREAK)) {
            String breaks = values.getAsString(ShiftsEntry.COLUMN_SHIFT_BREAK);
            if (breaks == null) {
                throw new IllegalArgumentException("break required");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ShiftsEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SHIFTS:
                rowsDeleted = database.delete(ShiftsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SHIFT_ID:
                selection = ShiftsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ShiftsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SHIFTS:
                return ShiftsEntry.CONTENT_LIST_TYPE;
            case SHIFT_ID:
                return ShiftsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
