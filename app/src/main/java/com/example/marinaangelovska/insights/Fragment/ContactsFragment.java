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
import com.example.marinaangelovska.insights.Model.NodeContact;
import com.example.marinaangelovska.insights.R;
import com.example.marinaangelovska.insights.Service.ContactsService;
import com.example.marinaangelovska.insights.Service.PeopleService;
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
    PeopleService peopleService;

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

    List<NodeContact> callListOutgoingDuration;
    List<NodeContact> callListIncomingDuration;
    List<NodeContact> callListIncomingFrequency;
    List<NodeContact> callListOutgoingFrequency;
    List<NodeContact> callListMissed;

    HashMap<Integer, List<NodeContact>> map;

    public List<NodeContact> creatNewInstance(List<NodeContact> temp) {
        return new ArrayList<NodeContact>(temp);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contactsService = new ContactsService(getActivity());
        peopleService = new PeopleService(getActivity());
        peopleService.getPeople();

        map = contactsService.getCallLogDetails();

        int callTypeIncoming = CallLog.Calls.INCOMING_TYPE;
        int callTypeOutgoing = CallLog.Calls.OUTGOING_TYPE;
        int callTypeMissed = CallLog.Calls.MISSED_TYPE;

        callListIncomingDuration = contactsService.getLongestCalls(creatNewInstance(map.get(callTypeIncoming)));
        callListOutgoingDuration = contactsService.getLongestCalls(creatNewInstance(map.get(callTypeOutgoing)));


        callListIncomingFrequency = contactsService.getMostFrequentCalls(creatNewInstance(map.get(callTypeIncoming)));
        callListOutgoingFrequency = contactsService.getMostFrequentCalls(creatNewInstance(map.get(callTypeOutgoing)));

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

    private void onClickAccordion(final Button btn, final List<NodeContact> callList, final LinearLayout layout, final PieChart pieChart) {
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

    public void transformDurationDataToPieEntry (List<NodeContact> callList, PieChart pieChart) {
        double totalAll = 0;
        ArrayList<PieEntry> pieEntryList = new ArrayList();
        int totalOthers = 0;
        for(int i=0;i<callList.size();i++) {
            totalAll += callList.get(i).getDuration();
        }
        if(!callList.isEmpty()) {
            if(callList.size() < 4 ) {
                for (int i = 0; i < callList.size()   ; i++){
                    int minutes = callList.get(i).getDuration() / 60;
                    PieEntry newPEntry = new PieEntry(callList.get(i).getDuration(),
                            callList.get(i).getName() + ": " + minutes + " min");
                    pieEntryList.add(newPEntry);
                }
            } else {
                int counter = 0;
                double total = 0;
                while (total / totalAll < 0.75) {
                    int minutes = callList.get(counter).getDuration() / 60;
                    PieEntry newPEntry = new PieEntry(callList.get(counter).getDuration(),
                            callList.get(counter).getName() + ": " + minutes + " min");
                    pieEntryList.add(newPEntry);
                    total += callList.get(counter).getDuration();
                    counter++;
                }
                for (int i = counter; i <callList.size() ; i++){
                    totalOthers += callList.get(i).getDuration();
                }
                PieEntry newPEntry = new PieEntry(totalOthers, "Others: " + totalOthers/60 + " min");
                pieEntryList.add(newPEntry);
            }

        }
        addDataSetToPie(callList, pieChart, pieEntryList);
    }

    public void transformFrequencyDataToPieEntry (List<NodeContact> callList, PieChart pieChart){
        ArrayList<PieEntry> pieEntryList = new ArrayList();
        int totalOthers = 0;
        double totalAll = 0;
        for(int i=0;i<callList.size();i++) {
            totalAll += callList.get(i).getOccurrence();
        }
        if(!callList.isEmpty()) {
            if(callList.size() < 4 ) {
                for (int i = 0; i < callList.size()   ; i++){
                    int times = callList.get(i).getOccurrence();
                    String s =  "s";
                    if (times == 1)
                        s = "";
                    PieEntry newPEntry = new PieEntry(callList.get(i).getOccurrence(),
                            callList.get(i).getName() + ": " + times + " time" + s);
                    pieEntryList.add(newPEntry);
                }
            } else {
                int counter = 0;
                double total = 0;
                while (total / totalAll < 0.75) {
                    int times = callList.get(counter).getOccurrence();
                    String s =  "s";
                    if (times == 1)
                        s = "";
                    PieEntry newPEntry = new PieEntry(callList.get(counter).getOccurrence(),
                            callList.get(counter).getName() + ": " + times + " time" + s);
                    pieEntryList.add(newPEntry);
                    total += callList.get(counter).getOccurrence();
                    counter++;
                }
                String s =  "s";
                for (int i = counter; i <callList.size() ; i++){
                    totalOthers += callList.get(i).getOccurrence();
                    if (totalOthers == 1)
                        s = "";
                }
                PieEntry newPEntry = new PieEntry(totalOthers, "Others: " + totalOthers + " time" + s);
                pieEntryList.add(newPEntry);
            }
        }
        addDataSetToPie(callList, pieChart, pieEntryList);
    }

    private void addDataSetToPie(List<NodeContact> callList, PieChart pieChart, ArrayList<PieEntry> pieEntryList) {

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
