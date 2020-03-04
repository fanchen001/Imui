package com.fanchen.filepicker.util;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * UiUtils
 */
public class UiUtils {

    public static void setViewPadding(final View view){
        if(view == null) return ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.post(new Runnable() {

                @Override
                public void run() {
                    int statusBarHeight = getStatusBarHeight(view.getContext());
                    view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + statusBarHeight,view.getPaddingRight(), view.getPaddingBottom());
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    if (layoutParams != null) {
                        layoutParams.height = view.getMeasuredHeight() + statusBarHeight;
                        view.setLayoutParams(layoutParams);
                    }
                }

            });
        }
    }

    /**
     * 获取状态栏高度
     *
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /***
     * DP 转 PX
     * @param c
     * @param dipValue
     * @return
     */
    public static int dpToPx(Context c, float dipValue) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static int getImageResize(Context context,RecyclerView recyclerView) {
        int mImageResize;
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        int spanCount = ((GridLayoutManager) lm).getSpanCount();
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mImageResize = screenWidth / spanCount;
        return mImageResize;
    }

}
