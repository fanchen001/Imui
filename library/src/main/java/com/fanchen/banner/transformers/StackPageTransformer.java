package com.fanchen.banner.transformers;

import android.view.View;

/**
 * Created by jxnk25 on 2016/10/18.
 * <p>
 *
 *
 *
 * description：
 */
public class StackPageTransformer extends BasePageTransformer {

    @Override
    public void handleInvisiblePage(View view, float position) {
    }

    @Override
    public void handleLeftPage(View view, float position) {
    }

    @Override
    public void handleRightPage(View view, float position) {
        view.setTranslationX(-view.getWidth() * position);
    }

}