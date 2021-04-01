package com.example.testlocalisation.Models;


public class CircleZone {

    private double latitude,longitude;
    private float metter;

    public CircleZone(){

    }

    public CircleZone(double latitude, double longitude, float metter) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.metter = metter;
    }

    public double getLatitude() {
        return latitude;

    }

    public double getLongitude() {
        return longitude;
    }

    public float getMetter() {
        return metter;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setMetter(float metter) {
        this.metter = metter;
    }
}
