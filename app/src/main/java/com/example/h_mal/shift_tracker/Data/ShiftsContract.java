package com.example.h_mal.shift_tracker.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by h_mal on 26/12/2017.
 */

public class ShiftsContract {

    private ShiftsContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.h_mal.shift_tracker";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SHIFTS = "shifts";

    public static final class ShiftsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SHIFTS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHIFTS;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHIFTS;


        public final static String TABLE_NAME = "shifts";

        public final static String TABLE_NAME_EXPORT = "shiftsexport";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_SHIFT_TYPE = "shifttype";

        public final static String COLUMN_SHIFT_DESCRIPTION = "description";

        public final static String COLUMN_SHIFT_DATE = "date";

        public final static String COLUMN_SHIFT_TIME_IN = "timein";

        public final static String COLUMN_SHIFT_TIME_OUT = "timeout";

        public final static String COLUMN_SHIFT_BREAK = "break";

        public final static String COLUMN_SHIFT_DURATION = "duration";

        public final static String COLUMN_SHIFT_UNIT = "unit";

        public final static String COLUMN_SHIFT_PAYRATE = "payrate";

        public final static String COLUMN_SHIFT_TOTALPAY = "totalpay";

    }
}
