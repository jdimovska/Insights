package com.example.marinaangelovska.insights.Comparators;

import com.example.marinaangelovska.insights.Model.NodeContact;

import java.util.Comparator;

/**
 * Created by aleksandarsmilevski on 2/16/18.
 */

public class DurationComparator implements Comparator<NodeContact> {
    @Override
    public int compare(NodeContact callNodeContactOne, NodeContact callNodeContactTwo) {
        return callNodeContactOne.getDuration() < callNodeContactTwo.getDuration() ? 1 : callNodeContactOne.getDuration() == callNodeContactTwo.getDuration() ? 0 : -1;
    }
}

