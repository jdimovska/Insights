package com.example.marinaangelovska.insights.Comparators;

import com.example.marinaangelovska.insights.Model.Person;

import java.util.Comparator;

/**
 * Created by aleksandarsmilevski on 2/16/18.
 */

public class FactorComparator implements Comparator<Person> {
    @Override
    public int compare(Person person1, Person person2) {
        return person1.getFactor() < person2.getFactor() ? 1 : person1.getFactor() == person2.getFactor() ? 0 : -1;
    }
}
