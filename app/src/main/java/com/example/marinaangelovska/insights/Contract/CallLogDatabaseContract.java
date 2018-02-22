package com.example.marinaangelovska.insights.Contract;

import android.provider.BaseColumns;

/**
 * Created by Jona Dimovska on 22.2.2018.
 */

public class CallLogDatabaseContract {

    public static class CallLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "call_log";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_NUMBER = "number";
        public static final String COLUMN_NAME_DATE= "date";
        public static final String COLUMN_NAME_DURATION = "duration";

    }
}
