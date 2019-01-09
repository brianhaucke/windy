package com.brianhaucke.windy;

import android.location.LocationManager;
import android.location.Location;

public class FindLocation {

    Location location = new Location("");
    double currentLatitude = location.getLatitude();
    double currelntLongitude = location.getLongitude();
}
