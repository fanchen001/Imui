package com.fanchen.location;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanchen.R;
import com.fanchen.filepicker.util.UiUtils;
import com.fanchen.location.adapter.ItemDecorntion;
import com.fanchen.location.adapter.LocationAdapter;
import com.fanchen.location.bean.LocationBean;
import com.fanchen.location.utils.CommonUtils;
import com.fanchen.location.view.FuckBaiduView;
import com.jude.swipbackhelper.SwipeBackHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MapSendActivity extends AppCompatActivity implements OnGetGeoCoderResultListener,
        BaseQuickAdapter.OnItemClickListener, BaiduMap.OnMapStatusChangeListener, View.OnClickListener,
        BaiduMap.OnMapTouchListener, BDLocationListener, BaiduMap.SnapshotReadyCallback {

    private LocationBean lastLocation = null;

    private TextureMapView mMapView;
    private ProgressBar progress_bar;
    private TextView sendButton = null;

    private TextView tv_search;
    private RelativeLayout rl_search;
    private LocationBean currentLocation = null; //定位位置

    /**
     * 列表适配器
     */
    private LocationAdapter locatorAdapter;
    /**
     * 附近地点列表
     */
    private RecyclerView recyclerview;
    /**
     * 列表数据
     */
    private List<LocationBean> datas = new ArrayList<>();
    /**
     * 百度地图对象
     */
    private BaiduMap mBaiduMap;
    /**
     * 地理编码
     */
    private GeoCoder mSearch;
    /**
     * 定位
     */
    private LocationClient mLocClient;
    // MapView 中央对于的屏幕坐标
    private android.graphics.Point mCenterPoint = null;
    /**
     * 是否第一次定位
     */
    private boolean isFirstLoc = true;
    /**
     * 按钮：回到原地
     */
    private ImageView iv_re_location;
    private boolean isTouch = true;
    private GridLayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map_send);
        initView();
        initData();
        setListener();
        SwipeBackHelper.onCreate(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    private void initView() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.location_message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams layoutParams = mToolbar.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.height = layoutParams.height + UiUtils.dpToPx(this, 25);
                mToolbar.setLayoutParams(layoutParams);
            }

            mToolbar.setPadding(mToolbar.getPaddingLeft(), mToolbar.getPaddingTop() + UiUtils.dpToPx(this, 25),
                    mToolbar.getPaddingRight(), mToolbar.getPaddingBottom());
        }

        setSupportActionBar(mToolbar);

        mMapView = ((FuckBaiduView) findViewById(R.id.bmapView)).getMapView();
        sendButton = (TextView) findViewById(R.id.btn_rtc);
        iv_re_location = (ImageView) findViewById(R.id.iv_re_location);
        tv_search = (TextView) findViewById(R.id.tv_search);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        rl_search = (RelativeLayout) findViewById(R.id.rl_search);
    }

    private void initData() {
        sendButton.setText(R.string.button_send);
        layoutManager = new GridLayoutManager(getBaseContext(), 1);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.addItemDecoration(new ItemDecorntion(0, 1, 0, 1));
        sendButton.setVisibility(View.VISIBLE);
        initMap();
        // 列表初始化
        locatorAdapter = new LocationAdapter(datas);
        locatorAdapter.bindToRecyclerView(recyclerview);
//        recyclerview.setAdapter(locatorAdapter);
    }

    private void setListener() {
        mMapView.setLongClickable(true);
        iv_re_location.setOnClickListener(this);
        tv_search.setOnClickListener(this);

        locatorAdapter.setOnItemClickListener(this);
//        locatorAdapter.setClickListener(this);
        rl_search.setOnClickListener(this);
        sendButton.setOnClickListener(this);
    }

    private void initMap() {
        CommonUtils.showDialogNumal(MapSendActivity.this, R.string.locationing);
        mBaiduMap = mMapView.getMap();
        // 设置为普通矢量图地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        View child = mMapView.getChildAt(1);
        if ((child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        // 不显示地图上比例尺
        mMapView.showScaleControl(false);
        // 不显示地图缩放控件（按钮控制栏）
        mMapView.showZoomControls(false);
        // 设置缩放比例(500米)
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setOnMapTouchListener(this);
        // 初始化当前 MapView 中心屏幕坐标
        mCenterPoint = mBaiduMap.getMapStatus().targetScreen;
        // 地理编码
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        // 地图状态监听
        mBaiduMap.setOnMapStatusChangeListener(this);
        // 定位初始化
        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("gcj02");
        option.setScanSpan(30000);
        option.setAddrType("all");
        mLocClient.setLocOption(option);
        // 可定位
        mBaiduMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        stopLocation();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        startLocation();
        super.onResume();
    }

    private void startLocation() {
        if (mLocClient != null) {
            mLocClient.start();
        }
    }

    private void stopLocation() {
        if (mLocClient != null) {
            mLocClient.stop();
        }
    }

    @Override
    protected void onDestroy() {
        stopLocation();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        super.onDestroy();
        SwipeBackHelper.onDestroy(this);
    }

    @Override
    public void onSnapshotReady(Bitmap bitmap) {
        File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/location/");
        if (!file1.exists()) file1.mkdirs();
        File file = new File(file1.getAbsolutePath() + "/" + System.currentTimeMillis() + ".png");
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
            runOnUiThread(new CencelRunnable());
            Intent intent = MapSendActivity.this.getIntent();
            intent.putExtra("Location", lastLocation);
            intent.putExtra("thumbnailPath", file.getAbsolutePath());
            MapSendActivity.this.setResult(RESULT_OK, intent);
            finish();
        } catch (Exception e) {
            runOnUiThread(new CencelRunnable());
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        // map view 销毁后不在处理新接收的位置
        CommonUtils.cencelDialog();
        if (currentLocation != null && currentLocation.getLat() == location.getLatitude() && currentLocation.getLng() == location.getLongitude()) {
            return;
        }
        if (location == null || mMapView == null) return;
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        Double mLatitude = location.getLatitude();
        Double mLongitude = location.getLongitude();
        LatLng currentLatLng = new LatLng(mLatitude, mLongitude);
        stopLocation();
        // 是否第一次定位
        if (!isFirstLoc) return;
        isFirstLoc = false;
        // 实现动画跳转
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(currentLatLng);
        mBaiduMap.animateMapStatus(u);
        mSearch.reverseGeoCode((new ReverseGeoCodeOption()).location(currentLatLng));
    }

    public void back(View v) {
        finish();
    }

    public void sendLocation(View view) {
        CommonUtils.showDialogNumal(MapSendActivity.this, R.string.are_doing);
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int hight = point.y;
        Rect rect = new Rect(0, (hight / 2) - (width / 4) - 180, width, (hight / 2) + (width / 4) - 180);
        mBaiduMap.snapshotScope(rect, this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_re_location) {
            //重定位
            isTouch = true;
            isFirstLoc = true;
            initMap();
            startLocation();
        } else if (v.getId() == R.id.tv_search || v.getId() == R.id.rl_search) {
            CommonUtils.showSearchPup(MapSendActivity.this, lastLocation, this);
        } else if (v.getId() == R.id.btn_rtc) {
            if(lastLocation == null){
                CommonUtils.showToastShort(this,R.string.location_is_empty);
            }else{
                sendLocation(v);
            }
        }
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
    }


    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR || !isTouch) {
            return;
        }
        String address = result.getAddress();
        // 获取反向地理编码结果
        if (result.getLocation() != null && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(result.getAddressDetail().city)) {
            if (currentLocation == null) {
                currentLocation = new LocationBean();
                currentLocation.setAddress(address);
                String city = result.getAddressDetail().city;
                currentLocation.setLat(result.getLocation().latitude);
                currentLocation.setLng(result.getLocation().longitude);
                currentLocation.setCity(city);
                currentLocation.setName(getString(R.string.now_address));
                lastLocation = currentLocation;
            }
        }
        datas.clear();
        if (!TextUtils.isEmpty(address)) {
            datas.add(currentLocation);
        }
        List<PoiInfo> poiList = result.getPoiList();
        if (poiList == null || poiList.isEmpty()) {
            locatorAdapter.notifyDataSetChanged();
            progress_bar.setVisibility(View.GONE);
            return;
        }
        for (int i = 0; i < poiList.size(); i++) {
            PoiInfo info = poiList.get(i);
            if (info.location != null && !TextUtils.isEmpty(info.address) && !TextUtils.isEmpty(info.city)) {
                LocationBean location = new LocationBean();
                location.setAddress(info.address);
                location.setLat(info.location.latitude);
                location.setLng(info.location.longitude);
                location.setCity(info.city);
                location.setName(info.name);
                if (!datas.contains(location)) {
                    datas.add(location);
                }
            }
        }
        locatorAdapter.notifyDataSetChanged();
        progress_bar.setVisibility(View.GONE);
    }

    /**
     * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
     *
     * @param status 地图状态改变开始时的地图状态
     */
    public void onMapStatusChangeStart(MapStatus status) {
    }

    /**
     * 地图状态变化中
     *
     * @param status 当前地图状态
     */
    public void onMapStatusChange(MapStatus status) {
    }

    /**
     * 地图状态改变结束
     *
     * @param status 地图状态改变结束后的地图状态
     */
    public void onMapStatusChangeFinish(MapStatus status) {
        // 显示列表，查找附近的地点
        if (!isTouch) return;
        searchPoi(status.target);
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        isTouch = true;
    }

    /**
     * 显示列表，查找附近的地点
     */
    public void searchPoi(LatLng latLng) {
        if (mCenterPoint == null) return;
        if (latLng != null) {
            mSearch.reverseGeoCode((new ReverseGeoCodeOption()).location(latLng));
        } else {
            // 获取当前 MapView 中心屏幕坐标对应的地理坐标
            LatLng currentLatLng = mBaiduMap.getProjection().fromScreenLocation(mCenterPoint);
            // 发起反地理编码检索
            mSearch.reverseGeoCode((new ReverseGeoCodeOption()).location(currentLatLng));
        }
        progress_bar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (adapter == locatorAdapter) {
            isTouch = false;
            lastLocation = (LocationBean) adapter.getItem(position);
            locatorAdapter.setSelectSearchItemIndex(position);
            locatorAdapter.notifyDataSetChanged();
            mBaiduMap.clear();
            LatLng latLng = new LatLng(lastLocation.getLat(), lastLocation.getLng());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.animateMapStatus(u);
            mSearch.reverseGeoCode((new ReverseGeoCodeOption()).location(latLng));
        } else {
            LocationBean location = (LocationBean) adapter.getItem(position);
            locatorAdapter.setSelectSearchItemIndex(0);
            locatorAdapter.notifyDataSetChanged();
            // 获取经纬度
            LatLng latLng = new LatLng(location.getLat(), location.getLng());
            // 实现动画跳转
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.animateMapStatus(u);
            mSearch.reverseGeoCode((new ReverseGeoCodeOption()).location(latLng));
        }
    }

    private static class CencelRunnable implements Runnable {

        @Override
        public void run() {
            CommonUtils.cencelDialog();
        }

    }
}
