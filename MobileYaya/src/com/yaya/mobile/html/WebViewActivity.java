package com.yaya.mobile.html;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import com.yaya.mobile.MainActivity_;
import com.yaya.mobile.R;
import com.yaya.mobile.category.CommodityActivity_;
import com.yaya.mobile.html.MyWebChromeClient.MyWebChromeCallBack;
import com.yaya.mobile.html.MyWebViewClient.MyWebViewCallback;
import com.yaya.mobile.html.adapter.WindowRadioCheckChangedListener;
import com.yaya.mobile.me.DoorsActivity_;
import com.yaya.mobile.me.LoginActivity_;
import com.yaya.mobile.me.RegisterActivity_;
import com.yaya.mobile.utils.NetworkUtils;
import com.yaya.mobile.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

@EActivity(R.layout.activity_webview)
public class WebViewActivity extends Activity {
	@Extra
	String main_url;
	@Extra
	String title;
	WebView mWebView;
	ProgressBar mProgressBar;
	ProgressBar mHorizontalProgressBar;
	@ViewById(R.id.leftBtn)
	Button backBtn;
	@ViewById(R.id.rightBtn)
	Button rightBtn;
	PopupWindow popupWindow;
	RadioGroup rg;
	@ViewById(R.id.wifi)
	View wifi;
	String reloadUrl;
	@ViewById(R.id.tv_reload)
	TextView tv_reload;

	@Click(R.id.rightBtn)
	void showMenu() {
		if (popupWindow == null) {
			View view = View.inflate(this, R.layout.dropdown_menu, null);
			popupWindow = new PopupWindow(view, -1, -2);
			popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			int drawabeles[] = { R.drawable.home, R.drawable.category, R.drawable.member, R.drawable.cart, R.drawable.doors_icon };
			rg = (RadioGroup) view.findViewById(R.id.rg);
			int bounds = Utils.dp2px(this, 20);
			for (int i = 0; i < rg.getChildCount(); i++) {
				RadioButton rb = (RadioButton) rg.getChildAt(i);
				rb.setTag(i);
				Drawable drawableTop = getResources().getDrawable(drawabeles[i]);
				drawableTop.setBounds(0, 0, bounds, bounds);
				rb.setCompoundDrawables(null, drawableTop, null, null);
			}
			rg.setOnCheckedChangeListener(new WindowRadioCheckChangedListener(this, popupWindow));
		}
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			popupWindow.showAsDropDown(rightBtn);

		}
	}

	@Click(R.id.leftBtn)
	void back() {
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (popupWindow != null) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	@AfterViews
	void initViews() {
		backBtn.setText(title);
		mWebView = (WebView) findViewById(R.id.wv);
		mProgressBar = (ProgressBar) findViewById(R.id.pb);
		mHorizontalProgressBar = (ProgressBar) findViewById(R.id.horizontal_pb);

		mWebView.setWebChromeClient(new MyWebChromeClient(new MyWebChromeCallBack() {

			@Override
			public void onLoadedHalf() {
				// mProgressBar.setVisibility(View.GONE);
			}

			@Override
			public void onProgressChanged(int progress) {
				if (progress > 10)
					mProgressBar.setVisibility(View.GONE);
				mHorizontalProgressBar.setProgress(progress);
				if (progress == 100)
					mHorizontalProgressBar.setVisibility(View.GONE);
			}
		}));

		mWebView.setWebViewClient(new MyWebViewClient(new MyWebViewCallback() {

			@Override
			public void onStartLoading() {
				mHorizontalProgressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onSkipMember() {
				sendBroad(2);
			}

			@Override
			public void goback() {
				// 返回
				onBackPressed();
			}

			@Override
			public void onSkipCategory() {
				// 商品分类
				sendBroad(1);
			}

			@Override
			public void onSkipCommodityList(String category_id, String category_name) {
				// 商品列表
				CommodityActivity_.intent(WebViewActivity.this).category_id(Integer.parseInt(category_id)).start();

			}

			@Override
			public void onSkipCart() {
				sendBroad(3);
			}

			@Override
			public void onSkipDoors(int page) {
				DoorsActivity_.intent(WebViewActivity.this).page(page).start();

			}

			@Override
			public void onSkipRegister() {
				RegisterActivity_.intent(WebViewActivity.this).start();

			}

			@Override
			public void onSkipLogin() {
				LoginActivity_.intent(WebViewActivity.this).start();

			}

			@Override
			public void onNetworkDisable(String url) {
				wifi.setVisibility(View.VISIBLE);
				reloadUrl = url;

			}
		}, mWebView, this));

		if (NetworkUtils.isNetworkAvailable(this)) {
			mWebView.loadUrl(main_url);
		} else {
			wifi.setVisibility(View.VISIBLE);
			reloadUrl = main_url;
		}
	}

	public void reload(View v) {
		if (NetworkUtils.isNetworkAvailable(this)) {
			wifi.setVisibility(View.GONE);
			mWebView.loadUrl(reloadUrl);
		}
	}

	void sendBroad(int page) {
		Intent intent = new Intent("com.yaya.mobile.skip_page");
		intent.putExtra("page", page);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		this.sendBroadcast(intent);
		MainActivity_.intent(this).start();
	}

	@Override
	public void onBackPressed() {
		if (mWebView != null && mWebView.canGoBack()) {
			mWebView.goBack();
		} else {
			super.onBackPressed();
		}
	}

}
