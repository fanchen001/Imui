//package com.fanchen.chat;
//
//import android.app.Activity;
//import android.graphics.Rect;
//import android.os.Build;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewTreeObserver;
//import android.widget.FrameLayout;
//
//import com.fanchen.filepicker.util.UiUtils;
//
//public class SoftHideKeyBoardUtil {
//    public static void assistActivity(Activity activity) {
//        new SoftHideKeyBoardUtil(activity, true);
//    }
//
//    public static void assistActivity(Activity activity, boolean statusBar) {
//        new SoftHideKeyBoardUtil(activity, statusBar);
//    }
//
//    private View mChildOfContent;
//    private int usableHeightPrevious;
//    private FrameLayout.LayoutParams frameLayoutParams;
//    //为适应华为小米等手机键盘上方出现黑条或不适配
//    private int contentHeight = -100;//获取setContentView本来view的高度
//    private int statusBarHeight;//状态栏高度
//    private int mBottomStatusHeight = 0;
//    private int mScreenHeight;
//    private boolean isVirtualShow = false;
//
//    private SoftHideKeyBoardUtil(final Activity activity, boolean statusBar) {
//        //1､找到Activity的最外层布局控件，它其实是一个DecorView,它所用的控件就是FrameLayout
//        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
//        //2､获取到setContentView放进去的View
//        mChildOfContent = content.getChildAt(0);
//        mScreenHeight = UiUtils.getScreenHeight(activity);
//        if (statusBar) statusBarHeight = UiUtils.getStatusBarHeight(activity);
//        mBottomStatusHeight = UiUtils.getVirtualBarHeigh(activity);
//        //3､给Activity的xml布局设置View树监听，当布局有变化，如键盘弹出或收起时，都会回调此监听
//        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            //4､软键盘弹起会使GlobalLayout发生变化
//            public void onGlobalLayout() {
//                if (contentHeight == -100) {
//                    contentHeight = mChildOfContent.getHeight();//兼容华为等机型
//                    if (contentHeight == mScreenHeight + mBottomStatusHeight) {
//                        Log.e("onGlobalLayout", "说明虚拟按键隐藏");
//                        isVirtualShow = false;
//                    } else {
//                        isVirtualShow = true;
//                        Log.e("onGlobalLayout", "说明虚拟按键显示");
//                    }
//                }
//
//                //5､当前布局发生变化时，对Activity的xml布局进行重绘
//                possiblyResizeChildOfContent();
//            }
//        });
//        //6､获取到Activity的xml布局的放置参数
//        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
//    }
//
//    // 获取界面可用高度，如果软键盘弹起后，Activity的xml布局可用高度需要减去键盘高度
//    private void possiblyResizeChildOfContent() {
//        //1､获取当前界面可用高度，键盘弹起后，当前界面可用布局会减少键盘的高度
//        int usableHeightNow = computeUsableHeight();
//        //2､如果当前可用高度和原始值不一样
//        if (usableHeightNow != usableHeightPrevious) {
//            //3､获取Activity中xml中布局在当前界面显示的高度
//            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
//            //4､Activity中xml布局的高度-当前可用高度
//            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
//            Log.e("onGlobalLayout", "heightDifference -> " + heightDifference);
//            Log.e("onGlobalLayout", "mBottomStatusHeight -> " + mBottomStatusHeight);
//            if (mBottomStatusHeight == heightDifference) {
//                Log.e("onGlobalLayout", "虚拟按键出来了 -> " + heightDifference);
//                if (isVirtualShow) {
//                    frameLayoutParams.height = contentHeight;
//                } else {
//                    frameLayoutParams.height = contentHeight - heightDifference + statusBarHeight;
//                }
//            } else {
//                //5､高度差大于屏幕1/4时，说明键盘弹出
//                if (heightDifference > (usableHeightSansKeyboard / 4)) {
//                    Log.e("onGlobalLayout", "键盘高度变化 -> " + heightDifference);
//                    // 6､键盘弹出了，Activity的xml布局高度应当减去键盘高度
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        frameLayoutParams.height = usableHeightSansKeyboard - heightDifference + statusBarHeight;
//                    } else {
//                        frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
//                    }
//                } else {
//                    if (isVirtualShow) {
//                        frameLayoutParams.height = contentHeight  ;
//                    } else {
//                        frameLayoutParams.height = contentHeight;
//                    }
//
//                    Log.e("onGlobalLayout", "键盘高度 -> " + frameLayoutParams.height);
//                }
//            }
//            //7､ 重绘Activity的xml布局
//            mChildOfContent.requestLayout();
//            usableHeightPrevious = usableHeightNow;
//        }
//    }
//
//    private int computeUsableHeight() {
//        Rect r = new Rect();
//        mChildOfContent.getWindowVisibleDisplayFrame(r);
//        // 全屏模式下：直接返回r.bottom，r.top其实是状态栏的高度
//        return (r.bottom - r.top);
//    }
//}
