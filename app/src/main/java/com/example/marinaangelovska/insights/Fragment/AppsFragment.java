package com.example.marinaangelovska.insights.Fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;



/**
 * Created by marinaangelovska on 2/2/18.
 */

public class AppsFragment extends Fragment {
    UsageStatsManager mUsageStatsManager;
    CustomAppsAdapter adapter;
    List<UsageStats> usageStatsList;
    Context context;
    AppsService appService;
    ArrayList<Application> appList;
    View view;
    ListView viewList;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    List<UsageStats> getUsageStatistics() {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        long startTime = calendar.getTimeInMillis();
        //if Sunday
        if(startTime > endTime) {
            startTime -= 604800000;
        }
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_app, container, false);

        ApplicationTask task = new ApplicationTask();
        task.execute();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onStart() {
        super.onStart();

    }

     class ApplicationTask extends AsyncTask<String,Void,String> {
        ProgressDialog nDialog;
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            nDialog =  new ProgressDialog(context);
            nDialog.setMessage("Loading applications...");
            nDialog.setCancelable(false);
            nDialog.setInverseBackgroundForced(false);
            nDialog.show();
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... urls) {
            viewList=(ListView)view.findViewById (R.id.appsList);

            mUsageStatsManager = (UsageStatsManager) getActivity()
                    .getSystemService(Context.USAGE_STATS_SERVICE);
            usageStatsList = getUsageStatistics();
            appService = new AppsService(getActivity(), getActivity());
            appList = appService.getApps(usageStatsList);
            return "Done";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            nDialog.hide();
            adapter = new CustomAppsAdapter(getActivity(), appList);
            viewList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

}
