package com.example.marinaangelovska.insights.Service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.marinaangelovska.insights.Comparators.FactorComparator;
import com.example.marinaangelovska.insights.Helper.AppDatabaseHelper;
import com.example.marinaangelovska.insights.Model.Message;
import com.example.marinaangelovska.insights.Model.NodeContact;
import com.example.marinaangelovska.insights.Model.NodeMessage;
import com.example.marinaangelovska.insights.Model.Person;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jona Dimovska on 31.1.2018.
 */

public class PeopleService {

    private Context context;
    private ContactsService contactsService;
    private AppDatabaseHelper helper;
    HashMap<Integer, List<NodeContact>> map;
    List<NodeContact> list;

    public PeopleService(Context context) {

        this.context = context;
        helper = new AppDatabaseHelper(context);
        this.contactsService = new ContactsService(context);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Person> getPeople(){
        map = contactsService.getCallLogDetails();
        list = map.get(1);
        ArrayList<Person> peopleList = new ArrayList<>();
        String query = "SELECT  * FROM people";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Person person = null;
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String number = cursor.getString(2);

                int factor = 0;
                for (int i=0;i < list.size(); i++) {
                    String normalizeNumber = NormalizeNumber.normalizeNumber(list.get(i).getNumber());
                    if(normalizeNumber.equals(number)) {
                        factor = list.get(i).getOccurrence();
                        break;
                    }
                }
                person = new Person(name, number, factor);
                if (!peopleList.contains(person))
                    peopleList.add(person);



            } while (cursor.moveToNext());
        }

        Collections.sort(peopleList, new FactorComparator());
        return peopleList;
    }
}
