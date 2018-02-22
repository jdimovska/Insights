package com.example.marinaangelovska.insights.Contract;

import android.provider.BaseColumns;

/**
 * Created by Jona Dimovska on 22.2.2018.
 */

public class PeopleContract {

    public static class PeopleEntry implements BaseColumns {
        public static final String TABLE_NAME = "people";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_NUMBER = "number";
        public static final String COLUMN_NAME_FACTOR = "factor";

    }
}
