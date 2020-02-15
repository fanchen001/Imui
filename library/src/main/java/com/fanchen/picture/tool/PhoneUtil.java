package com.fanchen.picture.tool;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * description:
 */
public class PhoneUtil {

	public static int getPhoneWid(Context context) {
		WindowManager windowManager =
			(WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metric = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metric);
		return metric.widthPixels;
	}

	public static int getPhoneHei(Context context) {
		WindowManager windowManager =
			(WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metric = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metric);
		return metric.heightPixels;
	}

	public static float getPhoneRatio(Context context) {
		return ((float) getPhoneHei(context)) / ((float) getPhoneWid(context));
	}

	public static int px2dp(Context context, float pxValue) {
		final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int dp2px(Context context, float dipValue) {
		final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}