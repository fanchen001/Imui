package com.fanchen.combine.layout;

import android.graphics.Bitmap;

public interface ILayoutManager {

    Bitmap combineBitmap(int size, int subSize, int gap, int gapColor, Bitmap[] bitmaps);

}
