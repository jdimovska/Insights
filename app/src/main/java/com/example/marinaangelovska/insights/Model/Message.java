package com.example.marinaangelovska.insights.Model;

import java.util.Date;

/**
 * Created by Jona Dimovska on 31.1.2018.
 */

public class Message {

    String phone;
    Date date;
    String content;
    public Message(String phone, Date date, String content){
        this.phone = phone;
        this.date = date;
        this.content = content;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
