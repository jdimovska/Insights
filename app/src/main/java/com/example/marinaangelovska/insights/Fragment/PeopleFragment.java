package com.example.marinaangelovska.insights.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.marinaangelovska.insights.Adapters.CustomPeopleAdapter;
import com.example.marinaangelovska.insights.Model.Person;
import com.example.marinaangelovska.insights.R;
import com.example.marinaangelovska.insights.Service.PeopleService;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.example.marinaangelovska.insights.Activity.MainActivity.dialog;

/**
 * Created by Jona Dimovska on 31.1.2018.
 */

public class PeopleFragment extends Fragment {

    PeopleService peopleService;
    ArrayList<Person> peopleList;
    CustomPeopleAdapter adapter;
    View view;
    ListView viewList;
    ArrayList<Person> allPeople;
    EditText search;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_people, container, false);
        search = (EditText)view.findViewById(R.id.search);
        allPeople = new ArrayList<Person>();
        peopleService = new PeopleService(getActivity());
        peopleList = peopleService.getPeople();
        adapter=new CustomPeopleAdapter(getActivity(), peopleList);
        allPeople.addAll(peopleList);

        viewList = (ListView)view.findViewById (R.id.peopleList);
        viewList.setAdapter(adapter);
        viewList.setTextFilterEnabled(true);
        adapter.notifyDataSetChanged();
        viewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Bundle arguments = new Bundle();
                PeopleDetailsFragment peopleDetailsFragment = new PeopleDetailsFragment();
                String name = peopleList.get(position).getName();
                arguments.putString("name", name);
                String number = peopleList.get(position).getNumber();
                arguments.putString("number", number.replaceAll("\\s+",""));

                peopleDetailsFragment.setArguments(arguments);
                android.app.FragmentManager fragmentManager = getFragmentManager();
                android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.root_layout, peopleDetailsFragment);
                fragmentTransaction.addToBackStack("back");
                fragmentTransaction.commit();
            }
        });

        doSearch();

        return view;
    }

    private void doSearch() {
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = search.getText().toString().toLowerCase(Locale.getDefault());
                filter(text);
            }
        });
    }
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        peopleList.clear();
        if (charText.length() == 0) {
            peopleList.addAll(allPeople);
        } else {
            for (Person wp : allPeople) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    peopleList.add(wp);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        search.setText("");
        search.setTextColor(Color.rgb(81,68,60));
        dialog.hide();

    }
    @Override
    public void onStart() {
        super.onStart();
        dialog.hide();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
