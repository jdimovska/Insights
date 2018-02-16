package com.example.marinaangelovska.insights.Comparators;

import com.example.marinaangelovska.insights.Model.NodeMessage;

import java.util.Comparator;

/**
 * Created by aleksandarsmilevski on 2/16/18.
 */

public class SizeComparator implements Comparator<NodeMessage> {
    @Override
    public int compare(NodeMessage callNodeOne, NodeMessage callNodeTwo) {
        return callNodeOne.getSize() < callNodeTwo.getSize() ? 1 : callNodeOne.getSize() == callNodeTwo.getSize() ? 0 : -1;

    }
}
