package com.fanchen.location.hoowe;


import java.util.List;

/**
 */
public interface OnLocationUpdatedListener {

    void onReceiveLocation(HooweLocation location);

    void onReceiveLocation(List<HooweLocation> LocationList);

    void onLocationTrackerExist();

    void onLocationTrackerNotRun();

}
