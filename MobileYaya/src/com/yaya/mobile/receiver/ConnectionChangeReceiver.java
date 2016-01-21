package com.yaya.mobile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionChangeReceiver extends BroadcastReceiver {
	NetStateChangeCallBack mCallback;

	public ConnectionChangeReceiver(NetStateChangeCallBack callback) {
		this.mCallback = callback;
	}

	public interface NetStateChangeCallBack {
		void notifyNetworkState(int status);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		// connectivityManager.getActiveNetworkInfo();
		if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
			mCallback.notifyNetworkState(0);
		} else {
			mCallback.notifyNetworkState(1);
		}
	}
}