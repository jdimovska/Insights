package com.example.marinaangelovska.insights.Service;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.marinaangelovska.insights.Comparators.FactorComparator;
import com.example.marinaangelovska.insights.Model.NodeContact;
import com.example.marinaangelovska.insights.Model.NodeMessage;
import com.example.marinaangelovska.insights.Model.Person;

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
    HashMap<Integer, List<NodeContact>> map;
    List<NodeContact> list;

    public PeopleService(Context context) {

        this.context = context;
        this.contactsService = new ContactsService(context);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Person> getPeople(){
        map = contactsService.getCallLogDetails();
        list = map.get(1);

        ArrayList<Person> peopleList = new ArrayList<>();
        Cursor managedCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        int display_name = managedCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int number = managedCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        while (managedCursor.moveToNext()) {
            String phDisplayName = managedCursor.getString(display_name);
            String phNumber = managedCursor.getString(number);
            if(phDisplayName.equals("Jona Dimovska")) {
               Log.i("string", "JONA");
            }
            phNumber = NormalizeNumber.normalizeNumber(phNumber);
            int factor = 0;
            for (int i=0;i < list.size(); i++) {
                String n = NormalizeNumber.normalizeNumber(list.get(i).getNumber());
                if(n.equals(phNumber)) {
                 factor = list.get(i).getOccurrence();
                 break;
                }
            }
            Person person = new Person(phDisplayName, phNumber, factor);
            if (!peopleList.contains(person))
                peopleList.add(person);

        }

        Collections.sort(peopleList, new FactorComparator());
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




}
