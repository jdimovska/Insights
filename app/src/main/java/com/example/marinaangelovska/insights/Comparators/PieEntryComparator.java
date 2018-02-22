package com.example.marinaangelovska.insights.Comparators;

import com.github.mikephil.charting.data.PieEntry;

import java.util.Comparator;

/**
 * Created by Jona Dimovska on 22.2.2018.
 */

public class PieEntryComparator implements Comparator<PieEntry> {


    @Override
    public int compare(PieEntry pieEntry, PieEntry pieEntry2) {
        return pieEntry.getValue() < pieEntry2.getValue() ? 1 : pieEntry.getValue() == pieEntry2.getValue() ? 0 : -1;
    }
}
