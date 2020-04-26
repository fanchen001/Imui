package com.fanchen.picture.view.listener;

import android.graphics.Bitmap;
import android.view.View;

import com.fanchen.picture.bean.IImageInfo;

public interface OnPopItemClickListener {

    void onPopItemClick(View v , int position, int itemPosition, Bitmap bitmap, IImageInfo imageInfo);

}
