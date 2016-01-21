package com.yaya.mobile.html;

import com.yaya.mobile.utils.NetworkUtils;

import android.annotation.SuppressLint;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {
	private static final String TAG = "MyWebViewClient";
	MyWebViewCallback mCallback;
	WebView mWebView;
	Context myContext;

	@SuppressLint({ "SetJavaScriptEnabled", "SdCardPath" })
	@SuppressWarnings("deprecation")
	public MyWebViewClient(MyWebViewCallback callback, WebView webView, Context context) {
		this.mCallback = callback;
		this.mWebView = webView;
		this.myContext = context;

		WebSettings s = mWebView.getSettings();
		s.setBuiltInZoomControls(false);
		s.setDefaultTextEncodingName("gbk");
		// s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		s.setUseWideViewPort(true);
		s.setLoadWithOverviewMode(true);
		s.setSavePassword(true);
		s.setSaveFormData(true);
		s.setJavaScriptEnabled(true);
		s.setCacheMode(WebSettings.LOAD_NO_CACHE);
		s.setGeolocationEnabled(true);
		s.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");
		s.setDomStorageEnabled(true);
		mWebView.addJavascriptInterface(new WebAppInterface(myContext), "myAndroid");
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		super.onPageStarted(view, url, favicon);
		mCallback.onStartLoading();
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
	}

	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
		handler.proceed();
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (!NetworkUtils.isNetworkAvailable(myContext)) {
			mCallback.onNetworkDisable(url);
		} else if (url.endsWith("/product_category.html")) {
			mCallback.onSkipCategory();
		} else if (url.endsWith("/mendian/all.html")) {
			mCallback.onSkipDoors(1);
		} else if (url.endsWith("/user/center/index.html")) {
			mCallback.onSkipMember();
		} else if (url.endsWith("/mendian/index.html")) {
			mCallback.onSkipDoors(1);
		} else if (url.endsWith("/mendian/zitidian.html")) {
			mCallback.onSkipDoors(2);
		} else if (url.endsWith("/shopping_cart.html")) {
			mCallback.onSkipCart();
		} else if (url.endsWith("/register.html")) {
			mCallback.onSkipRegister();
		} else if (url.endsWith("/login.html")) {
			mCallback.onSkipLogin();
		} else {
			if (url.endsWith(".html")) {
				url += "?u=&p=&s=1";
			}
			view.loadUrl(url);
		}

		return true;
	}

	public interface MyWebViewCallback {
		public void onNetworkDisable(String url);

		public void onSkipCart();

		public void onStartLoading();

		public void onSkipDoors(int page);

		public void onSkipMember();

		public void goback();

		public void onSkipCategory();

		public void onSkipRegister();

		public void onSkipCommodityList(String category_id, String category_name);

		public void onSkipLogin();
	}

	public class WebAppInterface {
		Context mContext;

		/** Instantiate the interface and set the context */
		WebAppInterface(Context c) {
			mContext = c;
		}

		/** Show a toast from the web page */
		// 如果target 大于等于API 17，则需要加上如下注解
		@JavascriptInterface
		public void skip(String action) {
			Log.i(TAG, action);
			if (action.equals("member")) {
				mCallback.onSkipMember();
			} else if (action.equals("goback")) {
				mCallback.goback();
			} else if (action.equals("category")) {
				mCallback.onSkipCategory();
			} else if (action.startsWith("commodity-")) {
				String[] info = action.split("-");
				String category_id = info[1];
				String category_name = info[2];
				mCallback.onSkipCommodityList(category_id, category_name);
			}
		}
	}

}
