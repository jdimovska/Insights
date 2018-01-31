package com.example.marinaangelovska.insights.Service;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.marinaangelovska.insights.Model.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jona Dimovska on 31.1.2018.
 */

public class PeopleService {

    private Context context;
    public PeopleService(Context context) {
        this.context = context;
    }

    public ArrayList<Person> getPeople(){
        ArrayList<Person> peopleList = new ArrayList<>();
        Cursor managedCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        int display_name = managedCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int number = managedCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        while (managedCursor.moveToNext()) {
            String phDisplayName = managedCursor.getString(display_name);
            String phNumber = managedCursor.getString(number);
            Person person = new Person(phDisplayName, phNumber);
            peopleList.add(person);
        }
        return peopleList;
    }

}
