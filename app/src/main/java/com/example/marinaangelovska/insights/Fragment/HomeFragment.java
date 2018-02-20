package com.example.marinaangelovska.insights.Fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.usage.NetworkStatsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;

import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import com.example.marinaangelovska.insights.Adapters.CustomPeopleAdapter;
import com.example.marinaangelovska.insights.Model.Application;
import com.example.marinaangelovska.insights.Model.NetworkUsage;
import com.example.marinaangelovska.insights.Model.Person;
import com.example.marinaangelovska.insights.R;
import com.example.marinaangelovska.insights.Service.AppsService;
import com.example.marinaangelovska.insights.Service.ContactsService;
import com.example.marinaangelovska.insights.Service.PeopleService;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.marinaangelovska.insights.Activity.MainActivity.dialog;

/**
 * Created by marinaangelovska on 2/2/18.
 */

public class HomeFragment extends Fragment {

    public static final String ACTION_USAGE_ACCESS_SETTINGS = Settings.ACTION_USAGE_ACCESS_SETTINGS;
    UsageStatsManager mUsageStatsManager;
    List<UsageStats> usageStatsList;

    AppsService appService;
    ContactsService contactsService;
    PeopleService peopleService;

    CustomPeopleAdapter favoritePeopleAdapter;
    public static ArrayList<Person> allPeopleList;
    ArrayList<Person> favoritePeopleList;
    ArrayList<Application> appList;
    ArrayList<NetworkUsage> appUsageList;

    View view;

    Long todaysCallsDuration = 0L;
    PieChart pieChart;

    public static Button unlockedTimesButton;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        unlockedTimesButton = (Button) getView().findViewById(R.id.unlockedTimes_button);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    List<UsageStats> getUsageStatistics() {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, -1);

        long startTime = calendar.getTimeInMillis();

        List<UsageStats> queryUsageStats = new ArrayList<>();

        Map<String, UsageStats> queryUsageStats1 = mUsageStatsManager
                .queryAndAggregateUsageStats(startTime,
                        endTime);

        if (queryUsageStats1.size() == 0) {
            startActivity(new Intent(ACTION_USAGE_ACCESS_SETTINGS));
        }

        Iterator it = queryUsageStats1.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            queryUsageStats.add((UsageStats) pair.getValue());
        }

        for(int i = 0; i < queryUsageStats.size(); i++){
            UsageStats tmp = queryUsageStats.get(i);
            if(tmp.getTotalTimeInForeground() <= 0){
                queryUsageStats.remove(i);
            }
        }

        return queryUsageStats;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //Get app usage
        mUsageStatsManager = (UsageStatsManager) getActivity()
                .getSystemService(Context.USAGE_STATS_SERVICE);
        usageStatsList = getUsageStatistics();

        //Initialize services
        appService = new AppsService(getActivity(), getActivity());
        contactsService = new ContactsService(getActivity());
        peopleService = new PeopleService(getActivity());

        //Get data
        appList = appService.getApps(usageStatsList);
        if(appList.size() < 3) {
            appList = new ArrayList<>(appList.subList(0, appList.size()));
        } else {
            appList = new ArrayList<>(appList.subList(0, 3));
        }
        todaysCallsDuration = contactsService.getTodaysCallsDuration();
        favoritePeopleList = peopleService.getPeople();
        allPeopleList = new ArrayList<Person>(favoritePeopleList);
        favoritePeopleList = new ArrayList<>(favoritePeopleList.subList(0, 5));
        appUsageList = appService.getAppsWithNetworkUsage((NetworkStatsManager) getActivity().getSystemService(Context.NETWORK_STATS_SERVICE));

        pieChart = (PieChart) view.findViewById(R.id.piechart);

        setDataForPieChart();

        favoritePeopleAdapter = new CustomPeopleAdapter(getActivity(), favoritePeopleList);
        ListView viewList=(ListView)view.findViewById (R.id.favoritePeople_list);
        viewList.setAdapter(favoritePeopleAdapter);
        viewList.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        setListViewHeightBasedOnChildren(viewList);
        favoritePeopleAdapter.notifyDataSetChanged();

        ScrollView mainScrollView = (ScrollView)view.findViewById(R.id.scrollView_home);
        mainScrollView.smoothScrollTo(0,0);
        return view;
    }


    private void setDataForPieChart() {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);

        float totalHours = cal.get(Calendar.HOUR_OF_DAY);
        float totalHoursSleep = 8;
        float totalHoursSpentOnCalls = todaysCallsDuration / 3600f;

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        float totalAppsTime = 0;
        for(Application app: appList){
            pieEntries.add(new PieEntry(app.getTime() / 3600f, app.getName()+ ": " + df.format(app.getTime() / 3600f) + "h"));
            totalAppsTime += (app.getTime() / 36000f);
        }
        float restOfday = 0;
        if(totalHours > totalHoursSleep) {
            restOfday = (totalHours - totalHoursSleep);
            if (restOfday > (totalHoursSpentOnCalls + totalAppsTime)) {
                restOfday = restOfday - (totalHoursSpentOnCalls + totalAppsTime);
            }
        }

        pieEntries.add(new PieEntry(totalHoursSleep, "Sleep: " + df.format(totalHoursSleep) + "h"));

        if(totalHoursSpentOnCalls > 0)
            pieEntries.add(new PieEntry(totalHoursSpentOnCalls, "Calls: " + df.format(totalHoursSpentOnCalls) + "h"));

        if(restOfday > 0)
            pieEntries.add(new PieEntry( restOfday, "Daily Life: " + df.format(restOfday) + "h"));

        bindDatasetToChart(pieEntries, pieChart);

    }

    private void bindDatasetToChart(ArrayList<PieEntry> pieEntries, PieChart pieChart){

        PieDataSet dataSet = new PieDataSet(pieEntries, null);
        dataSet.setSelectionShift(5f);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
        pieChart.setHoleRadius(10f);
        pieChart.setTransparentCircleAlpha(0);
        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        pieChart.setData(data);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);

        pieChart.highlightValues(null);
        pieChart.setDescription( null);
        pieChart.setUsePercentValues(true);
        pieChart.getLegend().setEnabled(false);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(81,68,60));
        colors.add(Color.rgb(95, 128, 181));
        colors.add(Color.rgb(170, 124, 124));
        colors.add(Color.rgb(236,189,174));
        colors.add(Color.rgb(193,131,141));
        colors.add(Color.rgb(182, 200,227));

        dataSet.setColors(colors);
        pieChart.invalidate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    private static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
    @Override
    public void onStart() {
        super.onStart();
        dialog.hide();
    }

}