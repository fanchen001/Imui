package com.fanchen.banner.transformers;

import android.view.View;

/**
 * Created by jxnk25 on 2016/10/18.
 * <p>
 *
 *
 *
 * description：AccordionPageTransformer
 */
public class AccordionPageTransformer extends BasePageTransformer {

    @Override
    public void handleInvisiblePage(View view, float position) {
    }

    @Override
    public void handleLeftPage(View view, float position) {
        view.setPivotX(view.getWidth());
        view.setScaleX(1.0f + position);
    }

    @Override
    public void handleRightPage(View view, float position) {
        view.setPivotX(0);
        view.setScaleX(1.0f - position);
        view.setAlpha(1);
    }

}