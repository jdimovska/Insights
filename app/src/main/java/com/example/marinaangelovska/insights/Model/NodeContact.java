package com.example.marinaangelovska.insights.Model;

/**
 * Created by Jona Dimovska on 28.1.2018.
 */

public class NodeContact {
    String name;
    String number;
    int occurrence;
    int duration;
    public NodeContact(String name, String number, int occurrence, int duration) {
        this.name  = name;
        this.number = number;
        this.occurrence = occurrence;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setName(String name) {
        this.name = name;

    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }
}
