package com.fanchen.location.provider;

import android.content.Context;
import android.content.Intent;

import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;

import java.util.ArrayList;
import java.util.List;

import com.fanchen.location.db.LocationDBHelper;
import com.fanchen.location.service.TrackerService;
import com.fanchen.location.utils.ServiceUtils;


/**
 */
public class LocationProvider {

    public static final String TAG = "LocationProvider";

    private Context mContext;

    private LocationClientOption mOption;

    private LocationTracker mTracker;

    private boolean hasTracker = false;

    private static int LOCATION_FREQUENCY = 1000; // 默认定位频度 1 秒/次 (必须 >= 1 秒)

    private int mFrequency = LOCATION_FREQUENCY; // 定位频度

    /**
     * 类级的内部类，也就是静态类的成员式内部类，该内部类的实例与外部类的实例
     * 没有绑定关系，而且只有被调用时才会装载，从而实现了延迟加载
     *
     * @author dream
     */
    private static class LocationProviderHolder {
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        private static LocationProvider instance = new LocationProvider();
    }

    /**
     * 私有化构造方法
     */
    private LocationProvider() {

    }

    public static LocationProvider getInstance() {
        return LocationProviderHolder.instance;
    }

    /**
     * initialize Map SDK
     *
     * @param context
     */
    public void initialize(Context context) {
        this.mContext = context;
        SDKInitializer.initialize(context);
        SDKInitializer.setHttpsEnable(true);
    }

    /**
     * 是否存在追踪任务
     *
     * @return
     */
    public boolean isHasTracker() {
        return hasTracker;
    }

    public int getmFrequency() {
        return mFrequency;
    }

    /**
     * 获取当前位置（实时定位）
     *
     * @param listener
     */
    public void getCurrentLocation(OnLocationListener listener) {
        this.getCurrentLocation(null, listener);
    }

    /**
     * 获取当前位置（实时定位）
     *
     * @param mOption
     * @param listener
     */
    public void getCurrentLocation(LocationClientOption mOption, OnLocationListener listener) {
        if (hasTracker) { // 有追踪任务在运行
            // 获取最新位置返回
            Location location = LocationDBHelper.getHelper(mContext).getLatestLocation();
            listener.onReceiveLocation(location);
        } else {
            LocationTracker locationTracker = new LocationTracker(mContext);
            if (mOption != null) {
                locationTracker.setLocationOption(mOption);
            } else {
                locationTracker.setLocationOption(locationTracker.getDefaultLocationClientOption());
            }
            locationTracker.registerListener(listener);
            locationTracker.start();
        }
    }

    /**
     * 异步 获取特定时间该设备的位置（前提是指定时间开启过位置追踪）
     *
     * @param time
     */
    public void getLocationByTime(long time, OnLocationListener listener) {
        Location location = LocationDBHelper.getHelper(mContext).locDBLoadByTime(time);
        if (location != null) {
            listener.onReceiveLocation(location);
        } else {
            listener.onLocationTrackerNotRun();
        }
    }

    /**
     * 异步 获取特定时间断该设备的位置（前提是指定时间断开启过位置追踪）
     *
     * @param startTime
     * @param endTime
     */
    public void getLocationByPeriod(Long startTime, long endTime, OnLocationListener listener) {
        List<Location> locationList = new ArrayList<>();
        locationList.addAll(LocationDBHelper.getHelper(mContext).locDBLoadByPeriod(startTime, endTime));
        if (locationList.size() > 0) {
            listener.onReceiveLocation(locationList);
        } else {
            listener.onLocationTrackerNotRun();
        }
    }

    /**
     * 同步 获取特定时间该设备的位置（前提是指定时间开启过位置追踪）
     *
     * @param time
     */
    public Location getLocationByTime(long time) {
        return LocationDBHelper.getHelper(mContext).locDBLoadByTime(time);
    }

    /**
     * 同步 获取特定时间断该设备的位置（前提是指定时间断开启过位置追踪）
     *
     * @param startTime
     * @param endTime
     */
    public List<Location> getLocationByPeriod(Long startTime, long endTime) {
        return LocationDBHelper.getHelper(mContext).locDBLoadByPeriod(startTime, endTime);
    }

    /**
     * 开始位置追踪 (启动追踪服务)
     */
    public void startTracker() {
        Intent intent = new Intent(mContext, TrackerService.class);
        mContext.startService(intent);
    }

    /**
     * 开始位置追踪
     *
     * @param listener
     */
    public void startTracker(OnLocationListener listener) {
        this.startTracker(null, listener);
    }

    /**
     * 开始位置追踪
     *
     * @param mOption  定位配置
     * @param listener
     */
    public void startTracker(LocationClientOption mOption, OnLocationListener listener) {
        if (!hasTracker) {
            mTracker = new LocationTracker(mContext);
            if (mOption == null) {
                mOption = new LocationClientOption();
                mOption.setOpenGps(true); // 打开gps
                mOption.setCoorType("bd09ll"); // 设置坐标类型
                mOption.setScanSpan(30 * 1000);
                mOption.setIsNeedAddress(true);
            }
            mTracker.setLocationOption(mOption);
            mTracker.registerListener(listener);
            mTracker.start();
            hasTracker = true;
        } else { // 已经存在追踪任务

            // TODO: 2017/8/28 回调通知调用者
            if (listener != null) {
                listener.onLocationTrackerExist();
            }
        }
    }

    /**
     * 结束位置追踪
     */
    public void endTracker() {
        if (mTracker != null) {
            mTracker.unregisterListener();
            mTracker.stop();
        }
        hasTracker = false;
        if (ServiceUtils.isServiceRun(mContext, ServiceUtils.TRACKER_SERVICE_CLASS_NAME)) {
            Intent intent = new Intent(mContext, TrackerService.class);
            mContext.stopService(intent);
        }

    }

}
