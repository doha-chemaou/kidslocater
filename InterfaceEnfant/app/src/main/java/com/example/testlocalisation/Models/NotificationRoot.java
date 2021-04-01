package com.example.testlocalisation.Models;

import com.google.gson.annotations.SerializedName;

public class NotificationRoot {

    private NotificationBody data;

    private String to;

    public NotificationRoot(NotificationBody body, String to) {
        this.data = body;
        this.to = to;
    }
    public  NotificationRoot(){}

    public NotificationBody getBody() {
        return data;
    }

    public void setBody(NotificationBody body) {
        this.data = body;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
