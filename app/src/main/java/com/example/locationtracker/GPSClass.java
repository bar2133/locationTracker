package com.example.locationtracker;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.LocationListener;

public class GPSClass implements LocationListener {

    public void onLocationChanged(Location location) {
        // Called when a new location is found by the network location provider.
        Log.i("Message: ", "Location changed, " + location.getAccuracy() + " , " + location.getLatitude() + "," + location.getLongitude());
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
    }
}