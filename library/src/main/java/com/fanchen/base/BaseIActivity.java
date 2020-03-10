package com.fanchen.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.jude.swipbackhelper.SwipeBackPage;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BaseIActivity extends AppCompatActivity {

    private SwipeBackPage mBackPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            Log.e("BaseIActivity", "onCreate fixOrientation when Oreo, result = " + fuckAndroidOCrash());
        }else{
        }
        super.onCreate(savedInstanceState);
        SwipeBackHelper.onCreate(this);
        mBackPage = SwipeBackHelper.getCurrentPage(this);// 获取当前页面
        mBackPage.setSwipeBackEnable(isSwipeActivity());// 设置是否可滑动
        mBackPage.setSwipeEdgePercent(getEdgePercent());// 可滑动的范围。百分比。0.2表示为左边20%的屏幕
        mBackPage.setSwipeSensitivity(getSensitivity());// 对横向滑动手势的敏感程度。0为迟钝 1为敏感
        mBackPage.setClosePercent(getClosePercent());// 触发关闭Activity百分比
        mBackPage.setSwipeRelateEnable(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);// 是否与下一级activity联动(微信效果)仅限5.0以上机器
        mBackPage.setDisallowInterceptTouchEvent(false);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // 使用滑动关闭功能
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SwipeBackHelper.onDestroy(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 防止调用onStop后使用fragment出现
        // Can not perform this action after onSaveInstanceState 异常
        onNewIntent(new Intent());
    }

    @Override
    protected void onSaveInstanceState(Bundle arg0) {
        super.onSaveInstanceState(arg0);
        // 防止调用onSaveInstanceState后使用fragment出现
        // Can not perform this action after onSaveInstanceState 异常
        onNewIntent(new Intent());
    }

    /**
     * 默认是否可滑动返回
     *
     * @return
     */
    protected boolean isSwipeActivity() {
        return true;
    }

    /**
     * 设置是否可滑动返回
     */
    public void setBackEnable(boolean enable) {
        if (mBackPage != null) {
            mBackPage.setSwipeBackEnable(enable);
        }
    }

    /**
     * 可滑动的范围。百分比。0.2表示为左边20%的屏幕
     *
     * @return
     */
    protected float getEdgePercent() {
        return 0.15f;
    }

    /**
     * 对横向滑动手势的敏感程度。0为迟钝 1为敏感
     *
     * @return
     */
    protected float getSensitivity() {
        return 0.45f;
    }

    /**
     * 触发关闭Activity百分比
     *
     * @return
     */
    protected float getClosePercent() {
        return 0.4f;
    }

    private boolean fuckAndroidOCrash() {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo) field.get(this);
            o.screenOrientation = -1;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressLint("PrivateApi")
    private boolean isTranslucentOrFloating() {
        boolean isTranslucentOrFloating = false;
        try {
            int[] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean) m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            return;
        }
        super.setRequestedOrientation(requestedOrientation);
    }

}
