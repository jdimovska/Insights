package com.example.marinaangelovska.insights.Comparators;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.marinaangelovska.insights.Model.NetworkUsage;

import java.util.Comparator;

/**
 * Created by aleksandarsmilevski on 2/16/18.
 */

public class NetworkUsageComparator implements Comparator<NetworkUsage> {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int compare(NetworkUsage n1, NetworkUsage n2) {
        return n1.getDataUsage().compareTo(n2.getDataUsage());

    }
}