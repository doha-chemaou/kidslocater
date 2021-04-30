package com.example.localiser.Models;

import java.util.Date;

public class Position {

    private Date date;
    private double latitude;
    private double longtitude;

    public Position(){

    }
    public Position(Date date, double latitude, double longtitude) {
        this.date = date;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public Date getDate() {
        return date;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }
}
