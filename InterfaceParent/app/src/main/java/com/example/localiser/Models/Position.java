package com.example.localiser.Models;

public class Position {

    private String date;
    private double latitude;
    private double longtitude;

    public Position(){

    }
    public Position(String date, double latitude, double longtitude) {
        this.date = date;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public String getDate() {
        return date;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }
}
