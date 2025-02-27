package com.example.newtracking.model;

public class Bus {


    private String busId;
    private String busName;



    // Default constructor required for Firebase
    public Bus() {
    }

    public Bus(String busId, String busName) {
        this.busId = busId;
        this.busName = busName;


    }

    // Getters and Setters
    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }


}
