package com.example.marinaangelovska.insights.Contract;

import android.provider.BaseColumns;

/**
 * Created by Jona Dimovska on 22.2.2018.
 */

public class MessageLogDatabaseContract {
    public static class MessageLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "message_log";
        public static final String COLUMN_NAME_NUMBER = "number";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_DATE= "date";
        public static final String COLUMN_NAME_CONTENT = "content";

    }
}
