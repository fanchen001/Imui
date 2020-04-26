package com.fanchen.picture.view.listener;

import android.graphics.Bitmap;
import android.view.View;

import com.fanchen.picture.bean.IImageInfo;

/**
 * description:
 */
public interface OnBigImageLongClickListener {

    /**
     * 长按事件
     * @param view
     * @param position
     */
    boolean onLongClick(View view, int position, Bitmap bitmap, IImageInfo imageInfo);
}