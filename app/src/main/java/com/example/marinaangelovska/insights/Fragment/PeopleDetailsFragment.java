package com.example.marinaangelovska.insights.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marinaangelovska.insights.Model.Application;
import com.example.marinaangelovska.insights.Model.NodeContact;
import com.example.marinaangelovska.insights.Model.NodeMessage;
import com.example.marinaangelovska.insights.R;
import com.example.marinaangelovska.insights.Service.ContactsService;
import com.example.marinaangelovska.insights.Service.MessagesService;

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




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_people_details, container, false);
        contactsService = new ContactsService(getActivity());
        messagesService = new MessagesService(getActivity());

        name = getArguments().getString("name");
        number = getArguments().getString("number");
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
    }

    void fillUpTextFields(HashMap<Integer, NodeContact> contactInformationContacts, HashMap<Integer, NodeMessage> contactInformationMessages) {
        personName.setText(name);
        personNumber.setText(number);

        ArrayList<Integer> callTypes = this.getCallTypes();
        for (int i = 0; i < callTypes.size(); i++) {
            switch (callTypes.get(i)) {
                case 1:
                    if(contactInformationContacts.containsKey(callTypes.get(i))) {
                        incomingFrequency.setText(String.valueOf(contactInformationContacts.get(callTypes.get(i)).getOccurrence()));
                        incomingDuration.setText(String.valueOf(contactInformationContacts.get(callTypes.get(i)).getDuration()));
                    } else {
                        incomingFrequency.setText(String.valueOf(0));
                        incomingDuration.setText(String.valueOf(0));
                    }
                    break;
                case 2:
                    if(contactInformationContacts.containsKey(callTypes.get(i))) {
                        outgoingFrequency.setText(String.valueOf(contactInformationContacts.get(callTypes.get(i)).getOccurrence()));
                        outgoingDuration.setText(String.valueOf(contactInformationContacts.get(callTypes.get(i)).getDuration()));
                    } else {
                        outgoingFrequency.setText(String.valueOf(0));
                        outgoingDuration.setText(String.valueOf(0));
                    }
                    break;
                case 3:
                    if(contactInformationContacts.containsKey(callTypes.get(i))) {
                        missedFrequency.setText(String.valueOf(contactInformationContacts.get(callTypes.get(i)).getDuration()));
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
                        incomingFrequencyMessages.setText(String.valueOf(contactInformationMessages.get(messageTypes.get(i)).getFrequency()));
                    } else {
                        incomingFrequencyMessages.setText(String.valueOf(0));
                    }
                    break;
                case 2:
                    if(contactInformationMessages.containsKey(messageTypes.get(i))) {
                        outgoingFrequencyMessages.setText(String.valueOf(contactInformationMessages.get(messageTypes.get(i)).getFrequency()));
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
