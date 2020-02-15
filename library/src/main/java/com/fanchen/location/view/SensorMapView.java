package com.fanchen.location.view;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.RouteLineWrap;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.fanchen.R;
import com.fanchen.location.utils.CommonUtils;

import static android.content.Context.SENSOR_SERVICE;

public class SensorMapView extends FrameLayout implements BaiduMap.OnMapClickListener, BDLocationListener,
        OnGetRoutePlanResultListener, SensorEventListener {

    private OverlayManager mRouteOverlay = null;
    private SensorManager mSensorManager = null;
    private RouteLineWrap.Type mMode = null;


    private float mLastX = 0f;
    private float mCurrentDirection = 0f;

    private boolean mStartLocation = false;

    private RoutePlanSearch mSearch = null;   // 搜索模块

    public LocationClient mLocClient = null;
    public MapView mMapView = null;
    public BaiduMap mBaiduMap = null;
    public  BDLocation mBDLocation = null;

    public double latitude = 0.0;

    public double longitude = 0.0;

    OnRouteResultListener mRouteResultListener = null;

    public SensorMapView(Context context) {
        super(context);
        initView(context);
    }

    public SensorMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SensorMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        //获取传感器管理服务
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        mMapView = new MapView(context);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        View child = mMapView.getChildAt(1);// 隐藏logo
        if ((child instanceof ImageView ||child instanceof ZoomControls)){
            child.setVisibility(View.INVISIBLE);
        }
        // 定位初始化
        mLocClient = new LocationClient(context);
        mLocClient .registerLocationListener(this);
        mLocClient .setLocOption( createOption());
        // 开启定位图层
        mBaiduMap .setMyLocationEnabled(true);
        mBaiduMap .setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        addView(mMapView);
    }

    public void startLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        mStartLocation = true;
        mLocClient.start();
    }

    public void onDestroy() {
        CommonUtils.cencelDialog();
        mSearch.destroy();
        mMapView.onDestroy();
    }

    public void onResume() {
        mMapView.onResume();
        Sensor defaultSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, defaultSensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void onPause() {
        mMapView.onPause();
        mSensorManager.unregisterListener(this);
    }

    /**
     * @return
     */
    private LocationClientOption createOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll");// 设置坐标类型
        option.setScanSpan(30 * 1000);
        option.setIsNeedAddress(true);
        return option;
    }

    /**
     * @param type
     * @param line
     */
    private void addToMap(RouteLineWrap.Type type, RouteLine<?> line) {
        mRouteOverlay .removeFromMap();
        this.mMode = type;
        if(type == RouteLineWrap.Type.WALKING_ROUTE){
            WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);
            mRouteOverlay = overlay;
            mBaiduMap .setOnMarkerClickListener(overlay);
            overlay.setData((WalkingRouteLine)line );
        }else if(type == RouteLineWrap.Type.TRANSIT_ROUTE){
            TransitRouteOverlay overlay = new TransitRouteOverlay(mBaiduMap);
            mRouteOverlay = overlay;
            mBaiduMap .setOnMarkerClickListener(overlay);
            overlay.setData((TransitRouteLine)line);
        }else if(type == RouteLineWrap.Type.DRIVING_ROUTE){
            DrivingRouteOverlay overlay = new  DrivingRouteOverlay(mBaiduMap);
            mRouteOverlay = overlay;
            mBaiduMap .setOnMarkerClickListener(overlay);
            overlay.setData((DrivingRouteLine)line);
        }
        mRouteOverlay .addToMap();
        mRouteOverlay .zoomToSpan();
        CommonUtils.cencelDialog();
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    public void drivingSearch(DrivingRoutePlanOption search) {
        CommonUtils.showDialogNumal(getContext(),getContext().getString(R.string.map_search_ing));
        mSearch.drivingSearch(search);
    }

    public void transitSearch(TransitRoutePlanOption search) {
        CommonUtils.showDialogNumal(getContext(),getContext().getString(R.string.map_search_ing));
        mSearch.transitSearch(search);
    }

    public void walkingSearch(WalkingRoutePlanOption search) {
        CommonUtils.showDialogNumal(getContext(),getContext().getString(R.string.map_search_ing));
        mSearch.walkingSearch(search);
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location == null) return;
        this.mBDLocation = location;
        if (mStartLocation) {//第一次定位成功自动规划路线
            mStartLocation = false;
            PlanNode startNode = PlanNode.withLocation(new LatLng(location.getLatitude(), location.getLongitude()));
            PlanNode endNode = PlanNode.withLocation(new LatLng(latitude, longitude));
            drivingSearch(new DrivingRoutePlanOption().from(startNode).to(endNode));
        }
        // 定位成功后就停止
        if (mLocClient.isStarted())
            mLocClient .stop();
        // 获取经纬度
        double mCurrentLat = location.getLatitude();
        double mCurrentLon = location.getLongitude();
        LatLng ll = new LatLng(mCurrentLat, mCurrentLon);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(18.0f);
        mMapView .getMap().animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        // 显示个人位置图标
        MyLocationData.Builder locationData = new MyLocationData.Builder();
        locationData.latitude(mCurrentLat);
        locationData.longitude(mCurrentLon);
        mBaiduMap.setMyLocationData(locationData.build());
    }

    /**
     * @param result
     * @return
     */

    private boolean checkSearchResult(SearchResult result) {
        if(result == null) return false;
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            Toast.makeText(getContext(),getContext().getString(R.string.map_address_ambiguity),Toast.LENGTH_SHORT).show();
        }if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getContext(),getContext().getString(R.string.map_address_not_find),Toast.LENGTH_SHORT).show();
        } else if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            return true;
        }
        return false;
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        CommonUtils.cencelDialog();
        if (!checkSearchResult(result)) return;
        if (result.getRouteLines().size() > 1) {
            if (mRouteResultListener != null) {
                mRouteResultListener.onRouteResult(result);
            } else {
                addToMap(RouteLineWrap.Type.WALKING_ROUTE, result.getRouteLines().get(0));
            }
        } else if (result.getRouteLines().size() == 1) {
            addToMap(RouteLineWrap.Type.WALKING_ROUTE, result.getRouteLines().get(0));
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
        CommonUtils.cencelDialog();
        if (!checkSearchResult(result)) return;
        if (result.getRouteLines().size() > 1) {
            if (mRouteResultListener != null) {
                mRouteResultListener.onRouteResult(result);
            } else {
                addToMap(RouteLineWrap.Type.TRANSIT_ROUTE, result.getRouteLines().get(0));
            }
        } else if (result.getRouteLines().size() == 1) {
            addToMap(RouteLineWrap.Type.TRANSIT_ROUTE, result.getRouteLines().get(0));
        }
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        CommonUtils.cencelDialog();
        if (!checkSearchResult(result)) return;
        if (result.getRouteLines().size() > 1) {
            if (mRouteResultListener != null) {
                mRouteResultListener.onRouteResult(result);
            } else {
                addToMap(RouteLineWrap.Type.DRIVING_ROUTE, result.getRouteLines().get(0));
            }
        } else if (result.getRouteLines().size() == 1) {
            addToMap(RouteLineWrap.Type.DRIVING_ROUTE, result.getRouteLines().get(0));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(mBDLocation == null || event == null) return;
        float x = event.values[SensorManager.DATA_X];
        if (Math.abs(x - mLastX) > 1.0) {
            mCurrentDirection = x; // 此处设置开发者获取到的方向信息，顺时针0-360
            MyLocationData.Builder locData =new MyLocationData.Builder();
            locData.accuracy(mBDLocation.getRadius());
            locData.direction(mCurrentDirection);
            locData.latitude(mBDLocation.getLatitude());
            locData.longitude(mBDLocation.getLongitude());
            mBaiduMap .setMyLocationData(locData.build());
        }
        mLastX = x;
    }

    interface OnRouteResultListener {

        void onRouteResult(SearchResult result);

    }

}
