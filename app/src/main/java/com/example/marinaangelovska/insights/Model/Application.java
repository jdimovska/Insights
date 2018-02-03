package com.example.marinaangelovska.insights.Model;

import android.graphics.drawable.Drawable;

/**
 * Created by marinaangelovska on 2/2/18.
 */

public class Application {
    String name;
    Drawable icon;
    Long time;

    public Application(String name, Drawable icon, Long time){
        this.name = name;
        this.icon = icon;
        this.time = time;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public Long getTime() {
        return time;
    }
    public void setIcon(Long time) {
        this.time = time;
    }
    public void updateTime(Long time) {this.time += time;}

}
