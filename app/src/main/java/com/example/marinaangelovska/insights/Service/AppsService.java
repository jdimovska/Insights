package com.example.marinaangelovska.insights.Service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.example.marinaangelovska.insights.Model.Application;
import com.example.marinaangelovska.insights.Model.NetworkUsage;
import com.example.marinaangelovska.insights.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by marinaangelovska on 2/2/18.
 */

public class AppsService {

    private Context context;
    private Activity activity;
    public AppsService(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
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
                                (tmp.getTotalTimeInForeground() / 1000L)));
                    else
                        appList.add(new Application(appName,
                                context.getDrawable(R.drawable.ic_icons8_google_mobile),
                                (tmp.getTotalTimeInForeground() / 1000L)));
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ArrayList<NetworkUsage> getAppsWithNetworkUsage(NetworkStatsManager networkStatsManager){
        PackageManager pm = context.getPackageManager();

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<NetworkUsage> appNetworkUsages = new ArrayList<>();

        for (Iterator<ApplicationInfo> iter = packages.listIterator(); iter.hasNext(); ) {
            ApplicationInfo pkgInfo = iter.next();
            if (isSystemPackage(pkgInfo)) {
                iter.remove();
            }
        }

        for(ApplicationInfo pkg: packages){
            if(getAppNameFromPackage(pkg.packageName, context) == null){
                NetworkUsage tmpNetworkUsage = new NetworkUsage(pkg.packageName, appNetworkUsage(networkStatsManager, pkg.uid));
                appNetworkUsages.add(tmpNetworkUsage);
                continue;
            }
            NetworkUsage tmpNetworkUsage = new NetworkUsage(getAppNameFromPackage(pkg.packageName, context), appNetworkUsage(networkStatsManager, pkg.uid));
            appNetworkUsages.add(tmpNetworkUsage);
        }

        Collections.sort(appNetworkUsages, new NetworkUsageComparator());
        Collections.reverse(appNetworkUsages);
        if(appNetworkUsages.size() > 5){
            return new ArrayList<>(appNetworkUsages.subList(0, 5));
        }
        return appNetworkUsages;
    }

    private boolean isSystemPackage(ApplicationInfo pkgInfo) {
        return ((pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Long appNetworkUsage(NetworkStatsManager networkStatsManager, int packageUid) {
        NetworkStats networkStats = null;
        Calendar c = Calendar.getInstance();   // this takes current date
        c.set(Calendar.DAY_OF_MONTH, 1);
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,
                    /*getSubscriberId(context, ConnectivityManager.TYPE_MOBILE)*/ "",
                    c.getTimeInMillis(),
                    System.currentTimeMillis(),
                    packageUid);
        } catch (RemoteException e) {

        }
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        networkStats.getNextBucket(bucket);
        return bucket.getRxBytes();
    }

    //Here Manifest.permission.READ_PHONE_STATS is needed
    @SuppressLint("MissingPermission")
    private String getSubscriberId(Context context, int networkType) {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                return tm.getSubscriberId();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
        return "";
    }

    class TimeComparator implements Comparator<Application> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public int compare(Application app1, Application app2) {
            return app1.getTime().compareTo(app2.getTime());

        }
    }

    class NetworkUsageComparator implements Comparator<NetworkUsage> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public int compare(NetworkUsage n1, NetworkUsage n2) {
            return n1.getDataUsage().compareTo(n2.getDataUsage());

        }
    }
}
