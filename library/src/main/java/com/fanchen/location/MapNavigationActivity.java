package com.fanchen.location;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.fanchen.base.BaseIActivity;
import com.fanchen.ui.R;
import com.fanchen.location.utils.CommonUtils;
import com.fanchen.location.view.SensorMapView;

public class MapNavigationActivity extends BaseIActivity {

    public static final String CITY = "CITY";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String ADDRESS = "ADDRESS";

    private String city = "";
    private double latitude = 0;
    private double longitude = 0;

    private SensorMapView mv_main;

    @Override
    protected boolean isSwipeActivity() {
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        setTheme(LocationPicker.themeId);
        //windowTranslucentStatus
        //BaseMapNavStyle
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(this.getApplication());
        SDKInitializer.setHttpsEnable(true);
        setContentView(R.layout.activity_map_navigation);
        mv_main = findViewById(R.id.mv_main_map);
        TextView tv_address = findViewById(R.id.tv_address_map);
        Toolbar toolbar_map = findViewById(R.id.toolbar_base_map);
        toolbar_map.setTitle("");
        setSupportActionBar(toolbar_map);
        city = getIntent().getStringExtra(CITY);
        latitude = getIntent().getDoubleExtra(LATITUDE, 0);
        longitude = getIntent().getDoubleExtra(LONGITUDE, 0);
        tv_address.setText(getIntent().getStringExtra(ADDRESS));
        mv_main.startLocation(latitude, longitude);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        BDLocation dbLocation = mv_main.mBDLocation;
        if (dbLocation != null) {
            PlanNode startNode = PlanNode.withLocation(new LatLng(dbLocation.getLatitude(), dbLocation.getLongitude()));
            PlanNode endNode = PlanNode.withLocation(new LatLng(latitude, longitude));
            if (item.getItemId() == R.id.action_drive) {
                mv_main.drivingSearch(new DrivingRoutePlanOption().from(startNode).to(endNode));
            } else if (item.getItemId() == R.id.action_public) {
                mv_main.transitSearch(new TransitRoutePlanOption().city(city).from(startNode).to(endNode));
            } else if (item.getItemId() == R.id.action_walk) {
                mv_main.walkingSearch(new WalkingRoutePlanOption().from(startNode).to(endNode));
            }
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mv_main.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mv_main.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mv_main.onDestroy();
    }

    public void onClick(View v) {
        BaiduMap baiduMap = mv_main.mBaiduMap;
        LocationClient client = mv_main.mLocClient;
        if (v.getId() == R.id.bt_zoom_out) {
            if (baiduMap.getMinZoomLevel() >= baiduMap.getMapStatus().zoom) return;
            MapStatus.Builder param = new MapStatus.Builder();
            param.zoom(baiduMap.getMapStatus().zoom - 0.5f);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(param.build()));
        } else if (v.getId() == R.id.bt_zoom_in) {
            if (baiduMap.getMaxZoomLevel() <= baiduMap.getMapStatus().zoom) return;
            MapStatus.Builder param = new MapStatus.Builder();
            param.zoom(baiduMap.getMapStatus().zoom + 0.5f);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(param.build()));
        } else if (v.getId() == R.id.fab_local) {
            baiduMap.setMyLocationEnabled(true);
            client.start();
        } else if (v.getId() == R.id.fab_goto) {
            CommonUtils.showMapChoiceDialog(this,latitude,longitude,city);
        } else {
            finish();
        }
    }

}
