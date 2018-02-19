package com.example.marinaangelovska.insights.Fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.marinaangelovska.insights.Model.NodeContact;
import com.example.marinaangelovska.insights.Model.NodeMessage;
import com.example.marinaangelovska.insights.R;
import com.example.marinaangelovska.insights.Service.ContactsService;
import com.example.marinaangelovska.insights.Service.MessagesService;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jona Dimovska on 05.2.2018.
 */

public class PeopleDetailsFragment extends Fragment {

    ContactsService contactsService;
    MessagesService messagesService;

    View view;
    TextView personName;
    TextView personNumber;

    TextView incomingFrequency;
    TextView incomingDuration;

    TextView outgoingFrequency;
    TextView outgoingDuration;

    TextView missedFrequency;

    TextView incomingFrequencyMessages;
    TextView outgoingFrequencyMessages;

    String name;
    String number;

    DecimalFormat df = new DecimalFormat("#.#");

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_people_details, container, false);
        contactsService = new ContactsService(getActivity());
        messagesService = new MessagesService(getActivity());

        name = getArguments().getString("name");
        number = getArguments().getString("number");

        df.setRoundingMode(RoundingMode.CEILING);

        HashMap<Integer, NodeContact> contactInformationContacts = contactsService.getInformationForContact(number);
        HashMap<Integer, NodeMessage> contactInformationMessages = messagesService.getInformationForContact(number);
        setUpTextFields(view);
        fillUpTextFields(contactInformationContacts, contactInformationMessages);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void setUpTextFields(View view) {
        personName = (TextView) view.findViewById(R.id.personName);
        personNumber = (TextView) view.findViewById(R.id.personNumber);
        incomingDuration = (TextView) view.findViewById(R.id.incomingDuration);
        incomingFrequency = (TextView) view.findViewById(R.id.incomingFrequency);

        outgoingDuration = (TextView) view.findViewById(R.id.outgoingDuration);
        outgoingFrequency = (TextView) view.findViewById(R.id.outgoingFrequency);

        incomingFrequencyMessages = (TextView) view.findViewById(R.id.incomingFrequencyMessages);
        outgoingFrequencyMessages = (TextView) view.findViewById(R.id.outgoingFrequencyMessages);

        missedFrequency = (TextView) view.findViewById(R.id.missedFrequency);

        personNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            123);
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + personNumber.getText()));
                    startActivity(intent);
                }
            }
        });
    }

    void fillUpTextFields(HashMap<Integer, NodeContact> contactInformationContacts, HashMap<Integer, NodeMessage> contactInformationMessages) {
        personName.setText(name);
        personNumber.setText(number);

        ArrayList<Integer> callTypes = this.getCallTypes();
        for (int i = 0; i < callTypes.size(); i++) {
            switch (callTypes.get(i)) {
                case 1:
                    if(contactInformationContacts.containsKey(callTypes.get(i))) {
                        String s = "s";
                        if(contactInformationContacts.get(callTypes.get(i)).getOccurrence() == 1)
                            s = "" ;
                        incomingFrequency.setText(String.valueOf(contactInformationContacts.get(callTypes.get(i)).getOccurrence()) + " time" + s);
                        incomingDuration.setText(String.valueOf(df.format(contactInformationContacts.get(callTypes.get(i)).getDuration() / 60f)) + " min");
                    } else {
                        incomingFrequency.setText(String.valueOf(0));
                        incomingDuration.setText(String.valueOf(0));
                    }
                    break;
                case 2:
                    if(contactInformationContacts.containsKey(callTypes.get(i))) {
                        String s = "s";
                        if(contactInformationContacts.get(callTypes.get(i)).getOccurrence() == 1)
                            s = "" ;
                        outgoingFrequency.setText(String.valueOf(contactInformationContacts.get(callTypes.get(i)).getOccurrence()) + " time" + s);
                        outgoingDuration.setText(String.valueOf(df.format(contactInformationContacts.get(callTypes.get(i)).getDuration() / 60f)) + " min");
                    } else {
                        outgoingFrequency.setText(String.valueOf(0));
                        outgoingDuration.setText(String.valueOf(0));
                    }
                    break;
                case 3:
                    if(contactInformationContacts.containsKey(callTypes.get(i))) {
                        String s = "s";
                        if(contactInformationContacts.get(callTypes.get(i)).getOccurrence() == 1)
                            s = "" ;
                        missedFrequency.setText(String.valueOf(contactInformationContacts.get(callTypes.get(i)).getOccurrence()) + " time" + s);
                    } else {
                        missedFrequency.setText(String.valueOf(0));

                    }
                    break;
            }
        }

        ArrayList<Integer> messageTypes = this.getMessageTypes();
        for (int i = 0; i < messageTypes.size(); i++) {
            switch (messageTypes.get(i)) {
                case 1:
                    if(contactInformationMessages.containsKey(messageTypes.get(i))) {
                        String s = "s";
                        if(contactInformationMessages.get(messageTypes.get(i)).getFrequency() == 1)
                            s = "" ;
                        incomingFrequencyMessages.setText(String.valueOf(contactInformationMessages.get(messageTypes.get(i)).getFrequency()) + " time" + s);
                    } else {
                        incomingFrequencyMessages.setText(String.valueOf(0));
                    }
                    break;
                case 2:
                    if(contactInformationMessages.containsKey(messageTypes.get(i))) {
                        String s = "s";
                        if(contactInformationMessages.get(messageTypes.get(i)).getFrequency() == 1)
                            s = "" ;
                        outgoingFrequencyMessages.setText(String.valueOf(contactInformationMessages.get(messageTypes.get(i)).getFrequency()) + " time" + s);
                    } else {
                        outgoingFrequencyMessages.setText(String.valueOf(0));

                    }
                    break;
            }
        }
    }

    private ArrayList<Integer> getCallTypes() {
        ArrayList<Integer> callTypes = new ArrayList<>();
        callTypes.add(CallLog.Calls.INCOMING_TYPE);
        callTypes.add(CallLog.Calls.OUTGOING_TYPE);
        callTypes.add(CallLog.Calls.MISSED_TYPE);
        return callTypes;
    }

    private ArrayList<Integer> getMessageTypes() {
        ArrayList<Integer> messageTypes = new ArrayList<>();
        messageTypes.add(Telephony.Sms.MESSAGE_TYPE_INBOX);
        messageTypes.add(Telephony.Sms.MESSAGE_TYPE_SENT);
        return messageTypes;
    }

}
