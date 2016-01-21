package com.yaya.mobile.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class Utils {
	public static final String TAG = "Utils";

	public static void hiddenSoftBorad(Context context) {
		try {
			((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {

		}
	}

	public static int dp2px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public static int px2dp(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

}
