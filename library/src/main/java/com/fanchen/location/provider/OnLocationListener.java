package com.fanchen.location.provider;


import java.util.List;

/**
 */
public interface OnLocationListener {

    void onReceiveLocation(Location location);

    void onReceiveLocation(List<Location> LocationList);

    void onLocationTrackerExist();

    void onLocationTrackerNotRun();

}
