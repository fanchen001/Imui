package com.fanchen.popup.impl;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.fanchen.popup.animator.PopupAnimator;
import com.fanchen.popup.animator.TranslateAnimator;
import com.fanchen.popup.core.AttachPopupView;
import com.fanchen.popup.enums.PopupAnimation;
import com.fanchen.popup.enums.PopupPosition;
import com.fanchen.popup.interfaces.OnClickOutsideListener;
import com.fanchen.popup.util.PopupUtils;

/**
 * Description: 局部阴影的弹窗，类似于淘宝商品列表的下拉筛选弹窗
 *  2018/12/21
 */
public abstract class PartShadowPopupView extends AttachPopupView {
    public PartShadowPopupView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        defaultOffsetY = popupInfo.offsetY == 0 ? PopupUtils.dp2px(getContext(), 0) : popupInfo.offsetY;
        defaultOffsetX = popupInfo.offsetX == 0 ? PopupUtils.dp2px(getContext(), 0) : popupInfo.offsetX;

        getPopupImplView().setTranslationX(popupInfo.offsetX);
        getPopupImplView().setTranslationY(popupInfo.offsetY);

        // 指定阴影动画的目标View
        if(popupInfo.hasShadowBg){
            shadowBgAnimator.targetView = getPopupContentView();
        }
    }

    @Override
    protected void applyBg() {
    }

    @Override
    public void onNavigationBarChange(boolean show) {
        super.onNavigationBarChange(show);
        if(!show){
            LayoutParams params = (LayoutParams)getPopupContentView().getLayoutParams();
            params.height = PopupUtils.getWindowHeight(getContext());
            getPopupContentView().setLayoutParams(params);
        }
    }

    @Override
    protected void doAttach() {
        if (popupInfo.getAtView() == null)
            throw new IllegalArgumentException("atView must not be null for PartShadowPopupView！");

        //1. apply width and height
        MarginLayoutParams params = (MarginLayoutParams) getPopupContentView().getLayoutParams();
        params.width = getMeasuredWidth();

        //水平居中
        if(popupInfo.isCenterHorizontal && getPopupImplView()!=null){
            getPopupImplView().setTranslationX(PopupUtils.getWindowWidth(getContext())/2f - getPopupContentView().getMeasuredWidth()/2f);
        }

        //1. 获取atView在屏幕上的位置
        int[] locations = new int[2];
        popupInfo.getAtView().getLocationOnScreen(locations);
        Rect rect = new Rect(locations[0], locations[1], locations[0] + popupInfo.getAtView().getMeasuredWidth(),
                locations[1] + popupInfo.getAtView().getMeasuredHeight());
        int centerY = rect.top + rect.height()/2;
        if((centerY > getMeasuredHeight()/2 || popupInfo.popupPosition== PopupPosition.Top) && popupInfo.popupPosition!=PopupPosition.Bottom){
            // 说明atView在Window下半部分，PartShadow应该显示在它上方，计算atView之上的高度
            params.height = rect.top;
            isShowUp = true;
            params.topMargin = -defaultOffsetY;
            // 同时自定义的impl View应该Gravity居于底部
            View implView = ((ViewGroup)getPopupContentView()).getChildAt(0);
            LayoutParams implParams = (LayoutParams) implView.getLayoutParams();
            implParams.gravity = Gravity.BOTTOM;
            if(getMaxHeight()!=0)
                implParams.height = Math.min(implView.getMeasuredHeight(), getMaxHeight());
            implView.setLayoutParams(implParams);

        } else {
            // atView在上半部分，PartShadow应该显示在它下方，计算atView之下的高度
            params.height = getMeasuredHeight() - rect.bottom;

            isShowUp = false;
            params.topMargin = rect.bottom + defaultOffsetY;

            // 同时自定义的impl View应该Gravity居于顶部
            View implView = ((ViewGroup)getPopupContentView()).getChildAt(0);
            LayoutParams implParams = (LayoutParams) implView.getLayoutParams();
            implParams.gravity = Gravity.TOP;
            if(getMaxHeight()!=0)
                implParams.height = Math.min(implView.getMeasuredHeight(), getMaxHeight());
            implView.setLayoutParams(implParams);
        }
        getPopupContentView().setLayoutParams(params);

        attachPopupContainer.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(popupInfo.isDismissOnTouchOutside)dismiss();
                return false;
            }
        });
        attachPopupContainer.setOnClickOutsideListener(new OnClickOutsideListener() {
            @Override
            public void onClickOutside() {
                if(popupInfo.isDismissOnTouchOutside)dismiss();
            }
        });
    }

    //让触摸透过
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(popupInfo.isDismissOnTouchOutside){
            dismiss();
        }
        if(dialog!=null && popupInfo.isClickThrough)dialog.passClick(event);
        return popupInfo.isClickThrough;
    }

    @Override
    protected PopupAnimator getPopupAnimator() {
        return new TranslateAnimator(getPopupImplView(), isShowUp ?
                PopupAnimation.TranslateFromBottom: PopupAnimation.TranslateFromTop);
    }

}
