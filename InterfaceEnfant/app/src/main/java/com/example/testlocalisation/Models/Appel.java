package com.example.testlocalisation.Models;

public class Appel {
    private String phNumber ;
    private   String callType ;
    private String callDate ;
    private String callDuration;
    private String callDir;

    public Appel(){

    }

    public Appel(String phNumber, String callType, String callDate, String callDuration, String callDir) {
        this.phNumber = phNumber;
        this.callType = callType;
        this.callDate = callDate;
        this.callDuration = callDuration;
        this.callDir = callDir;
    }

    public String getPhNumber() {
        return phNumber;
    }

    public void setPhNumber(String phNumber) {
        this.phNumber = phNumber;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getCallDir() {
        return callDir;
    }

    public void setCallDir(String callDir) {
        this.callDir = callDir;
    }
}
