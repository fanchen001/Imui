package com.fanchen.location.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ItemDecorntion extends RecyclerView.ItemDecoration {
    private int top,right,left,bottom;

    public ItemDecorntion(int left, int top, int right, int bottom) {
        this.top = top;
        this.right = right;
        this.left = left;
        this.bottom = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(left,top,right,bottom);
    }
}
