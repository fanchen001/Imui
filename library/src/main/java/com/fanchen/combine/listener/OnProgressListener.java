package com.fanchen.combine.listener;

import android.graphics.Bitmap;

public interface OnProgressListener {

    void onStart();

    void onComplete(Bitmap bitmap);

}
