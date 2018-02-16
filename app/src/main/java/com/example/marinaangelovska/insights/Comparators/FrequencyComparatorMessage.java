package com.example.marinaangelovska.insights.Comparators;

import com.example.marinaangelovska.insights.Model.NodeMessage;

import java.util.Comparator;

/**
 * Created by aleksandarsmilevski on 2/16/18.
 */

public class FrequencyComparatorMessage implements Comparator<NodeMessage> {
    @Override
    public int compare(NodeMessage callNodeOne, NodeMessage callNodeTwo) {
        return callNodeOne.getFrequency() < callNodeTwo.getFrequency() ? 1 : callNodeOne.getFrequency() == callNodeTwo.getFrequency() ? 0 : -1;

    }
}
