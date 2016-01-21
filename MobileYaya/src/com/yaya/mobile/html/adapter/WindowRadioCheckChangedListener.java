package com.yaya.mobile.html.adapter;


import com.yaya.mobile.MainActivity_;

import android.content.Context;
import android.content.Intent;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class WindowRadioCheckChangedListener implements OnCheckedChangeListener {
	Context mContext;
	PopupWindow mWindow;
	public WindowRadioCheckChangedListener(Context context, PopupWindow window) {
		this.mContext = context;
		this.mWindow = window;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int tag = (Integer) group.findViewById(checkedId).getTag();
		sendBroad(tag);
	}

	void sendBroad(int page) {
		mWindow.dismiss();
		Intent intent = new Intent("com.yaya.mobile.skip_page");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.putExtra("page", page);
		mContext.sendBroadcast(intent);
		MainActivity_.intent(mContext).start();
	}

}
