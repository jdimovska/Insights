package com.example.marinaangelovska.insights.Fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import com.example.marinaangelovska.insights.Adapters.CustomPeopleAdapter;
import com.example.marinaangelovska.insights.Model.Application;
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
import java.util.List;

/**
 * Created by marinaangelovska on 2/2/18.
 */

public class HomeFragment extends Fragment {

    UsageStatsManager mUsageStatsManager;
    List<UsageStats> usageStatsList;

    AppsService appService;
    ContactsService contactsService;
    PeopleService peopleService;

    CustomPeopleAdapter favoritePeopleAdapter;
    ArrayList<Person> favoritePeopleList;
    ArrayList<Application> appList;

    View view;

    Long todaysCallsDuration = 0L;
    PieChart pieChart;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public List<UsageStats> getUsageStatistics(int intervalType) {
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE , -1);

        long startTime = calendar.getTimeInMillis();

        List<UsageStats> queryUsageStats = mUsageStatsManager
                .queryUsageStats(intervalType, startTime,
                        endTime);

        if (queryUsageStats.size() == 0) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

        for(int i = 0; i < queryUsageStats.size(); i++){
            UsageStats tmp = queryUsageStats.get(i);
            if(tmp.getTotalTimeInForeground() <= 0){
                queryUsageStats.remove(i);
            }
        }
        Collections.sort(queryUsageStats, new UsageTimeComparator());
        Collections.reverse(queryUsageStats);

        return queryUsageStats;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //Get app usage
        mUsageStatsManager = (UsageStatsManager) getActivity()
                .getSystemService(Context.USAGE_STATS_SERVICE);
        usageStatsList = getUsageStatistics(UsageStatsManager.INTERVAL_DAILY);

        //Initialize services
        appService = new AppsService(getActivity());
        contactsService = new ContactsService(getActivity());
        peopleService = new PeopleService(getActivity());

        //Get data
        appList = appService.getApps(usageStatsList);
        appList = new ArrayList<>(appList.subList(0, 3));
        todaysCallsDuration = contactsService.getTodaysCallsDuration();
        favoritePeopleList = peopleService.getPeople();
        favoritePeopleList = new ArrayList<>(favoritePeopleList.subList(0, 5));

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

        for(Application app: appList){
            pieEntries.add(new PieEntry(app.getTime() / 3600f, app.getName()+ ": " + df.format(app.getTime() / 3600f) + "h"));
        }

        float restOfday = (totalHours - totalHoursSleep);

        pieEntries.add(new PieEntry(totalHoursSpentOnCalls, "Calls: " + df.format(totalHoursSpentOnCalls) + "h"));
        pieEntries.add(new PieEntry(totalHoursSleep, "Sleep: " + df.format(totalHoursSleep) + "h"));
        pieEntries.add(new PieEntry( restOfday, "Daily Life: " + df.format(restOfday) + "h"));

        PieDataSet dataSet = new PieDataSet(pieEntries, null);
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
        pieChart.setUsePercentValues(true);
        pieChart.getLegend().setEnabled(false);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(81,68,60));
        colors.add(Color.rgb(95, 128, 181));
        colors.add(Color.rgb(236,189,174));
        colors.add(Color.rgb(193,131,141));
        colors.add(Color.rgb(182, 200,227));
        colors.add(Color.rgb(195, 145,178));

        dataSet.setColors(colors);
        pieChart.invalidate();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    class UsageTimeComparator implements Comparator<UsageStats>{

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public int compare(UsageStats usageStats, UsageStats t1) {
            return Long.compare(usageStats.getTotalTimeInForeground(), t1.getTotalTimeInForeground());
        }
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
}