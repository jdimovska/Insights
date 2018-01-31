package com.example.marinaangelovska.insights.Service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.CallLog;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.marinaangelovska.insights.Model.Call;
import com.example.marinaangelovska.insights.Model.Message;
import com.example.marinaangelovska.insights.Model.Node;
import com.example.marinaangelovska.insights.Model.NodeMessage;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jona Dimovska on 31.1.2018.
 */

public class MessagesService {

    Context context;
    public MessagesService(Context context){
        this.context = context;
    }

    public List<NodeMessage> getMostFrequentMessages(List<NodeMessage> callList) {
        Collections.sort(callList, new FrequencyComparator());
        return callList;
    }

    public List<NodeMessage> getLongestMessages(List<NodeMessage> callList) {
        Collections.sort(callList, new SizeComparator());
        return callList;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<NodeMessage> getMessageLogDetails(int messageTypeHelper) {

        HashMap<String, List<Message>> messageList = new HashMap<String, List<Message>>();
        Toast.makeText(context, "MessagesService", Toast.LENGTH_LONG).show();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            Cursor managedCursor = context.getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, null);

            int body = managedCursor.getColumnIndex(Telephony.Sms.BODY);
            int phone = managedCursor.getColumnIndex(Telephony.Sms.ADDRESS);
            int date = managedCursor.getColumnIndex(Telephony.Sms.DATE);
            int type = managedCursor.getColumnIndex(Telephony.Sms.TYPE);

            while (managedCursor.moveToNext()) {

                String phPhone = managedCursor.getString(phone);
                String phBody = managedCursor.getString(body);
                String phDate = managedCursor.getString(date);
                Date formatedDate = new Date(Long.valueOf(managedCursor.getString(date)));
                String messageType = managedCursor.getString(type);
                int dircode = Integer.parseInt(messageType);

                if(dircode == messageTypeHelper) {
                    if(messageList.containsKey(phPhone)) {
                        messageList.get(phPhone).add(new Message(phPhone, formatedDate, phBody));
                    }else {
                        List<Message> singleMessageList =  new ArrayList<>();
                        singleMessageList.add(new Message(phPhone, formatedDate, phBody));
                        messageList.put(phPhone, singleMessageList);
                    }
                }



            }
            managedCursor.close();
        }
        List<NodeMessage> list = new ArrayList<NodeMessage>();
        Iterator itr = messageList.keySet().iterator();
        int totalSize = 0;
        while(itr.hasNext()){
            totalSize = 0;
            String key = itr.next().toString();
            List<Message> helper = messageList.get(key);
            for(int i=0;i<helper.size();i++){
                totalSize += helper.get(i).getContent().length();
            }
            list.add(new NodeMessage( key, totalSize, helper.size()));
        }
        return list;
    }

    class FrequencyComparator implements Comparator<NodeMessage> {
        @Override
        public int compare(NodeMessage callNodeOne, NodeMessage callNodeTwo) {
            return callNodeOne.getFrequency() < callNodeTwo.getFrequency() ? 1 : callNodeOne.getFrequency() == callNodeTwo.getFrequency() ? 0 : -1;

        }
    }

    class SizeComparator implements Comparator<NodeMessage> {
        @Override
        public int compare(NodeMessage callNodeOne, NodeMessage callNodeTwo) {
            return callNodeOne.getSize() < callNodeTwo.getSize() ? 1 : callNodeOne.getSize() == callNodeTwo.getSize() ? 0 : -1;

        }
    }
}
