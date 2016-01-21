package com.yaya.mobile.me.adapter;

import java.util.List;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DoorsPagerAdapter extends FragmentPagerAdapter {
	List<Fragment> mFragments;
	String[] mTitles = { "附近门店", "所有门店", "自提点" };

	public DoorsPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.mFragments = fragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		return mFragments.get(arg0);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mTitles[position];
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

}
