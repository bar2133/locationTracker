package com.example.locationtracker;

import java.util.ArrayList;

public class UserLocations {
    public String id;
    public ArrayList<LocationSample> locationSamples;

    public UserLocations() {
        locationSamples = new ArrayList<LocationSample>();
    }

    @Override
    public String toString() {
        return "UserLocations{" +
                "id='" + id + '\'' +
                ", locationSamples=" + locationSamples +
                '}';
    }
}
