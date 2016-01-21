package com.yaya.mobile.me;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaya.mobile.MainActionBarActivity;
import com.yaya.mobile.MyApplication;
import com.yaya.mobile.R;
import com.yaya.mobile.utils.Utils;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@EActivity(R.layout.activity_door_detail)
public class DoorsDetailActivity extends MainActionBarActivity {
	@Extra
	String extra;
	@ViewById(R.id.wv_detail)
	WebView wv_detail;
	@ViewById(R.id.iv)
	ImageView iv;
	@ViewById(R.id.tv_address)
	TextView tv_address;
	@ViewById(R.id.sell_consult)
	TextView sell_consult;
	@ViewById(R.id.after_sell)
	TextView after_sell;
	@ViewById(R.id.work_time)
	TextView work_time;
	@ViewById(R.id.ll_functions)
	LinearLayout functions;

	JSONObject object;

	int[] serviceDrawables = { R.drawable.shop01, R.drawable.shop02, R.drawable.shop03, R.drawable.shop04, R.drawable.shop05 };
	String[] serviceTexts = { "提货", "体验", "维修", "销售", "贴膜" };

	@ViewById(R.id.bmapView)
	MapView mMapView;

	@AfterInject
	void initMaps() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@AfterViews
	void initUI() {
		try {
			object = new JSONObject(extra);
			initData();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	void initData() {
		String title = object.optString("title");
		backButton.setText(title);

		String html = object.optString("text");
		wv_detail.getSettings().setJavaScriptEnabled(true);
		wv_detail.loadDataWithBaseURL(getString(R.string.url_get_image), html, "text/html", "utf-8", null);

		ImageLoader.getInstance().displayImage(object.optString("img_url"), iv, MyApplication.getOptions());

		tv_address.setText("门店地址：" + object.optString("address"));
		sell_consult.setText("销售热线：" + object.optString("tel"));
		after_sell.setText("售后热线：" + object.optString("tel_zx"));
		work_time.setText("营业时间：" + object.optString("worktime"));

		String[] services = object.optString("service_type").split("  ");
		int padding = Utils.dp2px(this, 5);
		for (String service : services) {
			for (int i = 0; i < serviceTexts.length; i++) {
				if (service.equals(serviceTexts[i])) {
					TextView tv = new TextView(this);
					Drawable top = getResources().getDrawable(serviceDrawables[i]);
					top.setBounds(0, 0, top.getIntrinsicWidth(), top.getIntrinsicHeight());
					tv.setCompoundDrawables(null, top, null, null);
					tv.setCompoundDrawablePadding(Utils.dp2px(this, 2));
					tv.setPadding(padding, padding, padding, padding);
					tv.setText(service);
					tv.setTextSize(12);
					tv.setGravity(Gravity.CENTER);
					tv.setTextColor(Color.GRAY);
					functions.addView(tv);
				}
			}
		}
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
