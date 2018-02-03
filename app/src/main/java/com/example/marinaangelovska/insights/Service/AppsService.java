package com.example.marinaangelovska.insights.Service;

import android.app.Activity;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.marinaangelovska.insights.Model.Application;
import com.example.marinaangelovska.insights.Model.Person;
import com.example.marinaangelovska.insights.R;
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
    public ArrayList<String> popularApps = new ArrayList<String>();
    ArrayList<Application> appList;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ArrayList<Application> getApps(List<UsageStats> usageStatsList) {
        appList = new ArrayList<>();
        for(int i=0;i<usageStatsList.size();i++) {
            UsageStats tmp = usageStatsList.get(i);
            String appName = getAppNameFromPackage(tmp.getPackageName(), context);
            if (appName != null) {
                if (checkList(appName))
                    continue;
                try {
                    if (context.getPackageManager().getApplicationIcon(tmp.getPackageName()) != null)
                        appList.add(new Application(appName,
                                context.getPackageManager().getApplicationIcon(tmp.getPackageName()),
                                (tmp.getTotalTimeInForeground() / 1000) % 60));
                    else
                        appList.add(new Application(appName,
                                context.getDrawable(R.drawable.ic_icons8_google_mobile),
                                (tmp.getTotalTimeInForeground() / 1000) % 60));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        Collections.sort(appList, new TimeComparator());
        Collections.reverse(appList);

        return appList;
    }
    private boolean checkList(String name) {
        for(int i=0;i<appList.size();i++) {
            if(appList.get(i).getName().equals(name)) {
                Long t = appList.get(i).getTime();
                appList.get(i).updateTime(t);
                return true;
            }

        }
        return false;
    }
    private String getAppNameFromPackage(String packageName, Context context) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> pkgAppsList = context.getPackageManager()
                .queryIntentActivities(mainIntent, 0);

        for (ResolveInfo app : pkgAppsList) {
            if (app.activityInfo.packageName.equals(packageName)) {
                return app.activityInfo.loadLabel(context.getPackageManager()).toString();
            }
        }
        return null;
    }
    class TimeComparator implements Comparator<Application> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public int compare(Application app1, Application app2) {
            return app1.getTime().compareTo(app2.getTime());

        }
    }
}
