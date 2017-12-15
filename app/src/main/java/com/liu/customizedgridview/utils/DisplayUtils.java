package com.liu.customizedgridview.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 与屏幕信息有关的类，包括屏幕的长宽、分辨率、长度换算
 * 
 */
public class DisplayUtils {
	/**
	 * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
	 * @param context
	 * @return 平板返回 True，手机返回 False
	 */
	public static boolean isPad(Context context) {
		return (context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK)
				>= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	public static float getActualDisplayFontPx(int fontSize) {
		float result = 0;
		result = ((fontSize) / 72.0F) * 96.0F;
		return result;
	}
	public static float getDisplayFontPx(int fontSize) {
		float result = 0;
		result = ((fontSize + 4) / 72.0F) * 96.0F;
		return result;
	}

	public static float getDisplayFontPxForPad(int fontSize) {
		float result = 0;
		if (fontSize > 4)
		{
			result = ((fontSize - 4) / 72.0F) * 96.0F;
		}
		else
		{
			result = ((fontSize) / 72.0F) * 96.0F;
		}

		return result;
	}
	/**
	 * 获取屏幕分辨率
	 * @param context
	 * @return
	 */
	public static int[] getScreenDispaly(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = windowManager.getDefaultDisplay().getWidth();// 手机屏幕的宽度
		int height = windowManager.getDefaultDisplay().getHeight();// 手机屏幕的高度
		int result[] = { width, height };
		return result;
	}

	/** 获取屏幕宽度 */
	public static int getDisplayWidth(Context context) {
		if (context != null) {
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			int w_screen = dm.widthPixels;
			// int h_screen = dm.heightPixels;
			return w_screen;
		}
		return 720;
	}

	/** 获取屏幕高度 */
	public static int getDisplayHight(Context context) {
		if (context != null) {
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			// int w_screen = dm.widthPixels;
			int h_screen = dm.heightPixels;
			return h_screen;
		}
		return 1280;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

}
