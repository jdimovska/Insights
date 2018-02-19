package com.example.marinaangelovska.insights.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.marinaangelovska.insights.R;

/**
 * Created by marinaangelovska on 2/19/18.
 */

public class AboutFragment extends Fragment {
    View view;
    TextView text;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about, container, false);
        text = (TextView) view.findViewById (R.id.about_text_view);
        text.setText("\u2022 Insights app is an application for reviewing the time spent on your personal mobile phone.\n\n" +
                "\u2022 We guarantee you that the data collected from your phone will not be published, used or stored anywhere.\n\n" +
                " \u2022 Everything you see in your application is collected locally on your phone. Once you close the application all the data are deleted immediately. \n\n" +
                " \u2022 Thank you for using Insights app.\n\n");
        return view;
    }
}
