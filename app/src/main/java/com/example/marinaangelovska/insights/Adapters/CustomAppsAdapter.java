package com.example.marinaangelovska.insights.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marinaangelovska.insights.Model.Application;
import com.example.marinaangelovska.insights.R;

import java.util.List;

/**
 * Created by marinaangelovska on 2/2/18.
 */

public class CustomAppsAdapter extends ArrayAdapter {
    Application application;
    TextView appName;
    ImageView appIcon;
    TextView appTime;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        application = (Application) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item_app, parent, false);
        }
        setupTextFields(convertView);
        fillUpTextFields(application);

        return convertView;
    }

    public CustomAppsAdapter(@NonNull Context context, List<Application> appsList) {
        super(context,0, appsList);

    }

    void setupTextFields(View convertView) {
        appName = (TextView) convertView.findViewById(R.id.appName);
        appIcon = (ImageView) convertView.findViewById(R.id.appImage);
        appTime = (TextView) convertView.findViewById(R.id.appTime);

    }

    void fillUpTextFields(Application application) {
        appName.setText(application.getName());
        appTime.setText(application.getTime() + " seconds this week");
        appIcon.setImageDrawable(application.getIcon());
        appName.setTextColor(Color.rgb(81,68,60));
        appTime.setTextColor(Color.rgb(81,68,60));
    }
}
