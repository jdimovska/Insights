package com.example.marinaangelovska.insights.Fragment;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.marinaangelovska.insights.Helper.AppDatabaseHelper;
import com.example.marinaangelovska.insights.Model.Call;
import com.example.marinaangelovska.insights.Model.NodeContact;
import com.example.marinaangelovska.insights.Model.NodeMessage;
import com.example.marinaangelovska.insights.R;
import com.example.marinaangelovska.insights.Service.MessagesService;
import com.example.marinaangelovska.insights.Service.NormalizeNumber;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.example.marinaangelovska.insights.Activity.MainActivity.dialog;
import static com.example.marinaangelovska.insights.Fragment.HomeFragment.*;

/**
 * Created by Jona Dimovska on 28.1.2018.
 */

public class MessagesFragment extends Fragment {
    MessagesService messagesService;

    AppDatabaseHelper helper;

    PieChart pieChartIncomingFrequency;
    PieChart pieChartOutgoingFrequency;

    PieChart pieChartIncomingSize;
    PieChart pieChartOutgoingSize;

    Button incomingFrequencyButton;
    Button outgoingFrequencyButton;

    Button incomingSizeButton;
    Button outgoingSizeButton;

    LinearLayout incomingFrequencyLayout;
    LinearLayout outgoingFrequencyLayout;

    LinearLayout incomingSizeLayout;
    LinearLayout outgoingSizeLayout;

    List<NodeMessage> messageListIncomingFrequency;
    List<NodeMessage> messageListOutgoingFrequency;

    List<NodeMessage> messageListIncomingSize;
    List<NodeMessage> messageListOutgoingSize;

    HashMap<Integer, List<NodeMessage>> map;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        helper = new AppDatabaseHelper(getActivity());
        messagesService = new MessagesService(getActivity());
        map = messagesService.getMessageLogDetails();

        int messageTypeInbox = Telephony.Sms.MESSAGE_TYPE_INBOX;
        int messageTypeSent = Telephony.Sms.MESSAGE_TYPE_SENT;

        messageListIncomingFrequency = messagesService.getMostFrequentMessages(creatNewInstance(map.get(messageTypeInbox)));
        messageListOutgoingFrequency = messagesService.getMostFrequentMessages(creatNewInstance(map.get(messageTypeSent)));

        messageListIncomingSize = messagesService.getLongestMessages(creatNewInstance(map.get(messageTypeInbox)));
        messageListOutgoingSize = messagesService.getLongestMessages(creatNewInstance(map.get(messageTypeSent)));

        initializingViews();

        onClickAccordion(incomingFrequencyButton, messageListIncomingFrequency, incomingFrequencyLayout, pieChartIncomingFrequency);
        onClickAccordion(outgoingFrequencyButton, messageListOutgoingFrequency, outgoingFrequencyLayout, pieChartOutgoingFrequency);
        onClickAccordion(incomingSizeButton, messageListIncomingSize, incomingSizeLayout, pieChartIncomingSize);
        onClickAccordion(outgoingSizeButton, messageListOutgoingSize, outgoingSizeLayout, pieChartOutgoingSize);

    }
    public List<NodeMessage> creatNewInstance(List<NodeMessage> temp) {
        return new ArrayList<NodeMessage>(temp);
    }
    private void onClickAccordion(final Button btn, final List<NodeMessage> callList, final LinearLayout layout, final PieChart pieChart) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout.getVisibility() == View.VISIBLE) {
                    layout.setVisibility(View.GONE);
                } else {
                    layout.setVisibility(View.VISIBLE);
                    int id = btn.getId();
                    switch (id) {
                        case R.id.incomingFrequency_btn_messages:  transformFrequencyDataToPieEntry(callList, pieChart);
                            break;
                        case R.id.outgoingFrequency_btn_messages:  transformFrequencyDataToPieEntry(callList, pieChart);
                            break;
                        case R.id.incomingSize_btn_messages:  transformSizeDataToPieEntry(callList, pieChart);
                            break;
                        case R.id.outgoingSize_btn_messages:  transformSizeDataToPieEntry(callList, pieChart);
                            break;
                    }
                }
            }

        });
    }

    public void transformSizeDataToPieEntry (List<NodeMessage> callList, PieChart pieChart) {
        ArrayList<PieEntry> pieEntryList = new ArrayList();
        double totalAll = 0;
        for(int i=0;i<callList.size();i++) {
            totalAll += callList.get(i).getSize();
        }
        int totalOthers = 0;
        if(!callList.isEmpty()) {
            if(callList.size() < 4 ) {
                for (int i = 0; i < callList.size()   ; i++){
                    int length = callList.get(i).getSize();
                    PieEntry newPEntry = new PieEntry(callList.get(i).getSize(),
                            getNameForNumber(callList.get(i).getNumber()) + ": " + length + " char");
                    pieEntryList.add(newPEntry);
                }
            } else {
                int counter = 0;
                double total = 0;
                while (total / totalAll < 0.75) {
                    int length = callList.get(counter).getSize();
                    PieEntry newPEntry = new PieEntry(callList.get(counter).getSize(),
                            getNameForNumber(callList.get(counter).getNumber()) + ": " + length + " char");
                    pieEntryList.add(newPEntry);
                    total += callList.get(counter).getSize();
                    counter++;
                }
                for (int i = counter; i <callList.size() ; i++){
                    totalOthers += callList.get(i).getSize();
                }
                PieEntry newPEntry = new PieEntry(totalOthers, "Others: " + totalOthers + " char");
                pieEntryList.add(newPEntry);
            }

        }
        addDataSetToPie(callList, pieChart, pieEntryList);
    }

    public void transformFrequencyDataToPieEntry (List<NodeMessage> callList, PieChart pieChart){
        ArrayList<PieEntry> pieEntryList = new ArrayList();
        int totalOthers = 0;
        double totalAll = 0;
        for(int i=0;i<callList.size();i++) {
            totalAll += callList.get(i).getFrequency();
        }
        if(!callList.isEmpty()) {
            if(callList.size() < 4 ) {
                for (int i = 0; i < callList.size()   ; i++){
                    int times = callList.get(i).getFrequency();
                    String s =  "s";
                    if (times == 1)
                        s = "";
                    PieEntry newPEntry = new PieEntry(callList.get(i).getFrequency(),
                            getNameForNumber(callList.get(i).getNumber()) + ": " + times + " time" + s);
                    pieEntryList.add(newPEntry);
                }
            } else {
                int counter = 0;
                double total = 0;
                while (total / totalAll < 0.75) {
                    int times = callList.get(counter).getFrequency();
                    String s =  "s";
                    if (times == 1)
                        s = "";
                    PieEntry newPEntry = new PieEntry(callList.get(counter).getFrequency(),
                            getNameForNumber(callList.get(counter).getNumber()) + ": " + times + " time" + s);
                    pieEntryList.add(newPEntry);
                    total += callList.get(counter).getFrequency();
                    counter++;
                }
                String s =  "s";
                for (int i = counter; i <callList.size() ; i++){
                    totalOthers += callList.get(i).getFrequency();
                    if (totalOthers == 1)
                        s = "";
                }
                PieEntry newPEntry = new PieEntry(totalOthers, "Others: " + totalOthers + " time" + s);
                pieEntryList.add(newPEntry);
            }
        }
        addDataSetToPie(callList, pieChart, pieEntryList);
    }

    private void addDataSetToPie(List<NodeMessage> callList, PieChart pieChart, ArrayList<PieEntry> pieEntryList) {

        PieDataSet dataSet = new PieDataSet(pieEntryList, null);
        dataSet.setSelectionShift(5f);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
        pieChart.setHoleRadius(10f);
        pieChart.setTransparentCircleAlpha(0);
        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        pieChart.setData(data);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(11f);

        pieChart.highlightValues(null);
        pieChart.setDescription( null);
        pieChart.getLegend().setEnabled(false);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(81,68,60));
        colors.add(Color.rgb(95, 128, 181));
        colors.add(Color.rgb(236,189,174));
        colors.add(Color.rgb(193,131,141));
        colors.add(Color.rgb(182, 200,227));
        colors.add(Color.rgb(170, 124, 124));

        dataSet.setColors(colors);
        pieChart.invalidate();
    }
    private String getNameForNumber(String number) {
        NormalizeNumber nm =new NormalizeNumber();
        number = nm.normalizeNumber(number);

        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("people", null, "number=?", new String[] {number }, null, null, null);


        String name = number;

        if (cursor.moveToFirst()) {
            do {
                    if(number.equals(nm.normalizeNumber(cursor.getString(2)))){
                    name = cursor.getString(1);
                    break;
                }


            } while (cursor.moveToNext());
        }
        return name;

    }

    private void initializingViews() {
        pieChartIncomingFrequency = (PieChart) getView().findViewById(R.id.idPieChartIncomingFrequency_messages);
        pieChartOutgoingFrequency = (PieChart) getView().findViewById(R.id.idPieChartOutgoingFrequency_messages);

        pieChartIncomingSize = (PieChart) getView().findViewById(R.id.idPieChartIncomingSize_messages);
        pieChartOutgoingSize = (PieChart) getView().findViewById(R.id.idPieChartOutgoingSize_messages);

        incomingFrequencyButton = (Button) getView().findViewById(R.id.incomingFrequency_btn_messages);
        outgoingFrequencyButton = (Button) getView().findViewById(R.id.outgoingFrequency_btn_messages);

        incomingSizeButton = (Button) getView().findViewById(R.id.incomingSize_btn_messages);
        outgoingSizeButton = (Button) getView().findViewById(R.id.outgoingSize_btn_messages);

        incomingFrequencyLayout = (LinearLayout) getView().findViewById(R.id.incomingFrequency_layout_messages);
        outgoingFrequencyLayout = (LinearLayout) getView().findViewById(R.id.outgoingFrequency_layout_messages);

        incomingSizeLayout = (LinearLayout) getView().findViewById(R.id.incomingSize_layout_messages);
        outgoingSizeLayout = (LinearLayout) getView().findViewById(R.id.outgoingSize_layout_messages);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        dialog.hide();
    }
    @Override
    public void onStart() {
        super.onStart();
        dialog.hide();
    }
}
