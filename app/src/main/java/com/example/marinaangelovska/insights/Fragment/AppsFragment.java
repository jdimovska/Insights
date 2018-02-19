package com.example.marinaangelovska.insights.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.marinaangelovska.insights.Adapters.CustomAppsAdapter;
import com.example.marinaangelovska.insights.Model.Application;
import com.example.marinaangelovska.insights.R;
import com.example.marinaangelovska.insights.Service.AppsService;
import com.example.marinaangelovska.insights.Service.ContactsService;
import com.example.marinaangelovska.insights.Service.PeopleService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.marinaangelovska.insights.Activity.MainActivity.dialog;


/**
 * Created by marinaangelovska on 2/2/18.
 */

public class AppsFragment extends Fragment {
    UsageStatsManager mUsageStatsManager;
    CustomAppsAdapter adapter;
    List<UsageStats> usageStatsList;
    AppsService appService;
    ArrayList<Application> appList;
    View view;
    ListView viewList;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    List<UsageStats> getUsageStatistics() {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        long startTime = calendar.getTimeInMillis();
        List<UsageStats> queryUsageStats = new ArrayList<>();
        Map<String, UsageStats> queryUsageStats1 = mUsageStatsManager.queryAndAggregateUsageStats(startTime,
                        endTime);

        if (queryUsageStats1.size() == 0) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
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
        view = inflater.inflate(R.layout.fragment_app, container, false);
        viewList=(ListView)view.findViewById (R.id.appsList);
        mUsageStatsManager = (UsageStatsManager) getActivity()
                .getSystemService(Context.USAGE_STATS_SERVICE);
        usageStatsList = getUsageStatistics();
        appService = new AppsService(getActivity(), getActivity());
        appList = appService.getApps(usageStatsList);
        adapter=new CustomAppsAdapter(getActivity(), appList);
        viewList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void loadApplications() {


    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
