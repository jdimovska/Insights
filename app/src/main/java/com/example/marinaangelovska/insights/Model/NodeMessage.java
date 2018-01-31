package com.example.marinaangelovska.insights.Model;

/**
 * Created by Jona Dimovska on 31.1.2018.
 */

public class NodeMessage {
    String number;
    int size;
    int frequency;

    public NodeMessage(String number, int size, int frequency){
        this.number = number;
        this.size = size;
        this.frequency = frequency;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
