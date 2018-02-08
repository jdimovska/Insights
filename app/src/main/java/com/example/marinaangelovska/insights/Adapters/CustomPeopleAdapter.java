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

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.marinaangelovska.insights.Model.Person;
import com.example.marinaangelovska.insights.R;

import java.util.ArrayList;

/**
 * Created by Jona Dimovska on 31.1.2018.
 */

public class CustomPeopleAdapter extends ArrayAdapter{
    TextView personName;
    TextView personNumber;
    Person person;
    ImageView personImage;
    Context context;
    public CustomPeopleAdapter(@NonNull Context context, ArrayList<Person> peopleList) {
        super(context,0, peopleList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        person = (Person) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item_person, parent, false);
        }

        context = parent.getContext();
        setupTextFields(convertView);
        fillUpTextFields(person);
        setupTextFields(convertView);

        personImage = (ImageView) convertView.findViewById(R.id.personImage);

        String initials[]= personName.getText().toString().split(" ");
        String letters = "";

        if (initials.length != 0) {
            if(initials.length > 1)
                letters = initials[0].substring(0,1) + initials[1].substring(0,1) + "";
            else
                letters = initials[0].substring(0,1) + "";
        } else {
            letters = "?";
        }

        //TextDrawable drawable = TextDrawable.builder().buildRound(letters,Color.parseColor("#696969"));

        TextDrawable drawable = TextDrawable.builder().buildRound(letters, Color.rgb		(193, 131, 141));
        //TextDrawable drawable = TextDrawable.builder().buildRound(letters,Color.rgb(38,174,144));
        personImage.setImageDrawable(drawable);
        return convertView;
    }

    void setupTextFields(View convertView) {
        personName = (TextView) convertView.findViewById(R.id.personName);
        personNumber = (TextView) convertView.findViewById(R.id.personNumber);

    }

    void fillUpTextFields(Person person) {
        personName.setText(person.getName());
        personNumber.setText(person.getNumber());
        personName.setTextColor(Color.rgb(81,68,60));
        personNumber.setTextColor(Color.rgb(81,68,60));
    }
}
