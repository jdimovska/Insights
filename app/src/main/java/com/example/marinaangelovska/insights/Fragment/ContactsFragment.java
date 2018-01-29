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
    PieChart pieChartOutgoing;
    PieChart pieChartIncoming;
    Button incomingButton;
    Button outgoingButton;
    LinearLayout incomingLayout;
    LinearLayout outgoingLayout;

    List<Node> callListOutgoing;
    List<Node> callListIncoming;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contactsService = new ContactsService(getActivity());
        int callTypeIncoming = CallLog.Calls.INCOMING_TYPE;
        int callTypeOutgoing = CallLog.Calls.OUTGOING_TYPE;
        callListIncoming = contactsService.getLongestOutgoingCalls(contactsService.getCallLogDetailsOutgoing(callTypeIncoming));
        callListOutgoing = contactsService.getLongestOutgoingCalls(contactsService.getCallLogDetailsOutgoing(callTypeOutgoing));

        pieChartOutgoing = (PieChart) getView().findViewById(R.id.idPieChartOutgoing);
        pieChartIncoming = (PieChart) getView().findViewById(R.id.idPieChartIncoming);

        incomingButton = (Button) getView().findViewById(R.id.incoming_btn);
        outgoingButton = (Button) getView().findViewById(R.id.outgoing_btn);
        incomingLayout = (LinearLayout) getView().findViewById(R.id.incoming_layout);
        outgoingLayout = (LinearLayout) getView().findViewById(R.id.outgoing_layout);

        onClickAccordion(incomingButton, callListIncoming, incomingLayout, pieChartIncoming);
        onClickAccordion(outgoingButton, callListOutgoing, outgoingLayout, pieChartOutgoing);


    }
    private void onClickAccordion(Button btn, final List<Node> callList, final LinearLayout layout, final PieChart pieChart) {
        btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_menu_manage, 0);
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
                PieEntry newPEntry = new PieEntry(callList.get(i).getDuration(), callList.get(i).getName() + " " + callList.get(i).getDuration());
                pieEntryList.add(newPEntry);
            }
            for (int i = 4; i <callList.size() ; i++){
                totalOthers += callList.get(i).getDuration();
            }
            PieEntry newPEntry = new PieEntry(totalOthers, "Others");
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
