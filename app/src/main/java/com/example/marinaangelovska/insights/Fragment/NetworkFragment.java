package com.example.marinaangelovska.insights.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.marinaangelovska.insights.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class NetworkFragment extends Fragment {


    ArrayList<Long> wifiData = new ArrayList<>();
    ArrayList<Long> mobileData = new ArrayList<>();

    View view;

    PieChart dataUsageChart;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_network, container, false);
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) getActivity().getSystemService(Context.NETWORK_STATS_SERVICE);

        wifiData = getWifiData(networkStatsManager);
        mobileData = getMobileData(getContext(), networkStatsManager);

        dataUsageChart = (PieChart) view.findViewById(R.id.piechart);

        setDataForPieChart();

        // Inflate the layout for this fragment
        return view;
    }

    private void setDataForPieChart() {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        if(wifiData.get(0) > 0)
            pieEntries.add(new PieEntry(bytesToMeg(wifiData.get(0)), "WIFI Download: " + humanReadableByteCount(wifiData.get(0), true)));
        if(wifiData.get(1) > 0)
            pieEntries.add(new PieEntry(bytesToMeg(wifiData.get(1)), "WIFI Upload: " + humanReadableByteCount(wifiData.get(1), true)));
        if(mobileData.get(0) > 0)
            pieEntries.add(new PieEntry(bytesToMeg(mobileData.get(0)), "Mobile Data Download: " + humanReadableByteCount(mobileData.get(0), true)));
        if(mobileData.get(1) > 0)
            pieEntries.add(new PieEntry(bytesToMeg(mobileData.get(1)), "Mobile Data Upload: " + humanReadableByteCount(mobileData.get(1), true)));

        PieDataSet dataSet = new PieDataSet(pieEntries, null);
        dataSet.setSelectionShift(5f);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
        dataUsageChart.setHoleRadius(10f);
        dataUsageChart.setTransparentCircleAlpha(0);
        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        dataUsageChart.setData(data);
        dataUsageChart.setEntryLabelColor(Color.WHITE);
        dataUsageChart.setEntryLabelTextSize(12f);

        dataUsageChart.highlightValues(null);
        dataUsageChart.setDescription( null);
        dataUsageChart.setUsePercentValues(true);
        dataUsageChart.getLegend().setEnabled(false);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(81,68,60));
        colors.add(Color.rgb(95, 128, 181));
        colors.add(Color.rgb(236,189,174));
        colors.add(Color.rgb(193,131,141));
        colors.add(Color.rgb(182, 200,227));
        colors.add(Color.rgb(195, 145,178));

        dataSet.setColors(colors);
        dataUsageChart.invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private ArrayList<Long> getWifiData(NetworkStatsManager networkStatsManager) {
        NetworkStats.Bucket bucket;
        ArrayList<Long> data = new ArrayList<>();
        Calendar c = Calendar.getInstance();   // this takes current date
        c.set(Calendar.DAY_OF_MONTH, 1);
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI,
                    "",
                    c.getTimeInMillis(),
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return data;
        }

        data.add(bucket.getRxBytes());
        data.add(bucket.getTxBytes());
        return data;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ArrayList<Long> getMobileData(Context context, NetworkStatsManager networkStatsManager) {
        NetworkStats.Bucket bucket;
        ArrayList<Long> data = new ArrayList<>();
        Calendar c = Calendar.getInstance();   // this takes current date
        c.set(Calendar.DAY_OF_MONTH, 1);
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                    c.getTimeInMillis(),
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return data;
        }
        data.add(bucket.getRxBytes());
        data.add(bucket.getTxBytes());
        return data;
    }

    //Here Manifest.permission.READ_PHONE_STATS is needed
    @SuppressLint("MissingPermission")
    private String getSubscriberId(Context context, int networkType) {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                return tm.getSubscriberId();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
        return "";
    }

    private String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    private static final long  MEGABYTE = 1024L * 1024L;

    public static long bytesToMeg(long bytes) {
        return bytes / MEGABYTE ;
    }
}
