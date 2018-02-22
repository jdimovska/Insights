package com.example.marinaangelovska.insights.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.marinaangelovska.insights.Contract.CallLogDatabaseContract;
import com.example.marinaangelovska.insights.Contract.MessageLogDatabaseContract;
import com.example.marinaangelovska.insights.Contract.PeopleContract;

/**
 * Created by Jona Dimovska on 22.2.2018.
 */

public class AppDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "insights.db";

    public static final String SQL_CREATE_CALL_LOG =
            "CREATE TABLE " + CallLogDatabaseContract.CallLogEntry.TABLE_NAME + " (" +
                    CallLogDatabaseContract.CallLogEntry._ID + " INTEGER PRIMARY KEY," +
                    CallLogDatabaseContract.CallLogEntry.COLUMN_NAME_NAME + " TEXT," +
                    CallLogDatabaseContract.CallLogEntry.COLUMN_NAME_TYPE + " TEXT," +
                    CallLogDatabaseContract.CallLogEntry.COLUMN_NAME_NUMBER + " TEXT," +
                    CallLogDatabaseContract.CallLogEntry.COLUMN_NAME_DATE + " TEXT," +
                    CallLogDatabaseContract.CallLogEntry.COLUMN_NAME_DURATION + " TEXT);";

    public static final String SQL_CREATE_MESSAGE_LOG =
            "CREATE TABLE " + MessageLogDatabaseContract.MessageLogEntry.TABLE_NAME + " (" +
                    MessageLogDatabaseContract.MessageLogEntry._ID + " INTEGER PRIMARY KEY," +
                    MessageLogDatabaseContract.MessageLogEntry.COLUMN_NAME_NUMBER + " TEXT," +
                    MessageLogDatabaseContract.MessageLogEntry.COLUMN_NAME_TYPE + " TEXT," +
                    MessageLogDatabaseContract.MessageLogEntry.COLUMN_NAME_DATE + " TEXT," +
                    MessageLogDatabaseContract.MessageLogEntry.COLUMN_NAME_CONTENT + " TEXT);";

    public static final String SQL_CREATE_PEOPLE =
            "CREATE TABLE " + PeopleContract.PeopleEntry.TABLE_NAME + " (" +
                    PeopleContract.PeopleEntry._ID + " INTEGER PRIMARY KEY," +
                    PeopleContract.PeopleEntry.COLUMN_NAME_NAME + " TEXT," +
                    PeopleContract.PeopleEntry.COLUMN_NAME_NUMBER + " TEXT," +
                    PeopleContract.PeopleEntry.COLUMN_NAME_FACTOR + " TEXT);";



    public AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CALL_LOG);
        db.execSQL(SQL_CREATE_MESSAGE_LOG);
        db.execSQL(SQL_CREATE_PEOPLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
