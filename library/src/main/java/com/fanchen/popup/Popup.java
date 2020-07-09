package com.fanchen.popup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.View;


import com.fanchen.popup.animator.PopupAnimator;
import com.fanchen.popup.core.AttachPopupView;
import com.fanchen.popup.core.BasePopupView;
import com.fanchen.popup.core.BottomPopupView;
import com.fanchen.popup.core.CenterPopupView;
import com.fanchen.popup.core.PopupInfo;
import com.fanchen.popup.core.PositionPopupView;
import com.fanchen.popup.enums.PopupAnimation;
import com.fanchen.popup.enums.PopupPosition;
import com.fanchen.popup.enums.PopupType;
import com.fanchen.popup.impl.AttachListPopupView;
import com.fanchen.popup.impl.BottomListPopupView;
import com.fanchen.popup.impl.CenterListPopupView;
import com.fanchen.popup.impl.ConfirmPopupView;
import com.fanchen.popup.impl.InputConfirmPopupView;
import com.fanchen.popup.impl.LoadingPopupView;
import com.fanchen.popup.interfaces.OnCancelListener;
import com.fanchen.popup.interfaces.OnConfirmListener;
import com.fanchen.popup.interfaces.OnInputConfirmListener;
import com.fanchen.popup.interfaces.OnSelectListener;
import com.fanchen.popup.interfaces.XPopupCallback;
import com.fanchen.popup.util.XPermission;



public class Popup {
    private Popup() {
    }

    /**
     * 全局弹窗的设置
     **/
    private static int primaryColor = Color.parseColor("#121212");
    private static int animationDuration = 350;
    public static int statusBarShadowColor = Color.parseColor("#55000000");
    private static int shadowBgColor = Color.parseColor("#9F000000");

    public static void setShadowBgColor(int color) {
        shadowBgColor = color;
    }

    public static int getShadowBgColor() {
        return shadowBgColor;
    }

    /**
     * 设置主色调
     *
     * @param color
     */
    public static void setPrimaryColor(int color) {
        primaryColor = color;
    }

    public static int getPrimaryColor() {
        return primaryColor;
    }

    public static void setAnimationDuration(int duration) {
        if (duration >= 0) {
            animationDuration = duration;
        }
    }

    public static int getAnimationDuration() {
        return animationDuration;
    }

    public static class Builder {
        private final PopupInfo popupInfo = new PopupInfo();
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder popupType(PopupType popupType) {
            this.popupInfo.popupType = popupType;
            return this;
        }

        /**
         * 设置按下返回键是否关闭弹窗，默认为true
         *
         * @param isDismissOnBackPressed
         * @return
         */
        public Builder dismissOnBackPressed(Boolean isDismissOnBackPressed) {
            this.popupInfo.isDismissOnBackPressed = isDismissOnBackPressed;
            return this;
        }

        /**
         * 设置点击弹窗外面是否关闭弹窗，默认为true
         *
         * @param isDismissOnTouchOutside
         * @return
         */
        public Builder dismissOnTouchOutside(Boolean isDismissOnTouchOutside) {
            this.popupInfo.isDismissOnTouchOutside = isDismissOnTouchOutside;
            return this;
        }

        /**
         * 设置当操作完毕后是否自动关闭弹窗，默认为true。比如：点击Confirm弹窗的确认按钮默认是关闭弹窗，如果为false，则不关闭
         *
         * @param autoDismiss
         * @return
         */
        public Builder autoDismiss(Boolean autoDismiss) {
            this.popupInfo.autoDismiss = autoDismiss;
            return this;
        }

        /**
         * 弹窗是否有半透明背景遮罩，默认是true
         *
         * @param hasShadowBg
         * @return
         */
        public Builder hasShadowBg(Boolean hasShadowBg) {
            this.popupInfo.hasShadowBg = hasShadowBg;
            return this;
        }
        /**
         * 是否设置背景为高斯模糊背景。默认为false
         * @param hasBlurBg
         * @return
         */
        public Builder hasBlurBg(boolean hasBlurBg) {
            this.popupInfo.hasBlurBg = hasBlurBg;
            return this;
        }

        /**
         * 设置弹窗依附的View，Attach弹窗必须设置这个
         *
         * @param atView
         * @return
         */
        public Builder atView(View atView) {
            this.popupInfo.atView = atView;
            return this;
        }

        /**
         * 设置弹窗监视的View
         *
         * @param watchView
         * @return
         */
        public Builder watchView(View watchView) {
            this.popupInfo.watchView = watchView;
            this.popupInfo.watchView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (popupInfo.touchPoint == null || event.getAction() == MotionEvent.ACTION_DOWN)
                        popupInfo.touchPoint = new PointF(event.getRawX(), event.getRawY());
                    return false;
                }
            });
            return this;
        }

        /**
         * 为弹窗设置内置的动画器，默认情况下，已经为每种弹窗设置了效果最佳的动画器；如果你不喜欢，仍然可以修改。
         *
         * @param popupAnimation
         * @return
         */
        public Builder popupAnimation(PopupAnimation popupAnimation) {
            this.popupInfo.popupAnimation = popupAnimation;
            return this;
        }

        /**
         * 自定义弹窗动画器
         *
         * @param customAnimator
         * @return
         */
        public Builder customAnimator(PopupAnimator customAnimator) {
            this.popupInfo.customAnimator = customAnimator;
            return this;
        }

        /**
         * 设置最大宽度，如果重写了弹窗的getMaxWidth，则以重写的为准
         *
         * @param maxWidth
         * @return
         */
        public Builder maxWidth(int maxWidth) {
            this.popupInfo.maxWidth = maxWidth;
            return this;
        }

        /**
         * 设置最大高度，如果重写了弹窗的getMaxHeight，则以重写的为准
         *
         * @param maxHeight
         * @return
         */
        public Builder maxHeight(int maxHeight) {
            this.popupInfo.maxHeight = maxHeight;
            return this;
        }

        /**
         * 是否自动打开输入法，当弹窗包含输入框时很有用，默认为false
         *
         * @param autoOpenSoftInput
         * @return
         */
        public Builder autoOpenSoftInput(Boolean autoOpenSoftInput) {
            this.popupInfo.autoOpenSoftInput = autoOpenSoftInput;
            return this;
        }

        /**
         * 当弹出输入法时，弹窗是否要移动到输入法之上，默认为true。如果不移动，弹窗很有可能被输入法盖住
         *
         * @param isMoveUpToKeyboard
         * @return
         */
        public Builder moveUpToKeyboard(Boolean isMoveUpToKeyboard) {
            this.popupInfo.isMoveUpToKeyboard = isMoveUpToKeyboard;
            return this;
        }

        /**
         * 设置弹窗出现在目标的什么位置，有四种取值：Left，Right，Top，Bottom。这种手动设置位置的行为
         * 只对Attach弹窗和Drawer弹窗生效。
         *
         * @param popupPosition
         * @return
         */
        public Builder popupPosition(PopupPosition popupPosition) {
            this.popupInfo.popupPosition = popupPosition;
            return this;
        }

        /**
         * 设置是否给StatusBar添加阴影，目前对Drawer弹窗生效。如果你的Drawer的背景是白色，建议设置为true，因为状态栏文字的颜色也往往
         * 是白色，会导致状态栏文字看不清；如果Drawer的背景色不是白色，则忽略即可
         *
         * @param hasStatusBarShadow
         * @return
         */
        public Builder hasStatusBarShadow(boolean hasStatusBarShadow) {
            this.popupInfo.hasStatusBarShadow = hasStatusBarShadow;
            return this;
        }

        /**
         * 设置是否显示状态栏，默认是显示的
         *
         * @param hasStatusBar
         * @return
         */
        public Builder hasStatusBar(boolean hasStatusBar) {
            this.popupInfo.hasStatusBar = hasStatusBar;
            return this;
        }

        /**
         * 设置是否显示导航栏，默认是显示的
         *
         * @param hasNavigationBar
         * @return
         */
        public Builder hasNavigationBar(boolean hasNavigationBar) {
            this.popupInfo.hasNavigationBar = hasNavigationBar;
            return this;
        }

        /**
         * 弹窗在x方向的偏移量，对所有弹窗生效，单位是px
         *
         * @param offsetX
         * @return
         */
        public Builder offsetX(int offsetX) {
            this.popupInfo.offsetX = offsetX;
            return this;
        }

        /**
         * 弹窗在y方向的偏移量，对所有弹窗生效，单位是px
         *
         * @param offsetY
         * @return
         */
        public Builder offsetY(int offsetY) {
            this.popupInfo.offsetY = offsetY;
            return this;
        }

        /**
         * 是否启用拖拽，比如：Bottom弹窗默认是带手势拖拽效果的，如果禁用则不能拖拽
         *
         * @param enableDrag
         * @return
         */
        public Builder enableDrag(boolean enableDrag) {
            this.popupInfo.enableDrag = enableDrag;
            return this;
        }

        /**
         * 是否水平居中，默认情况下Attach弹窗依靠着目标的左边或者右边，如果isCenterHorizontal为true，则与目标水平居中对齐
         *
         * @param isCenterHorizontal
         * @return
         */
        public Builder isCenterHorizontal(boolean isCenterHorizontal) {
            this.popupInfo.isCenterHorizontal = isCenterHorizontal;
            return this;
        }

        /**
         * 是否抢占焦点，默认情况下弹窗会抢占焦点，目的是为了能处理返回按键事件。如果为false，则不在抢焦点，但也无法响应返回按键了
         *
         * @param isRequestFocus 默认为true
         * @return
         */
        public Builder isRequestFocus(boolean isRequestFocus) {
            this.popupInfo.isRequestFocus = isRequestFocus;
            return this;
        }

        /**
         * 是否让弹窗内的输入框自动获取焦点，默认是true。弹窗内有输入法的情况下该设置才有效
         *
         * @param autoFocusEditText
         * @return
         */
        public Builder autoFocusEditText(boolean autoFocusEditText) {
            this.popupInfo.autoFocusEditText = autoFocusEditText;
            return this;
        }

        /**
         * 是否让使用暗色主题，默认是false。
         *
         * @param isDarkTheme
         * @return
         */
        public Builder isDarkTheme(boolean isDarkTheme) {
            this.popupInfo.isDarkTheme = isDarkTheme;
            return this;
        }

        /**
         * 是否点击弹窗背景时将点击事件透传到Activity下，默认是不透传。由于容易引发其他问题，目前只对PartShadow弹窗有效。
         *
         * @param isClickThrough
         * @return
         */
        public Builder isClickThrough(boolean isClickThrough) {
            this.popupInfo.isClickThrough = isClickThrough;
            return this;
        }

        /**
         * 是否允许应用在后台的时候也能弹出弹窗，默认是false。注意如果开启这个开关，需要申请悬浮窗权限才能生效。
         *
         * @param enableShowWhenAppBackground
         * @return
         */
        public Builder enableShowWhenAppBackground(boolean enableShowWhenAppBackground) {
            this.popupInfo.enableShowWhenAppBackground = enableShowWhenAppBackground;
            return this;
        }

        /**
         * 是否开启三阶拖拽效果，想高德地图上面的弹窗那样可以拖拽的效果
         *
         * @param isThreeDrag
         * @return
         */
        public Builder isThreeDrag(boolean isThreeDrag) {
            this.popupInfo.isThreeDrag = isThreeDrag;
            return this;
        }

        /**
         * 是否在弹窗消失后就立即释放资源，杜绝内存泄漏，仅仅适用于弹窗只用一次的场景，默认为false。
         * 如果你的弹窗对象需要用多次，千万不要开启这个设置
         * @param isDestroyOnDismiss
         * @return
         */
        public Builder isDestroyOnDismiss(boolean isDestroyOnDismiss) {
            this.popupInfo.isDestroyOnDismiss = isDestroyOnDismiss;
            return this;
        }

        /**
         * 设置弹窗显示和隐藏的回调监听
         *
         * @param xPopupCallback
         * @return
         */
        public Builder setPopupCallback(XPopupCallback xPopupCallback) {
            this.popupInfo.xPopupCallback = xPopupCallback;
            return this;
        }

        /****************************************** 便捷方法 ****************************************/
        /**
         * 显示确认和取消对话框
         *
         * @param title           对话框标题，传空串会隐藏标题
         * @param content         对话框内容
         * @param cancelBtnText   取消按钮的文字内容
         * @param confirmBtnText  确认按钮的文字内容
         * @param confirmListener 点击确认的监听器
         * @param cancelListener  点击取消的监听器
         * @param isHideCancel    是否隐藏取消按钮
         * @return
         */
        public ConfirmPopupView asConfirm(CharSequence title, CharSequence content, CharSequence cancelBtnText, CharSequence confirmBtnText, OnConfirmListener confirmListener, OnCancelListener cancelListener, boolean isHideCancel) {
            popupType(PopupType.Center);
            ConfirmPopupView popupView = new ConfirmPopupView(this.context);
            popupView.setTitleContent(title, content, null);
            popupView.setCancelText(cancelBtnText);
            popupView.setConfirmText(confirmBtnText);
            popupView.setListener(confirmListener, cancelListener);
            if (isHideCancel) popupView.hideCancelBtn();
            popupView.popupInfo = this.popupInfo;
            return popupView;
        }

        public ConfirmPopupView asConfirm(CharSequence title, CharSequence content, OnConfirmListener confirmListener, OnCancelListener cancelListener) {
            return asConfirm(title, content, null, null, confirmListener, cancelListener, false);
        }

        public ConfirmPopupView asConfirm(CharSequence title, CharSequence content, OnConfirmListener confirmListener) {
            return asConfirm(title, content, null, null, confirmListener, null, false);
        }

        /**
         * 显示带有输入框，确认和取消对话框
         *
         * @param title           对话框标题，传空串会隐藏标题
         * @param content         对话框内容,，传空串会隐藏
         * @param inputContent    输入框文字内容，会覆盖hint
         * @param hint            输入框默认文字
         * @param confirmListener 点击确认的监听器
         * @param cancelListener  点击取消的监听器
         * @return
         */
        public InputConfirmPopupView asInputConfirm(CharSequence title, CharSequence content, CharSequence inputContent, CharSequence hint, OnInputConfirmListener confirmListener, OnCancelListener cancelListener) {
            popupType(PopupType.Center);
            InputConfirmPopupView popupView = new InputConfirmPopupView(this.context);
            popupView.setTitleContent(title, content, hint);
            popupView.inputContent = inputContent;
            popupView.setListener(confirmListener, cancelListener);
            popupView.popupInfo = this.popupInfo;
            return popupView;
        }

        public InputConfirmPopupView asInputConfirm(CharSequence title, CharSequence content, CharSequence inputContent, CharSequence hint, OnInputConfirmListener confirmListener) {
            return asInputConfirm(title, content, inputContent, hint, confirmListener, null);
        }

        public InputConfirmPopupView asInputConfirm(CharSequence title, CharSequence content, CharSequence hint, OnInputConfirmListener confirmListener) {
            return asInputConfirm(title, content, null, hint, confirmListener, null);
        }

        public InputConfirmPopupView asInputConfirm(CharSequence title, CharSequence content, OnInputConfirmListener confirmListener) {
            return asInputConfirm(title, content, null, null, confirmListener, null);
        }

        /**
         * 显示在中间的列表Popup
         *
         * @param title          标题，可以不传，不传则不显示
         * @param data           显示的文本数据
         * @param iconIds        图标的id数组，可以没有
         * @param selectListener 选中条目的监听器
         * @return
         */
        public CenterListPopupView asCenterList(CharSequence title, String[] data, int[] iconIds, int checkedPosition, OnSelectListener selectListener) {
            popupType(PopupType.Center);
            CenterListPopupView popupView = new CenterListPopupView(this.context)
                    .setStringData(title, data, iconIds)
                    .setCheckedPosition(checkedPosition)
                    .setOnSelectListener(selectListener);
            popupView.popupInfo = this.popupInfo;
            return popupView;
        }

        public CenterListPopupView asCenterList(CharSequence title, String[] data, OnSelectListener selectListener) {
            return asCenterList(title, data, null, -1, selectListener);
        }

        public CenterListPopupView asCenterList(CharSequence title, String[] data, int[] iconIds, OnSelectListener selectListener) {
            return asCenterList(title, data, iconIds, -1, selectListener);
        }

        /**
         * 显示在中间加载的弹窗
         *
         * @return
         */
        public LoadingPopupView asLoading(CharSequence title) {
            popupType(PopupType.Center);
            LoadingPopupView popupView = new LoadingPopupView(this.context)
                    .setTitle(title);
            this.popupInfo.isDismissOnTouchOutside = false; //Loading弹窗点击外部不消失
            popupView.popupInfo = this.popupInfo;
            return popupView;
        }

        public LoadingPopupView asLoading() {
            return asLoading(null);
        }

        /**
         * 显示在底部的列表Popup
         *
         * @param title           标题，可以不传，不传则不显示
         * @param data            显示的文本数据
         * @param iconIds         图标的id数组，可以没有
         * @param checkedPosition 选中的位置，传-1为不选中
         * @param selectListener  选中条目的监听器
         * @return
         */
        public BottomListPopupView asBottomList(CharSequence title, String[] data, int[] iconIds, int checkedPosition, boolean enableDrag, OnSelectListener selectListener) {
            popupType(PopupType.Bottom);
            BottomListPopupView popupView = new BottomListPopupView(this.context)
                    .setStringData(title, data, iconIds)
                    .setCheckedPosition(checkedPosition)
                    .setOnSelectListener(selectListener);
            popupView.popupInfo = this.popupInfo;
            return popupView;
        }

        public BottomListPopupView asBottomList(CharSequence title, String[] data, OnSelectListener selectListener) {
            return asBottomList(title, data, null, -1, true, selectListener);
        }

        public BottomListPopupView asBottomList(CharSequence title, String[] data, int[] iconIds, OnSelectListener selectListener) {
            return asBottomList(title, data, iconIds, -1, true, selectListener);
        }

        public BottomListPopupView asBottomList(CharSequence title, String[] data, int[] iconIds, int checkedPosition, OnSelectListener selectListener) {
            return asBottomList(title, data, iconIds, checkedPosition, true, selectListener);
        }

        public BottomListPopupView asBottomList(CharSequence title, String[] data, int[] iconIds, boolean enableDrag, OnSelectListener selectListener) {
            return asBottomList(title, data, iconIds, -1, enableDrag, selectListener);
        }


        /**
         * 显示依附于某View的列表，必须调用atView()方法，指定依附的View
         *
         * @param data           显示的文本数据
         * @param iconIds        图标的id数组，可以为null
         * @param selectListener 选中条目的监听器
         * @return
         */
        public AttachListPopupView asAttachList(String[] data, int[] iconIds, OnSelectListener selectListener) {
            popupType(PopupType.AttachView);
            AttachListPopupView popupView = new AttachListPopupView(this.context)
                    .setStringData(data, iconIds)
                    .setOnSelectListener(selectListener);
            popupView.popupInfo = this.popupInfo;
            return popupView;
        }

        public BasePopupView asCustom(BasePopupView popupView) {
            if (popupView instanceof CenterPopupView) {
                popupType(PopupType.Center);
            } else if (popupView instanceof BottomPopupView) {
                popupType(PopupType.Bottom);
            } else if (popupView instanceof AttachPopupView) {
                popupType(PopupType.AttachView);
            } /*else if (popupView instanceof ImageViewerPopupView) {
                popupType(PopupType.ImageViewer);
            } */else if (popupView instanceof PositionPopupView) {
                popupType(PopupType.Position);
            }
            popupView.popupInfo = this.popupInfo;
            return popupView;
        }

    }

    /**
     * 跳转申请悬浮窗权限界面
     * @param context
     * @param callback
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestOverlayPermission(Context context, XPermission.SimpleCallback callback){
        XPermission.create(context).requestDrawOverlays(callback);
    }
}
