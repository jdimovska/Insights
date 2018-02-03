package com.example.marinaangelovska.insights.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.marinaangelovska.insights.Adapters.CustomPeopleAdapter;
import com.example.marinaangelovska.insights.Model.Person;
import com.example.marinaangelovska.insights.R;
import com.example.marinaangelovska.insights.Service.PeopleService;

import java.util.ArrayList;

/**
 * Created by Jona Dimovska on 31.1.2018.
 */

public class PeopleFragment extends Fragment {

    PeopleService peopleService;
    ArrayList<Person> peopleList;
    CustomPeopleAdapter adapter;
    View view;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_people, container, false);
        peopleService = new PeopleService(getActivity());
        peopleList = peopleService.getPeople();
        adapter=new CustomPeopleAdapter(getActivity(), peopleList);

        ListView viewList=(ListView)view.findViewById (R.id.peopleList);
        viewList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}