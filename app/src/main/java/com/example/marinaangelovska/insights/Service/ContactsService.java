package com.example.marinaangelovska.insights.Service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;

import com.example.marinaangelovska.insights.Model.Call;
import com.example.marinaangelovska.insights.Model.NodeContact;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jona Dimovska on 28.1.2018.
 */

public class ContactsService {

    private Context context;

    public ContactsService(Context context) {
        this.context = context;
    }

    public List<NodeContact> getMostFrequentCalls(List<NodeContact> callList1) {
        Collections.sort(callList1, new FrequencyComparator());
        return callList1;
    }

    public List<NodeContact> getLongestCalls(List<NodeContact> callList2) {
        Collections.sort(callList2, new DurationComparator());
        return callList2;
    }

    private  ArrayList<Integer> getCallTypes() {
        ArrayList<Integer> callTypes = new ArrayList<>();
        callTypes.add(CallLog.Calls.INCOMING_TYPE);
        callTypes.add(CallLog.Calls.OUTGOING_TYPE);
        callTypes.add(CallLog.Calls.MISSED_TYPE);
        return callTypes;

    }

    public  HashMap<Integer, List<NodeContact>> getCallLogDetails() {

        List<Integer> callTypes = this.getCallTypes();

        HashMap<Integer, List<NodeContact>> allTypeCallsList = new HashMap<>();
        for(int i=0; i < callTypes.size(); i++) {
            HashMap<String, List<Call>> callList = new HashMap<String, List<Call>>();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

                int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);

                while (managedCursor.moveToNext()) {
                    String phNumber = managedCursor.getString(number);
                    String callType = managedCursor.getString(type);
                    String phName = managedCursor.getString(name);
                    Date callDayTime = new Date(Long.valueOf(managedCursor.getString(date)));
                    String callDuration = managedCursor.getString(duration);
                    int dircode = Integer.parseInt(callType);

                    if (dircode == callTypes.get(i)) {
                        if (callList.containsKey(phNumber)) {
                            callList.get(phNumber).add(new Call(phName, callDayTime, callDuration));
                        } else {
                            List<Call> singleCallList = new ArrayList<>();
                            singleCallList.add(new Call(phName, callDayTime, callDuration));
                            callList.put(phNumber, singleCallList);
                        }
                    }
                }
                managedCursor.close();
            }

            List<NodeContact> list = new ArrayList<>();
            Iterator itr = callList.keySet().iterator();
            int totalDuration = 0;
            while (itr.hasNext()) {
                totalDuration = 0;
                String key = itr.next().toString();
                List<Call> helper = callList.get(key);
                for (int j = 0; j < helper.size(); j++) {
                    totalDuration += Integer.parseInt(helper.get(j).getDuration());
                }
                list.add(new NodeContact(callList.get(key).get(0).getName(), key, callList.get(key).size(), totalDuration));
            }
            allTypeCallsList.put(callTypes.get(i), list);
        }
        return allTypeCallsList;
    }

    public Long getTodaysCallsDuration(){
        Long totalDuration = Long.valueOf(0);
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED){
            Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

            while (managedCursor.moveToNext()) {
                Date callDayTime = new Date(Long.valueOf(managedCursor.getString(date)));
                Long callDuration = Long.parseLong(managedCursor.getString(duration));
                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                if(today.getTime().before(callDayTime)){
                    totalDuration += callDuration;
                }

            }
        }
        return totalDuration;
    }

    class FrequencyComparator implements Comparator<NodeContact> {
        @Override
        public int compare(NodeContact callNodeContactOne, NodeContact callNodeContactTwo) {
            return callNodeContactOne.getOccurrence() < callNodeContactTwo.getOccurrence() ? 1 : callNodeContactOne.getOccurrence() == callNodeContactTwo.getOccurrence() ? 0 : -1;
        }
    }

    class DurationComparator implements Comparator<NodeContact> {
        @Override
        public int compare(NodeContact callNodeContactOne, NodeContact callNodeContactTwo) {
            return callNodeContactOne.getDuration() < callNodeContactTwo.getDuration() ? 1 : callNodeContactOne.getDuration() == callNodeContactTwo.getDuration() ? 0 : -1;
        }
    }

    public HashMap<Integer, NodeContact> getInformationForContact(String number) {

        HashMap<Integer, NodeContact> informationForContact = new HashMap<>();
        ArrayList<Integer> callTypes = this.getCallTypes();
        List<NodeContact> contactInfoForType;
        HashMap<Integer, List<NodeContact>> helperMap = this.getCallLogDetails();
        for(int i = 0; i < callTypes.size(); i++) {
            contactInfoForType = helperMap.get(callTypes.get(i));
            for(int j = 0; j < contactInfoForType.size(); j++){
                String numberContact = contactInfoForType.get(j).getNumber();
                if(number.equals(numberContact)) {
                    informationForContact.put(callTypes.get(i), new NodeContact(contactInfoForType.get(j).getName(), contactInfoForType.get(j).getNumber(), contactInfoForType.get(j).getOccurrence(), contactInfoForType.get(j).getDuration()));
                    break;
                }
            }
        }
       return informationForContact;
    }
}
