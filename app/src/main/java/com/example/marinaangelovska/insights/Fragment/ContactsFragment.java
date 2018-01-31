package com.example.marinaangelovska.insights.Fragment;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.example.marinaangelovska.insights.Model.Node;
import com.example.marinaangelovska.insights.R;
import com.example.marinaangelovska.insights.Service.ContactsService;
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

public class ContactsFragment extends Fragment {
    ContactsService contactsService;

    PieChart pieChartOutgoingDuration;
    PieChart pieChartIncomingDuration;
    PieChart pieChartOutgoingFrequency;
    PieChart pieChartIncomingFrequency;
    PieChart pieChartMissed;

    Button incomingDurationButton;
    Button outgoingDurationButton;
    Button incomingFrequencyButton;
    Button outgoingFrequencyButton;
    Button missedButton;

    LinearLayout incomingDurationLayout;
    LinearLayout outgoingDurationLayout;
    LinearLayout incomingFrequencyLayout;
    LinearLayout outgoingFrequencyLayout;
    LinearLayout missedLayout;

    List<Node> callListOutgoingDuration;
    List<Node> callListIncomingDuration;
    List<Node> callListIncomingFrequency;
    List<Node> callListOutgoingFrequency;
    List<Node> callListMissed;

    HashMap<Integer, List<Node>> map;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        contactsService = new ContactsService(getActivity());
        map = contactsService.getCallLogDetails();

        int callTypeIncoming = CallLog.Calls.INCOMING_TYPE;
        int callTypeOutgoing = CallLog.Calls.OUTGOING_TYPE;
        int callTypeMissed = CallLog.Calls.MISSED_TYPE;

        callListIncomingDuration = contactsService.getLongestCalls(map.get(callTypeIncoming));
        callListOutgoingDuration = contactsService.getLongestCalls(map.get(callTypeOutgoing));

        callListIncomingFrequency = contactsService.getMostFrequentCalls(map.get(callTypeIncoming));
        callListOutgoingFrequency = contactsService.getMostFrequentCalls(map.get(callTypeOutgoing));

        callListMissed = contactsService.getMostFrequentCalls(map.get(callTypeMissed));


        initializingViews();
        onClickAccordion(incomingDurationButton, callListIncomingDuration, incomingDurationLayout, pieChartIncomingDuration);
        onClickAccordion(outgoingDurationButton, callListOutgoingDuration, outgoingDurationLayout, pieChartOutgoingDuration);
        onClickAccordion(outgoingFrequencyButton, callListOutgoingFrequency, outgoingFrequencyLayout, pieChartOutgoingFrequency);
        onClickAccordion(incomingFrequencyButton, callListIncomingFrequency, incomingFrequencyLayout, pieChartIncomingFrequency);
        onClickAccordion(missedButton, callListMissed, missedLayout, pieChartMissed);

    }
    private void initializingViews() {
        pieChartOutgoingDuration = (PieChart) getView().findViewById(R.id.idPieChartOutgoingDuration);
        pieChartIncomingDuration = (PieChart) getView().findViewById(R.id.idPieChartIncomingDuration);

        pieChartOutgoingFrequency = (PieChart) getView().findViewById(R.id.idPieChartOutgoingFrequency);
        pieChartIncomingFrequency = (PieChart) getView().findViewById(R.id.idPieChartIncomingFrequency);

        pieChartMissed = (PieChart) getView().findViewById(R.id.idPieChartMissed);

        incomingDurationButton = (Button) getView().findViewById(R.id.incomingDuration_btn);
        outgoingDurationButton = (Button) getView().findViewById(R.id.outgoingDuration_btn);

        incomingFrequencyButton = (Button) getView().findViewById(R.id.incomingFrequency_btn);
        outgoingFrequencyButton = (Button) getView().findViewById(R.id.outgoingFrequency_btn);

        missedButton = (Button) getView().findViewById(R.id.missed_btn);

        incomingDurationLayout = (LinearLayout) getView().findViewById(R.id.incomingDuration_layout);
        outgoingDurationLayout = (LinearLayout) getView().findViewById(R.id.outgoingDuration_layout);

        incomingFrequencyLayout = (LinearLayout) getView().findViewById(R.id.incomingFrequency_layout);
        outgoingFrequencyLayout = (LinearLayout) getView().findViewById(R.id.outgoingFrequency_layout);

        missedLayout = (LinearLayout) getView().findViewById(R.id.missed_layout);
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
                        case R.id.missed_btn:  transformFrequencyDataToPieEntry(callList, pieChart);
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
