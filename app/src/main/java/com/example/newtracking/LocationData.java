package com.example.newtracking;

public class LocationData {

    public double latitude;
    public double longitude;

    // Default constructor required for calls to DataSnapshot.getValue(LocationData.class)
    public LocationData() {
    }

    public LocationData(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
