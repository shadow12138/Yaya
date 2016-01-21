package com.yaya.mobile.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SplashPagerAdapter extends PagerAdapter {

	ImageView[] imgs;

	public SplashPagerAdapter(ImageView[] imgs) {
		this.imgs = imgs;
	}

	@Override
	public int getCount() {
		return imgs.length;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(imgs[position]);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView img = imgs[position];
		container.addView(img);
		return img;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

}
