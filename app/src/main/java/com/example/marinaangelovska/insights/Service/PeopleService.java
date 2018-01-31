package com.example.marinaangelovska.insights.Service;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.marinaangelovska.insights.Model.NodeMessage;
import com.example.marinaangelovska.insights.Model.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
            if (!peopleList.contains(person))
                peopleList.add(person);

        }
        Collections.sort(peopleList, new NameComparator());
        ArrayList<Person> uniqueList = new ArrayList<>();
        int counter = 0;
        if(!peopleList.isEmpty())
            uniqueList.add(peopleList.get(0));
        for(int i = 1; i < peopleList.size(); i++) {
            if(!uniqueList.get(counter).getName().equals(peopleList.get(i).getName())) {
                uniqueList.add(peopleList.get(i));
                counter++;
            }
        }
        return uniqueList;
    }
    class NameComparator implements Comparator<Person> {
        @Override
        public int compare(Person person1, Person person2) {
            return (person1.getName()).compareTo(person2.getName());
        }
    }
}
