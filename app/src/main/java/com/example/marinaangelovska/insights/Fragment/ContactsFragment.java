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
    Button incomingDurationButton;
    Button outgoingDurationButton;
    Button totalDurationButton;
    LinearLayout incomingDurationLayout;
    LinearLayout outgoingDurationLayout;
    LinearLayout totalDurationLayout;

    List<Node> callListOutgoing;
    List<Node> callListIncoming;
    List<Node> callListTotal;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contactsService = new ContactsService(getActivity());
        int callTypeIncoming = CallLog.Calls.INCOMING_TYPE;
        int callTypeOutgoing = CallLog.Calls.OUTGOING_TYPE;

        callListIncoming = contactsService.getLongestOutgoingCalls(contactsService.getCallLogDetailsOutgoing(callTypeIncoming));
        callListOutgoing = contactsService.getLongestOutgoingCalls(contactsService.getCallLogDetailsOutgoing(callTypeOutgoing));

        //callListTotal =  callListIncoming;
        //callListTotal.addAll(callListOutgoing);

        initializingViews();

        onClickAccordion(incomingDurationButton, callListIncoming, incomingDurationLayout, pieChartIncomingDuration);
        onClickAccordion(outgoingDurationButton, callListOutgoing, outgoingDurationLayout, pieChartOutgoingDuration);
        //onClickAccordion(totalDurationButton, callListTotal, totalDurationLayout, pieChartTotalDuration);


    }
    private void initializingViews() {
        pieChartOutgoingDuration = (PieChart) getView().findViewById(R.id.idPieChartOutgoingDuration);
        pieChartIncomingDuration = (PieChart) getView().findViewById(R.id.idPieChartIncomingDuration);
        pieChartTotalDuration = (PieChart) getView().findViewById(R.id.idPieChartTotalDuration);

        incomingDurationButton = (Button) getView().findViewById(R.id.incomingDuration_btn);
        outgoingDurationButton = (Button) getView().findViewById(R.id.outgoingDuration_btn);
        totalDurationButton = (Button) getView().findViewById(R.id.totalDuration_btn);

        incomingDurationLayout = (LinearLayout) getView().findViewById(R.id.incomingDuration_layout);
        outgoingDurationLayout = (LinearLayout) getView().findViewById(R.id.outgoingDuration_layout);
        totalDurationLayout = (LinearLayout) getView().findViewById(R.id.totalDuration_layout);
    }

    private void onClickAccordion(Button btn, final List<Node> callList, final LinearLayout layout, final PieChart pieChart) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout.getVisibility() == View.VISIBLE) {
                    layout.setVisibility(View.GONE);

                } else {
                    layout.setVisibility(View.VISIBLE);
                    addDataSetToPie(callList, pieChart);
                }
            }

        });
    }

    private void addDataSetToPie(List<Node> callList, PieChart pieChart) {
        pieChart.setHoleRadius(10f);
        pieChart.setTransparentCircleAlpha(0);

        ArrayList<PieEntry> pieEntryList = new ArrayList();
        int totalOthers = 0;
        if(!callList.isEmpty()) {
            for (int i = 0; i < 4; i++){
                int minutes = callList.get(i).getDuration() / 60;
                PieEntry newPEntry = new PieEntry(callList.get(i).getDuration(),
                                            callList.get(i).getName() + ": " + minutes + " min");
                pieEntryList.add(newPEntry);
            }
            for (int i = 4; i <callList.size() ; i++){
                totalOthers += callList.get(i).getDuration();
            }
            PieEntry newPEntry = new PieEntry(totalOthers, "Others: " + totalOthers + " min");
            pieEntryList.add(newPEntry);
        }

        PieDataSet dataSet = new PieDataSet(pieEntryList, null);
        dataSet.setSelectionShift(5f);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        pieChart.setData(data);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(7f);

        pieChart.highlightValues(null);
        pieChart.setDescription( null);
        pieChart.getLegend().setEnabled(false);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.MAGENTA);
        colors.add(Color.CYAN);
        colors.add(Color.LTGRAY);
        colors.add(Color.YELLOW);
        colors.add(Color.GREEN);
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
