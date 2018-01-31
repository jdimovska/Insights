package com.example.marinaangelovska.insights.Fragment;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.marinaangelovska.insights.Model.Message;
import com.example.marinaangelovska.insights.Model.Node;
import com.example.marinaangelovska.insights.Model.NodeMessage;
import com.example.marinaangelovska.insights.R;
import com.example.marinaangelovska.insights.Service.ContactsService;
import com.example.marinaangelovska.insights.Service.MessagesService;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jona Dimovska on 28.1.2018.
 */

public class MessagesFragment extends Fragment {
    MessagesService messagesService;

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
        messagesService = new MessagesService(getActivity());
        map = messagesService.getMessageLogDetails();

        int messageTypeInbox = Telephony.Sms.MESSAGE_TYPE_INBOX;
        int messageTypeSent = Telephony.Sms.MESSAGE_TYPE_SENT;

        messageListIncomingFrequency = messagesService.getMostFrequentMessages(map.get(messageTypeInbox));
        messageListOutgoingFrequency = messagesService.getMostFrequentMessages(map.get(messageTypeSent));

        messageListIncomingSize = messagesService.getLongestMessages(map.get(messageTypeInbox));
        messageListOutgoingSize = messagesService.getLongestMessages(map.get(messageTypeSent));

        initializingViews();

        onClickAccordion(incomingFrequencyButton, messageListIncomingFrequency, incomingFrequencyLayout, pieChartIncomingFrequency);
        onClickAccordion(outgoingFrequencyButton, messageListOutgoingFrequency, outgoingFrequencyLayout, pieChartOutgoingFrequency);
        onClickAccordion(incomingSizeButton, messageListIncomingSize, incomingSizeLayout, pieChartIncomingSize);
        onClickAccordion(outgoingSizeButton, messageListOutgoingSize, outgoingSizeLayout, pieChartOutgoingSize);

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
        int totalOthers = 0;
        if(!callList.isEmpty()) {
            if(callList.size() < 4 ) {
                for (int i = 0; i < callList.size()   ; i++){
                    int length = callList.get(i).getSize();
                    PieEntry newPEntry = new PieEntry(callList.get(i).getSize(),
                            callList.get(i).getNumber() + ": " + length + " char");
                    pieEntryList.add(newPEntry);
                }
            } else {
                for (int i = 0; i < 4 ; i++){
                    int length = callList.get(i).getSize();
                    PieEntry newPEntry = new PieEntry(callList.get(i).getSize(),
                            callList.get(i).getNumber() + ": " + length + " char");
                    pieEntryList.add(newPEntry);
                }
                for (int i = 4; i <callList.size() ; i++){
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
        if(!callList.isEmpty()) {
            if(callList.size() < 4 ) {
                for (int i = 0; i < callList.size()   ; i++){
                    int times = callList.get(i).getFrequency();
                    PieEntry newPEntry = new PieEntry(callList.get(i).getFrequency(),
                            callList.get(i).getNumber() + ": " + times + " times");
                    pieEntryList.add(newPEntry);
                }
            } else {
                for (int i = 0; i < 4 ; i++){
                    int times = callList.get(i).getFrequency();
                    PieEntry newPEntry = new PieEntry(callList.get(i).getFrequency(),
                            callList.get(i).getNumber() + ": " + times + " times");
                    pieEntryList.add(newPEntry);
                }
                for (int i = 4; i <callList.size() ; i++){
                    totalOthers += callList.get(i).getFrequency();
                }
                PieEntry newPEntry = new PieEntry(totalOthers, "Others: " + totalOthers + " times");
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
        pieChart.setEntryLabelTextSize(10f);

        pieChart.highlightValues(null);
        pieChart.setDescription( null);
        pieChart.getLegend().setEnabled(false);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(88,140,155));
        colors.add(Color.rgb(114,88,77));
        colors.add(Color.rgb(242,174,114));
        colors.add(Color.rgb(217, 100,89));
        colors.add(Color.rgb(140,70,70));
        dataSet.setColors(colors);
        pieChart.invalidate();
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
}
