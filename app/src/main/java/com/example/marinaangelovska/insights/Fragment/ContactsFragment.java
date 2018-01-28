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
    PieChart pieChart;
    List<Node> callList;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contactsService = new ContactsService(getActivity());
        int callType = CallLog.Calls.INCOMING_TYPE;
        callList = contactsService.getLongestOutgoingCalls(contactsService.getCallLogDetailsOutgoing(callType));

        for(int i=0;i<callList.size();i++) {
            Log.i("number" , callList.get(i).getName() +" "+ callList.get(i).getNumber() + " " +callList.get(i).getOccurrence() +" "+callList.get(i).getDuration());
        }
        pieChart = (PieChart) getView().findViewById(R.id.idPieChart);
        addDataSetToPie();

    }

    private void addDataSetToPie() {
        pieChart.setHoleRadius(10f);
        pieChart.setTransparentCircleAlpha(0);

        ArrayList<PieEntry> pieEntryList = new ArrayList();
        int totalOthers = 0;
        for (int i = 0; i < 4; i++){
            PieEntry newPEntry = new PieEntry(callList.get(i).getDuration(), callList.get(i).getName() + " " + callList.get(i).getDuration());
            pieEntryList.add(newPEntry);
        }
        for (int i = 4; i <callList.size() ; i++){
            totalOthers += callList.get(i).getDuration();
        }
        PieEntry newPEntry = new PieEntry(totalOthers, "Others");
        pieEntryList.add(newPEntry);

        PieDataSet dataSet = new PieDataSet(pieEntryList, "");
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
