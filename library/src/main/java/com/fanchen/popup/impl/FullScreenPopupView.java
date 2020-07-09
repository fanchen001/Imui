package com.fanchen.popup.impl;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.fanchen.popup.Popup;
import com.fanchen.popup.animator.PopupAnimator;
import com.fanchen.popup.animator.TranslateAnimator;
import com.fanchen.popup.core.CenterPopupView;
import com.fanchen.popup.enums.PopupAnimation;
import com.fanchen.popup.util.PopupUtils;

/**
 * Description: 宽高撑满的全屏弹窗
 * Create by lxj, at 2019/2/1
 */
public class FullScreenPopupView extends CenterPopupView {
    public ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    public FullScreenPopupView(@NonNull Context context) {
        super(context);
    }
    @Override
    protected int getMaxWidth() {
        return 0;
    }

    @Override
    protected void initPopupContent() {
        super.initPopupContent();
        popupInfo.hasShadowBg = false;
    }

    @Override
    public void onNavigationBarChange(boolean show) {
        if(!show){
            applyFull();
            getPopupContentView().setPadding(0,0,0,0);
        }else {
            applySize(true);
        }
    }

    @Override
    protected void applySize(boolean isShowNavBar) {
        int rotation = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        View contentView = getPopupContentView();
        LayoutParams params = (LayoutParams) contentView.getLayoutParams();
        params.gravity = Gravity.TOP;
        contentView.setLayoutParams(params);

        int actualNabBarHeight = isShowNavBar|| PopupUtils.isNavBarVisible(dialog.getWindow()) ? PopupUtils.getNavBarHeight() : 0;
        if (rotation == 0) {
            contentView.setPadding(contentView.getPaddingLeft(), contentView.getPaddingTop(), contentView.getPaddingRight(),
                    0);
        } else if (rotation == 1 || rotation == 3) {
            contentView.setPadding(contentView.getPaddingLeft(), contentView.getPaddingTop(), contentView.getPaddingRight(), 0);
        }
    }

    Paint paint = new Paint();
    Rect shadowRect;

    int currColor = Color.TRANSPARENT;
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (popupInfo.hasStatusBarShadow) {
            paint.setColor(currColor);
            shadowRect = new Rect(0, 0, PopupUtils.getWindowWidth(getContext()), PopupUtils.getStatusBarHeight());
            canvas.drawRect(shadowRect, paint);
        }
    }

    @Override
    protected void doShowAnimation() {
        super.doShowAnimation();
        doStatusBarColorTransform(true);
    }

    @Override
    protected void doDismissAnimation() {
        super.doDismissAnimation();
        doStatusBarColorTransform(false);
    }

    public void doStatusBarColorTransform(boolean isShow){
        if (popupInfo.hasStatusBarShadow) {
            //状态栏渐变动画
            ValueAnimator animator = ValueAnimator.ofObject(argbEvaluator,
                    isShow ? Color.TRANSPARENT : Popup.statusBarShadowColor,
                    isShow ? Popup.statusBarShadowColor : Color.TRANSPARENT);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    currColor = (Integer) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            animator.setDuration(Popup.getAnimationDuration()).start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        paint = null;
    }

    @Override
    protected PopupAnimator getPopupAnimator() {
        return new TranslateAnimator(getPopupContentView(), PopupAnimation.TranslateFromBottom);
    }
}
