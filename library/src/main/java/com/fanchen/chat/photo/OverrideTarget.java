package com.fanchen.chat.photo;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.bumptech.glide.request.target.ImageViewTarget;
import com.fanchen.chat.ChatInputView;

public class OverrideTarget extends ImageViewTarget<Bitmap> {

    private ImageView target;
    private boolean parentOverride;

    public OverrideTarget(ImageView target, boolean parentOverride) {
        super(target);
        this.target = target;
        this.parentOverride = parentOverride;
    }

    @Override
    protected void setResource(Bitmap resource) {
        view.setImageBitmap(resource);
        int width = resource.getWidth();
        int height = resource.getHeight();
        float zoom = (float) height / (float) ChatInputView.sMenuHeight;
        int containerWidth = (int) (width / zoom);
        if (parentOverride) {
            ViewParent viewParent = target.getParent();
            ViewParent parent = viewParent;
            while (viewParent != null) {
                viewParent = viewParent.getParent();
                if (viewParent != null) {
                    parent = viewParent;
                }
            }
            if (parent instanceof View) {
                View v = (View) parent;
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.width = containerWidth;
                    v.setLayoutParams(layoutParams);
                }
            }
        } else {
            ViewGroup.LayoutParams params = target.getLayoutParams();
            if (params != null) {
                params.width = containerWidth;
                target.setLayoutParams(params);
            }
        }
    }
}
