package com.appttude.h_mal.farmr.data.legacydb

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns
import com.appttude.h_mal.farmr.BuildConfig

/**
 * Created by h_mal on 26/12/2017.
 */
object ShiftsContract {
    const val CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID
    val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")
    const val PATH_SHIFTS = "shifts"

    object ShiftsEntry : BaseColumns {
        val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SHIFTS)
        const val CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHIFTS
        const val CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHIFTS
        const val TABLE_NAME = "shifts"
        const val TABLE_NAME_EXPORT = "shiftsexport"
        const val _ID = BaseColumns._ID
        const val COLUMN_SHIFT_TYPE = "shifttype"
        const val COLUMN_SHIFT_DESCRIPTION = "description"
        const val COLUMN_SHIFT_DATE = "date"
        const val COLUMN_SHIFT_TIME_IN = "timein"
        const val COLUMN_SHIFT_TIME_OUT = "timeout"
        const val COLUMN_SHIFT_BREAK = "break"
        const val COLUMN_SHIFT_DURATION = "duration"
        const val COLUMN_SHIFT_UNIT = "unit"
        const val COLUMN_SHIFT_PAYRATE = "payrate"
        const val COLUMN_SHIFT_TOTALPAY = "totalpay"
    }
}