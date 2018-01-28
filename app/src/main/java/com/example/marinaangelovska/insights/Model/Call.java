package com.example.marinaangelovska.insights.Model;

import android.provider.CallLog;

import java.util.Date;

/**
 * Created by Jona Dimovska on 28.1.2018.
 */

public class Call {

    String name;
    Date date ;
    String duration ;

    public Call(String name, Date date, String duration) {
        this.name = name;
        this.date = date;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
