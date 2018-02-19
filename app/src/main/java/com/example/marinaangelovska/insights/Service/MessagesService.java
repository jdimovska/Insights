package com.example.marinaangelovska.insights.Service;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.ULocale;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

import com.example.marinaangelovska.insights.Comparators.FrequencyComparatorMessage;
import com.example.marinaangelovska.insights.Comparators.SizeComparator;
import com.example.marinaangelovska.insights.Model.Message;
import com.example.marinaangelovska.insights.Model.NodeMessage;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jona Dimovska on 31.1.2018.
 */

public class MessagesService {

    private Context context;
    public MessagesService(Context context){
        this.context = context;
    }

    public List<NodeMessage> getMostFrequentMessages(List<NodeMessage> callList) {
        Collections.sort(callList, new FrequencyComparatorMessage());
        return callList;
    }

    public List<NodeMessage> getLongestMessages(List<NodeMessage> callList) {
        Collections.sort(callList, new SizeComparator());
        return callList;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public  HashMap<Integer, List<NodeMessage>> getMessageLogDetails() {
        List<Integer> messageTypes = this.getMessageTypes();


        HashMap<Integer, List<NodeMessage>> allTypeMessageList = new HashMap<>();
        for(int i=0;i<messageTypes.size();i++) {
            HashMap<String, List<Message>> messageList = new HashMap<String, List<Message>>();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                Cursor managedCursor = context.getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, null);

                int body = managedCursor.getColumnIndex(Telephony.Sms.BODY);
                int phone = managedCursor.getColumnIndex(Telephony.Sms.ADDRESS);
                int date = managedCursor.getColumnIndex(Telephony.Sms.DATE);
                int type = managedCursor.getColumnIndex(Telephony.Sms.TYPE);

                while (managedCursor.moveToNext()) {

                    String phPhone = managedCursor.getString(phone);
                    phPhone = NormalizeNumber.normalizeNumber(phPhone);
                    String phBody = managedCursor.getString(body);
                    String phDate = managedCursor.getString(date);
                    Date formatedDate = new Date(Long.valueOf(managedCursor.getString(date)));
                    String messageType = managedCursor.getString(type);
                    int dircode = Integer.parseInt(messageType);

                    if (dircode == messageTypes.get(i)) {
                        if (messageList.containsKey(phPhone)) {
                            messageList.get(phPhone).add(new Message(phPhone, formatedDate, phBody));
                        } else {
                            List<Message> singleMessageList = new ArrayList<>();
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
            while (itr.hasNext()) {
                totalSize = 0;
                String key = itr.next().toString();
                List<Message> helper = messageList.get(key);
                for (int j = 0; j < helper.size(); j++) {
                    totalSize += helper.get(j).getContent().length();
                }
                list.add(new NodeMessage(key, totalSize, helper.size()));
            }
            allTypeMessageList.put(messageTypes.get(i), list);
        }
        return allTypeMessageList;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public HashMap<Integer, NodeMessage> getInformationForContact(String number) {

        HashMap<Integer, NodeMessage> informationForContact = new HashMap<>();
        ArrayList<Integer> callTypes = this.getMessageTypes();
        List<NodeMessage> contactInfoForType;
        HashMap<Integer, List<NodeMessage>> helperMap = this.getMessageLogDetails();
        for(int i = 0; i < callTypes.size(); i++) {
            contactInfoForType = helperMap.get(callTypes.get(i));
            for(int j = 0; j < contactInfoForType.size(); j++){
                String numberContact;
                numberContact = contactInfoForType.get(j).getNumber();
                if(number.equals(numberContact) ) {
                    informationForContact.put(callTypes.get(i), new NodeMessage(contactInfoForType.get(j).getNumber(),  contactInfoForType.get(j).getSize(), contactInfoForType.get(j).getFrequency()));
                    break;
                }
            }
        }
        return informationForContact;
    }

    private  ArrayList<Integer> getMessageTypes() {
        ArrayList<Integer> messageTypes = new ArrayList<>();
        messageTypes.add(Telephony.Sms.MESSAGE_TYPE_INBOX);
        messageTypes.add(Telephony.Sms.MESSAGE_TYPE_SENT);
        return messageTypes;
    }
}
