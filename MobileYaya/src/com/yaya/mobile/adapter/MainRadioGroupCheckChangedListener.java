package com.yaya.mobile.adapter;

import com.yaya.mobile.MainActivity_;
import com.yaya.mobile.me.DoorsActivity_;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainRadioGroupCheckChangedListener implements OnCheckedChangeListener {
	ViewPager mViewPager;
	Context mContext;

	public MainRadioGroupCheckChangedListener(ViewPager vp, Context context) {
		this.mViewPager = vp;
		this.mContext = context;
	}

	@Override
	public void onCheckedChanged(final RadioGroup arg0, int arg1) {
		View v = arg0.findViewById(arg1);
		int position = (Integer) v.getTag();
		Log.i("TAG", "position = " + position);
		switch (position) {
		case 0:
		case 1:
		case 2:
		case 3:
			mViewPager.setCurrentItem(position);
			break;
		case 4:
			DoorsActivity_.intent(mContext).page(1).start();
			final MainActivity_ activity_ = (MainActivity_) mContext;
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					activity_.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							((RadioButton)arg0.getChildAt(0)).setChecked(true);
						}
					});
				}
			}, 200);
			break;
		default:
			break;
		}
	}

}
