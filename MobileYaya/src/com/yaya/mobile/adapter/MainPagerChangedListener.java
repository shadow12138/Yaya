package com.yaya.mobile.adapter;


import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainPagerChangedListener implements OnPageChangeListener {
	RadioGroup mRadioGroup;

	public MainPagerChangedListener(RadioGroup radioGroup) {
		this.mRadioGroup = radioGroup;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		RadioButton rb = (RadioButton) mRadioGroup.getChildAt(arg0);
		rb.setChecked(true);
	}

}
