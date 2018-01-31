package com.example.marinaangelovska.insights.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.marinaangelovska.insights.Model.Call;
import com.example.marinaangelovska.insights.Model.Node;
import com.example.marinaangelovska.insights.R;
import com.example.marinaangelovska.insights.Service.ContactsService;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jona Dimovska on 28.1.2018.
 */

public class ContactsFragment extends Fragment {
    ContactsService contactsService;

    PieChart pieChartOutgoingDuration;
    PieChart pieChartIncomingDuration;
    PieChart pieChartTotalDuration;

    PieChart pieChartOutgoingFrequency;
    PieChart pieChartIncomingFrequency;
    PieChart pieChartTotalFrequency;

    Button incomingDurationButton;
    Button outgoingDurationButton;
    Button totalDurationButton;

    Button incomingFrequencyButton;
    Button outgoingFrequencyButton;
    Button totalFrequencyButton;

    LinearLayout incomingDurationLayout;
    LinearLayout outgoingDurationLayout;
    LinearLayout totalDurationLayout;

    LinearLayout incomingFrequencyLayout;
    LinearLayout outgoingFrequencyLayout;
    LinearLayout totalFrequencyLayout;

    List<Node> callListOutgoingDuration;
    List<Node> callListIncomingDuration;
    List<Node> callListTotalDuration;

    List<Node> callListIncomingFrequency;
    List<Node> callListOutgoingFrequency;
    List<Node> callListTotalFrequency;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contactsService = new ContactsService(getActivity());

        int callTypeIncoming = CallLog.Calls.INCOMING_TYPE;
        int callTypeOutgoing = CallLog.Calls.OUTGOING_TYPE;
        int callTypeTotal = 555;

        callListIncomingDuration = contactsService.getLongestCalls(contactsService.getCallLogDetails(callTypeIncoming));
        callListOutgoingDuration = contactsService.getLongestCalls(contactsService.getCallLogDetails(callTypeOutgoing));

        callListIncomingFrequency = contactsService.getMostFrequentCalls(contactsService.getCallLogDetails(callTypeIncoming));
        callListOutgoingFrequency = contactsService.getMostFrequentCalls(contactsService.getCallLogDetails(callTypeOutgoing));

        callListTotalDuration = contactsService.getLongestCalls(contactsService.getCallLogDetails(callTypeTotal));
        callListTotalFrequency = contactsService.getLongestCalls(contactsService.getCallLogDetails(callTypeTotal));

        initializingViews();

        onClickAccordion(incomingDurationButton, callListIncomingDuration, incomingDurationLayout, pieChartIncomingDuration);
        onClickAccordion(outgoingDurationButton, callListOutgoingDuration, outgoingDurationLayout, pieChartOutgoingDuration);
        onClickAccordion(outgoingFrequencyButton, callListOutgoingFrequency, outgoingFrequencyLayout, pieChartOutgoingFrequency);
        onClickAccordion(incomingFrequencyButton, callListIncomingFrequency, incomingFrequencyLayout, pieChartIncomingFrequency);
        onClickAccordion(totalDurationButton, callListTotalDuration, totalDurationLayout, pieChartTotalDuration);
        onClickAccordion(totalFrequencyButton, callListTotalFrequency, totalFrequencyLayout, pieChartTotalFrequency);
    }
    private void initializingViews() {
        pieChartOutgoingDuration = (PieChart) getView().findViewById(R.id.idPieChartOutgoingDuration);
        pieChartIncomingDuration = (PieChart) getView().findViewById(R.id.idPieChartIncomingDuration);
        pieChartTotalDuration = (PieChart) getView().findViewById(R.id.idPieChartTotalDuration);

        pieChartOutgoingFrequency = (PieChart) getView().findViewById(R.id.idPieChartOutgoingFrequency);
        pieChartIncomingFrequency = (PieChart) getView().findViewById(R.id.idPieChartIncomingFrequency);
        pieChartTotalFrequency = (PieChart) getView().findViewById(R.id.idPieChartTotalFrequency);

        incomingDurationButton = (Button) getView().findViewById(R.id.incomingDuration_btn);
        outgoingDurationButton = (Button) getView().findViewById(R.id.outgoingDuration_btn);
        totalDurationButton = (Button) getView().findViewById(R.id.totalDuration_btn);

        incomingFrequencyButton = (Button) getView().findViewById(R.id.incomingFrequency_btn);
        outgoingFrequencyButton = (Button) getView().findViewById(R.id.outgoingFrequency_btn);
        totalFrequencyButton = (Button) getView().findViewById(R.id.totalFrequency_btn);

        incomingDurationLayout = (LinearLayout) getView().findViewById(R.id.incomingDuration_layout);
        outgoingDurationLayout = (LinearLayout) getView().findViewById(R.id.outgoingDuration_layout);
        totalDurationLayout = (LinearLayout) getView().findViewById(R.id.totalDuration_layout);

        incomingFrequencyLayout = (LinearLayout) getView().findViewById(R.id.incomingFrequency_layout);
        outgoingFrequencyLayout = (LinearLayout) getView().findViewById(R.id.outgoingFrequency_layout);
        totalFrequencyLayout = (LinearLayout) getView().findViewById(R.id.totalFrequency_layout);
    }

    private void onClickAccordion(final Button btn, final List<Node> callList, final LinearLayout layout, final PieChart pieChart) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout.getVisibility() == View.VISIBLE) {
                    layout.setVisibility(View.GONE);
                } else {
                    layout.setVisibility(View.VISIBLE);
                    int id = btn.getId();
                    switch (id) {
                        case R.id.incomingDuration_btn:  transformDurationDataToPieEntry(callList, pieChart);
                            break;
                        case R.id.incomingFrequency_btn:  transformFrequencyDataToPieEntry(callList, pieChart);
                            break;
                        case R.id.outgoingDuration_btn:  transformDurationDataToPieEntry(callList, pieChart);
                            break;
                        case R.id.outgoingFrequency_btn:  transformFrequencyDataToPieEntry(callList, pieChart);
                            break;
                        case R.id.totalDuration_btn:  transformDurationDataToPieEntry(callList, pieChart);
                            break;
                        case R.id.totalFrequency_btn:  transformFrequencyDataToPieEntry(callList, pieChart);
                            break;
                    }
                }
            }

        });
    }

    public void transformDurationDataToPieEntry (List<Node> callList, PieChart pieChart) {

        ArrayList<PieEntry> pieEntryList = new ArrayList();
        int totalOthers = 0;
        if(!callList.isEmpty()) {
            if(callList.size() < 4 ) {
                for (int i = 0; i < callList.size()   ; i++){
                    int minutes = callList.get(i).getDuration() / 60;
                    PieEntry newPEntry = new PieEntry(callList.get(i).getDuration(),
                            callList.get(i).getName() + ": " + minutes + " min");
                    pieEntryList.add(newPEntry);
                }
            } else {
                for (int i = 0; i < 4 ; i++){
                    int minutes = callList.get(i).getDuration() / 60;
                    PieEntry newPEntry = new PieEntry(callList.get(i).getDuration(),
                            callList.get(i).getName() + ": " + minutes + " min");
                    pieEntryList.add(newPEntry);
                }
                for (int i = 4; i <callList.size() ; i++){
                    totalOthers += callList.get(i).getDuration();
                }
                PieEntry newPEntry = new PieEntry(totalOthers, "Others: " + totalOthers/60 + " min");
                pieEntryList.add(newPEntry);
            }

        }
        addDataSetToPie(callList, pieChart, pieEntryList);
    }

    public void transformFrequencyDataToPieEntry (List<Node> callList, PieChart pieChart){
        ArrayList<PieEntry> pieEntryList = new ArrayList();
        int totalOthers = 0;
        if(!callList.isEmpty()) {
            if(callList.size() < 4 ) {
                for (int i = 0; i < callList.size()   ; i++){
                    int times = callList.get(i).getOccurrence();
                    PieEntry newPEntry = new PieEntry(callList.get(i).getOccurrence(),
                            callList.get(i).getName() + ": " + times + " times");
                    pieEntryList.add(newPEntry);
                }
            } else {
                for (int i = 0; i < 4 ; i++){
                    int times = callList.get(i).getOccurrence();
                    PieEntry newPEntry = new PieEntry(callList.get(i).getOccurrence(),
                            callList.get(i).getName() + ": " + times + " times");
                    pieEntryList.add(newPEntry);
                }
                for (int i = 4; i <callList.size() ; i++){
                    totalOthers += callList.get(i).getOccurrence();
                }
                PieEntry newPEntry = new PieEntry(totalOthers, "Others: " + totalOthers + " times");
                pieEntryList.add(newPEntry);
            }
        }
        addDataSetToPie(callList, pieChart, pieEntryList);
    }

    private void addDataSetToPie(List<Node> callList, PieChart pieChart, ArrayList<PieEntry> pieEntryList) {

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
