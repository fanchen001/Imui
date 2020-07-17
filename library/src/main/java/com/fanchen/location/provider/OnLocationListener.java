package com.fanchen.location.provider;


import java.util.List;

/**
 */
public interface OnLocationListener extends BaseLocationListener {

    void onReceiveLocation(List<Location> LocationList);

    void onLocationTrackerExist();

    void onLocationTrackerNotRun();

}
