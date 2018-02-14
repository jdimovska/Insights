package com.example.marinaangelovska.insights.Model;

import android.graphics.drawable.Drawable;

/**
 * Created by aleksandarsmilevski on 2/12/18.
 */

public class NetworkUsage {
    String appName;
    Long dataUsage;

    public NetworkUsage(String appName, Long dataUsage) {
        this.appName = appName;
        this.dataUsage = dataUsage;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Long getDataUsage() {
        return dataUsage;
    }

    public void setDataUsage(Long dataUsage) {
        this.dataUsage = dataUsage;
    }
}
