package com.fanchen.location.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.baidu.mapapi.map.TextureMapView;

public class FuckBaiduView extends FrameLayout {

    public FuckBaiduView(Context context) {
        this(context, null);
    }

    public FuckBaiduView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FuckBaiduView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TextureMapView textureMapView = new TextureMapView(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(textureMapView, layoutParams);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);//请求父控件不拦截触摸事件
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return super.dispatchTouchEvent(ev);
    }

    public TextureMapView getMapView() {
        return (TextureMapView) getChildAt(0);
    }

}
