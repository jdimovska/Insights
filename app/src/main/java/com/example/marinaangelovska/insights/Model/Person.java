package com.example.marinaangelovska.insights.Model;

/**
 * Created by Jona Dimovska on 31.1.2018.
 */

public class Person {

    String name;
    String number;
    double factor;

    public Person(String name, String number, double factor){
        this.name = name;
        this.number = number;
        this.factor = factor;
    }

    public String getName() {
        return name;
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

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }
}
