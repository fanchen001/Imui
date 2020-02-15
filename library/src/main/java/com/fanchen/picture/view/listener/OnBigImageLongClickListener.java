package com.fanchen.picture.view.listener;

import android.view.View;

/**
 * description:
 */
public interface OnBigImageLongClickListener {

    /**
     * 长按事件
     * @param view
     * @param position
     */
    boolean onLongClick(View view, int position);
}