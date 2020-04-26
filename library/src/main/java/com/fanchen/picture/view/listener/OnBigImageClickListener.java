package com.fanchen.picture.view.listener;

import android.view.View;

import com.fanchen.picture.bean.IImageInfo;


/**
 * description:
 */
public interface OnBigImageClickListener {

    /**
     * 点击事件
     * @param view
     * @param position
     */
    void onClick(View view, int position, IImageInfo imageInfo);
}