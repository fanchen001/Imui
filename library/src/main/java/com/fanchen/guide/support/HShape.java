package com.fanchen.guide.support;

import android.support.annotation.IntDef;

/**
 * 高亮区域形状
 *
 */
@IntDef({
        HShape.CIRCLE,
        HShape.RECTANGLE,
        HShape.OVAL
})
public @interface HShape {

    int CIRCLE = 0;

    int RECTANGLE = 1;

    int OVAL = 2;
}
