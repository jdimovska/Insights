package com.example.marinaangelovska.insights.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marinaangelovska.insights.Model.Application;
import com.example.marinaangelovska.insights.Model.NodeContact;
import com.example.marinaangelovska.insights.R;
import com.example.marinaangelovska.insights.Service.ContactsService;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jona Dimovska on 05.2.2018.
 */

public class PeopleDetailsFragment extends Fragment {

    ContactsService contactsService;
    View view;
    TextView personName;
    TextView personNumber;

    TextView incomingFrequency;
    TextView incomingDuration;

    TextView outgoingFrequency;
    TextView outgoingDuration;

    TextView missedFrequency;

    String name;
    String number;




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_people_details, container, false);
        contactsService = new ContactsService(getActivity());
        name = getArguments().getString("name");
        number = getArguments().getString("number");
        HashMap<Integer, NodeContact> contactInformation = contactsService.getInformationForContact(number);
        ArrayList<Integer> callTypes = this.getCallTypes();
        setUpTextFields(view);
        fillUpTextFields(contactInformation);
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

        missedFrequency = (TextView) view.findViewById(R.id.missedFrequency);
    }

    void fillUpTextFields(HashMap<Integer, NodeContact> contactInformation) {
        personName.setText(name);
        personNumber.setText(number);
        if(contactInformation.size()!=0) {
            ArrayList<Integer> callTypes = this.getCallTypes();
            for (int i = 0; i < callTypes.size(); i++) {
                if(contactInformation.containsKey(callTypes.get(i))) {
                    switch (callTypes.get(i)) {
                        case 1:
                            incomingFrequency.setText(String.valueOf(contactInformation.get(callTypes.get(i)).getOccurrence()));
                            incomingDuration.setText(String.valueOf(contactInformation.get(callTypes.get(i)).getDuration()));
                            break;
                        case 2:
                            outgoingFrequency.setText(String.valueOf(contactInformation.get(callTypes.get(i)).getOccurrence()));
                            outgoingDuration.setText(String.valueOf(contactInformation.get(callTypes.get(i)).getDuration()));
                            break;
                        case 3:
                            missedFrequency.setText(String.valueOf(contactInformation.get(callTypes.get(i)).getDuration()));
                            break;
                    }
                }
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
}
