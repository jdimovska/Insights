package com.example.marinaangelovska.insights.Service;

import android.app.Activity;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;

import com.example.marinaangelovska.insights.Model.Application;
import com.example.marinaangelovska.insights.Model.Person;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by marinaangelovska on 2/2/18.
 */

public class AppsService {

    private Context context;
    public AppsService(Context context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArrayList<Application> getApps(List<UsageStats> usageStatsList) throws PackageManager.NameNotFoundException {
        ArrayList<Application> appList = new ArrayList<>();

        for(int i=0;i<usageStatsList.size();i++) {
            UsageStats tmp = usageStatsList.get(i);

            appList.add(new Application(tmp.getPackageName(),
                    context.getPackageManager().getApplicationIcon(tmp.getPackageName()),
                    tmp.getLastTimeUsed()));

            Collections.sort(appList, new TimeComparator());
        }
        return appList;
    }
    class TimeComparator implements Comparator<Application> {
        @Override
        public int compare(Application app1, Application app2) {
            if (app1.getTime() > app2.getTime())
                return 1;
            else
                return -1;
        }
    }
}
