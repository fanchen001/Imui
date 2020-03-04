package com.fanchen.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jude.swipbackhelper.SwipeBackHelper;
import com.jude.swipbackhelper.SwipeBackPage;

public class BaseIActivity extends AppCompatActivity {

    private SwipeBackPage mBackPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
     * 是否使用eventbus
     *
     * @return
     */
    protected boolean isRegisterEventBus() {
        return false;
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
}
