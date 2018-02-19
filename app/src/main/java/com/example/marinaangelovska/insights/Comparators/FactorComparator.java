package com.example.marinaangelovska.insights.Comparators;

import com.example.marinaangelovska.insights.Model.Person;

import java.util.Comparator;

/**
 * Created by aleksandarsmilevski on 2/16/18.
 */

public class FactorComparator implements Comparator<Person> {
    @Override
    public int compare(Person person1, Person person2) {
        if(person1.getFactor() < person2.getFactor())
            return 1;
        else if(person1.getFactor() > person2.getFactor())
            return -1;
        else if(person1.getName().compareTo(person2.getName()) > 0)
            return 1;
        else return -1;

    }
}
