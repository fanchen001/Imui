package com.fanchen.banner.transformers;

import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Created by jxnk25 on 2016/10/18.
 * <p>
 *
 *
 *
 * description：AlphaPageTransformer
 */
public class AlphaPageTransformer extends BasePageTransformer {
    private float mMinScale = 0.4f;

    public AlphaPageTransformer() {
    }

    public AlphaPageTransformer(float minScale) {
        setMinScale(minScale);
    }

    @Override
    public void handleInvisiblePage(View view, float position) {
        ViewCompat.setAlpha(view, 0);
    }

    @Override
    public void handleLeftPage(View view, float position) {
        view.setAlpha(mMinScale + (1 - mMinScale) * (1 + position));
    }

    @Override
    public void handleRightPage(View view, float position) {
        view.setAlpha(mMinScale + (1 - mMinScale) * (1 - position));
    }

    public void setMinScale(float minScale) {
        if (minScale >= 0.0f && minScale <= 1.0f) {
            mMinScale = minScale;
        }
    }
}