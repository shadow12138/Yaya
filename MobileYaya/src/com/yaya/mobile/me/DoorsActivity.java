package com.yaya.mobile.me;

import java.util.ArrayList;

import java.util.List;

import org.androidannotations.annotations.AfterViews;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.yaya.mobile.MainActionBarActivity;
import com.yaya.mobile.R;
import com.yaya.mobile.adapter.MainPagerChangedListener;
import com.yaya.mobile.adapter.MainRadioGroupCheckChangedListener;
import com.yaya.mobile.me.adapter.DoorsPagerAdapter;

@EActivity(R.layout.activity_doors)
public class DoorsActivity extends MainActionBarActivity {
	@ViewById(R.id.vp)
	ViewPager mViewPager;
	@Extra
	int page;
	@ViewById(R.id.rg)
	RadioGroup rg;
	List<Fragment> mFragments = new ArrayList<Fragment>();
	NearDoorsFragment nearFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@AfterViews
	void initUI() {
		backButton.setText("门店");
		nearFragment = new NearDoorsFragment();
		mFragments.add(nearFragment);
		mFragments.add(new AllDoorsFragment());
		mFragments.add(new SelfFragment());
		mViewPager.setAdapter(new DoorsPagerAdapter(getSupportFragmentManager(), mFragments));

		mViewPager.setCurrentItem(page);
		for (int i = 0; i < rg.getChildCount(); i++) {
			rg.getChildAt(i).setTag(i);
		}
		rg.setOnCheckedChangeListener(new MainRadioGroupCheckChangedListener(mViewPager, this));
		mViewPager.setOnPageChangeListener(new MainPagerChangedListener(rg));

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean isOpened = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (isOpened)
			nearFragment.getLocation();
		else
			Toast.makeText(this, "GPS未打开", Toast.LENGTH_SHORT).show();
		super.onActivityResult(arg0, arg1, arg2);
	}

	@Override
	public void backButtonClick(View v) {
		finish();
	}

	@Override
	public void titleButtonClick(View v) {

	}

	@Override
	public void rightButtonClick(View v) {

	}

	@Override
	public Boolean showHeadView() {
		return true;
	}

}
