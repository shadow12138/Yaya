package com.yaya.mobile.me;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import android.view.View;

import com.yaya.mobile.MainActionBarActivity;
import com.yaya.mobile.R;


@EActivity(R.layout.activity_find1)
public class FindPswActivity extends MainActionBarActivity {
	@AfterViews
	void initUI() {
		backButton.setText("忘记密码");
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
