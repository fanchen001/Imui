package com.fanchen.banner;

import android.content.Context;
import android.widget.Scroller;

/**
 * description：
 */
public class BannerScroller extends Scroller {

    //值越大，滑动越慢
    private int mDuration = 800;

    BannerScroller(Context context, int duration) {
        super(context);
        mDuration = duration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}
