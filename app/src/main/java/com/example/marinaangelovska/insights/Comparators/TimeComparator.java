package com.example.marinaangelovska.insights.Comparators;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.marinaangelovska.insights.Model.Application;

import java.util.Comparator;

/**
 * Created by aleksandarsmilevski on 2/16/18.
 */

public class TimeComparator implements Comparator<Application> {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int compare(Application app1, Application app2) {
        return app1.getTime().compareTo(app2.getTime());

    }
}
