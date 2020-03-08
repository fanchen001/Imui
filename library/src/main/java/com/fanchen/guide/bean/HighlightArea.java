package com.fanchen.guide.bean;

import android.graphics.RectF;
import android.view.View;

import com.fanchen.guide.support.HShape;

/**
 * 高亮区域显示
 */
public class HighlightArea {

    public View mHightlightView;

    @HShape
    public int mShape;

    public HighlightArea(View view, @HShape int shape) {
        this.mHightlightView = view;
        this.mShape = shape;
    }

    public RectF getRectF() {
        RectF rectF = new RectF();
        if (mHightlightView != null) {
            int[] location = new int[2];
            mHightlightView.getLocationOnScreen(location);
            rectF.left = location[0];
            rectF.top = location[1];
            rectF.right = location[0] + mHightlightView.getWidth();
            rectF.bottom = location[1] + mHightlightView.getHeight();
        }
        return rectF;
    }
}
