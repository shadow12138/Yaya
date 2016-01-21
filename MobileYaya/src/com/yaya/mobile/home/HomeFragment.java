package com.yaya.mobile.home;

import org.androidannotations.annotations.EFragment;

import com.yaya.mobile.MainActivity;
import com.yaya.mobile.R;
import com.yaya.mobile.category.CommodityActivity_;
import com.yaya.mobile.html.MyWebChromeClient;
import com.yaya.mobile.html.MyWebChromeClient.MyWebChromeCallBack;
import com.yaya.mobile.html.MyWebViewClient;
import com.yaya.mobile.html.MyWebViewClient.MyWebViewCallback;
import com.yaya.mobile.me.DoorsActivity_;
import com.yaya.mobile.me.LoginActivity_;
import com.yaya.mobile.me.RegisterActivity_;
import com.yaya.mobile.utils.NetworkUtils;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

@EFragment
public class HomeFragment extends Fragment {
	View rootView;
	Context mContext;

	View wifi;
	TextView tv_reload;
	String reloadUrl;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView != null) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null)
				parent.removeView(rootView);
		} else {
			mContext = getActivity();
			rootView = View.inflate(mContext, R.layout.home_fragment, null);
		}
		init();
		return rootView;
	}

	public WebView wv;

	public void init() {
		String main_url = "http://myaya.ynyes.net/index.html?u=&p=&s=1";
		wv = (WebView) rootView.findViewById(R.id.wv);
		wifi = rootView.findViewById(R.id.wifi);
		tv_reload = (TextView) wifi.findViewById(R.id.tv_reload);
		tv_reload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (NetworkUtils.isNetworkAvailable(mContext)) {
					wifi.setVisibility(View.GONE);
					wv.loadUrl(reloadUrl);
				}
			}
		});
		final ProgressBar pb = (ProgressBar) rootView.findViewById(R.id.pb);
		wv.setWebChromeClient(new MyWebChromeClient(new MyWebChromeCallBack() {

			@Override
			public void onLoadedHalf() {
				pb.setVisibility(View.GONE);
			}

			@Override
			public void onProgressChanged(int progress) {
				// TODO Auto-generated method stub

			}
		}));

		wv.setWebViewClient(new MyWebViewClient(new MyWebViewCallback() {

			@Override
			public void onStartLoading() {
				pb.setVisibility(View.VISIBLE);
			}

			@Override
			public void onSkipMember() {
				// 跳转至会员中心
				((MainActivity) getActivity()).skipTo(2);
			}

			@Override
			public void goback() {
				// 返回
			}

			@Override
			public void onSkipCategory() {
				// 跳转至商品分类
				((MainActivity) getActivity()).skipTo(1);

			}

			@Override
			public void onSkipCommodityList(String category_id, String category_name) {
				CommodityActivity_.intent(mContext).category_id(Integer.parseInt(category_id)).start();

			}

			@Override
			public void onSkipCart() {
				((MainActivity) getActivity()).skipTo(3);

			}

			@Override
			public void onSkipDoors(int page) {
				DoorsActivity_.intent(mContext).page(page).start();

			}

			@Override
			public void onSkipRegister() {
				RegisterActivity_.intent(mContext).start();
			}

			@Override
			public void onSkipLogin() {
				LoginActivity_.intent(mContext).start();
			}

			@Override
			public void onNetworkDisable(String url) {
				wifi.setVisibility(View.VISIBLE);
				reloadUrl = url;
			}

		}, wv, mContext));

		if (NetworkUtils.isNetworkAvailable(mContext)) {
			wv.loadUrl(main_url);
		} else {
			wifi.setVisibility(View.VISIBLE);
			reloadUrl = main_url;
		}

	}

}
