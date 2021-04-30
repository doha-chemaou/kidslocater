package com.example.testlocalisation.Models;

public class Message {
    private  String date;
    private String number;
    private String body;
    private String type;

    public  Message(){

    }

    public Message(String date, String number, String body, String type) {
        this.date = date;
        this.number = number;
        this.body = body;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
