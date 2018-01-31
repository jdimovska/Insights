package com.example.marinaangelovska.insights.Service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;

import com.example.marinaangelovska.insights.Model.Call;
import com.example.marinaangelovska.insights.Model.Node;
import com.example.marinaangelovska.insights.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparingInt;

/**
 * Created by Jona Dimovska on 28.1.2018.
 */

public class ContactsService {

    private Context context;

    public ContactsService(Context context) {
        this.context = context;
    }

    public List<Node> getMostFrequentCalls(List<Node> callList) {
        Collections.sort(callList, new FrequencyComparator());
        return callList;
    }

    public List<Node> getLongestCalls( List<Node> callList) {
        Collections.sort(callList, new DurationComparator());
        return callList;
    }

    public  List<Node> getCallLogDetails(int callTypeHelper) {


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
                String phName =  managedCursor.getString(name);
                Date callDayTime = new Date(Long.valueOf(managedCursor.getString(date)));
                String callDuration = managedCursor.getString(duration);
                int dircode = Integer.parseInt(callType);

                if(callTypeHelper == 555) {
                    if((dircode == CallLog.Calls.INCOMING_TYPE) || (dircode == CallLog.Calls.OUTGOING_TYPE )) {
                        if(callList.containsKey(phNumber)) {
                            callList.get(phNumber).add(new Call(phName, callDayTime, callDuration));
                        }else {
                            List<Call> singleCallList =  new ArrayList<>();
                            singleCallList.add(new Call(phName, callDayTime, callDuration));
                            callList.put(phNumber, singleCallList);
                        }
                    }
                } else {
                    if(dircode == callTypeHelper) {
                        if(callList.containsKey(phNumber)) {
                            callList.get(phNumber).add(new Call(phName, callDayTime, callDuration));
                        }else {
                            List<Call> singleCallList =  new ArrayList<>();
                            singleCallList.add(new Call(phName, callDayTime, callDuration));
                            callList.put(phNumber, singleCallList);
                        }
                    }
                }
            }
            managedCursor.close();
        }

        List<Node> list = new ArrayList<Node>();
        Iterator itr = callList.keySet().iterator();
        int totalDuration = 0;
        while(itr.hasNext()){
            totalDuration = 0;
            String key = itr.next().toString();
            List<Call> helper = callList.get(key);
            for(int i=0;i<helper.size();i++){
                totalDuration += Integer.parseInt(helper.get(i).getDuration());
            }
            list.add(new Node(callList.get(key).get(0).getName(), key, callList.get(key).size(), totalDuration));
        }
        return list;
    }

    class FrequencyComparator implements Comparator<Node> {
        @Override
        public int compare(Node callNodeOne, Node callNodeTwo) {
            return callNodeOne.getOccurrence() < callNodeTwo.getOccurrence() ? 1 : callNodeOne.getOccurrence() == callNodeTwo.getOccurrence() ? 0 : -1;
        }
    }

    class DurationComparator implements Comparator<Node> {
        @Override
        public int compare(Node callNodeOne, Node callNodeTwo) {
            return callNodeOne.getDuration() < callNodeTwo.getDuration() ? 1 : callNodeOne.getDuration() == callNodeTwo.getDuration() ? 0 : -1;
        }
    }


}
