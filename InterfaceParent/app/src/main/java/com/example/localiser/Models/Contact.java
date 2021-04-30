package com.example.localiser.Models;


import androidx.annotation.NonNull;

public class Contact {
    private String name;
    private String phone;

    public Contact(){

    }

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    @NonNull
    @Override
    public String toString() {
        return ""+this.name+"\n"+this.phone;
    }
}
