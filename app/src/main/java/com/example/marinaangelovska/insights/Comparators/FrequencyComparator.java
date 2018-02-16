package com.example.marinaangelovska.insights.Comparators;

import com.example.marinaangelovska.insights.Model.NodeContact;

import java.util.Comparator;

/**
 * Created by aleksandarsmilevski on 2/16/18.
 */

public class FrequencyComparator implements Comparator<NodeContact> {
    @Override
    public int compare(NodeContact callNodeContactOne, NodeContact callNodeContactTwo) {
        return callNodeContactOne.getOccurrence() < callNodeContactTwo.getOccurrence() ? 1 : callNodeContactOne.getOccurrence() == callNodeContactTwo.getOccurrence() ? 0 : -1;
    }
}
