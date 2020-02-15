package com.fanchen.location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.fanchen.R;
import com.fanchen.location.bean.LocationBean;
import com.fanchen.location.utils.CommonUtils;

import java.util.Map;

public final class LocationPicker {

    public static int themeId = R.style.BaseMapStyle;

    public static void startSendActivity(Activity context, int code) {
        try {
            context.startActivityForResult(new Intent(context, MapSendActivity.class), code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startNavigayionActivity(Activity context) {
        try {
            context.startActivity(new Intent(context, MapNavigationActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map.Entry<String, LocationBean> getLocation(Intent data) {
        if (data == null) {
            return null;
        }
        final LocationBean bean = data.getParcelableExtra("Location");
        final String thumbnailPath = data.getStringExtra("thumbnailPath");
        return new Map.Entry<String, LocationBean>() {
            @Override
            public String getKey() {
                return thumbnailPath;
            }

            @Override
            public LocationBean getValue() {
                return bean;
            }

            @Override
            public LocationBean setValue(LocationBean value) {
                return null;
            }
        };
    }

    public static void showMapChoiceDialog(final Context context, final double latitude, final double longitude, final String city) {
        CommonUtils.showMapChoiceDialog(context, latitude, longitude, city);
    }
}
