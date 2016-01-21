package com.yaya.mobile.adapter;

import com.yaya.mobile.R;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashPagerChangedListener implements OnPageChangeListener {
	LinearLayout mLinearLayout;
	TextView tv_go;

	public SplashPagerChangedListener(LinearLayout ll_points, TextView tv_go) {
		mLinearLayout = ll_points;
		this.tv_go = tv_go;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		for (int i = 0; i < mLinearLayout.getChildCount(); i++)
			((ImageView) mLinearLayout.getChildAt(i)).setImageResource(R.drawable.page_unfocused);
		ImageView iv = (ImageView) mLinearLayout.getChildAt(arg0);
		iv.setImageResource(R.drawable.page_focused);
		
		tv_go.setVisibility(arg0 == mLinearLayout.getChildCount() - 1 ? View.VISIBLE : View.GONE);
	}
}
