package com.example.locationtracker;

import com.google.firebase.Timestamp;

public class LocationSample {
    public String userId;
    public Timestamp timestamp;
    public double longitude;
    public double latitude;

    public LocationSample() {
    }

    @Override
    public String toString() {
        return "LocationSample{" +
                "userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
