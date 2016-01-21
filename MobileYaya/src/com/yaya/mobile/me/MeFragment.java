package com.yaya.mobile.me;

import org.androidannotations.annotations.AfterViews;

import org.androidannotations.annotations.EFragment;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaya.mobile.MainActivity_;
import com.yaya.mobile.R;
import com.yaya.mobile.html.WebViewActivity_;
import com.yaya.mobile.me.adapter.MeGridViewAdapter;
import com.yaya.mobile.me.bean.UserBean;
import com.yaya.mobile.ui.view.MyGridView;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

@EFragment(R.layout.me_fragment)
public class MeFragment extends Fragment {
	View rootView;
	Context mContext;
	UserBean mUser;
	TextView tv_quit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView != null) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null) {
				parent.removeView(rootView);
			}
		} else {
			mContext = getActivity();
			rootView = View.inflate(mContext, R.layout.me_fragment, null);
			initGridView();
		}
		return rootView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && TextUtils.isEmpty(UserBean.getUSerBean().getMobile()))
			LoginActivity_.intent(getActivity()).start();
	}

	@Override
	public void onResume() {
		super.onResume();
		init();
	}

	void initGridView() {
		MyGridView gv = (MyGridView) rootView.findViewById(R.id.gv);
		tv_quit = (TextView) rootView.findViewById(R.id.tv_quit);
		gv.setAdapter(new MeGridViewAdapter(mContext));
		gv.setOnItemClickListener(new MyGridViewItemClick());
		tv_quit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UserBean.setUserBean(null);
				MainActivity_ activity = (MainActivity_) mContext;
				activity.skipTo(0);
			}
		});
	}

	class MyGridViewItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			String url = null;
			String title = null;
			switch (position) {
			case 0:
				url = getString(R.string.url_me_order);
				title = "我的订单";
				break;
			case 1:
				title = "收货人管理";
				url = getString(R.string.url_me_address);
				break;
			case 2:
				title = "评价晒单";
				url = getString(R.string.url_me_comment);
				break;
			case 3:
				title = "我的关注";
				url = getString(R.string.url_me_focus);
				break;
			case 4:
				title = "投诉建议";
				url = getString(R.string.url_me_advice);
				break;
			case 5:
				title = "个人信息";
				url = getString(R.string.url_me_info);
				break;
			case 7:
				title = "修改密码";
				url = getString(R.string.url_modify_psw);
				break;
			case 8:
				title = "我的信息";
				url = getString(R.string.url_information);
				break;
			case 6:
				DoorsActivity_.intent(mContext).page(1).start();
				break;
			case 9:
				title = "售后服务";
				url = getString(R.string.url_meshouhou);
				break;
			case 10:
				title="会员俱乐部";
				url = getString(R.string.url_meclub);
				break;
			case 11:
				title = "网上报修";
				url = getString(R.string.url_repair);
				break;

			default:
				break;
			}
			if (!TextUtils.isEmpty(url)) {
				url += "?u=" + mUser.getMobile() + "&p=" + mUser.getPassword() + "&t=2&s=1";
				WebViewActivity_.intent(mContext).main_url(url).title(title).start();
			} 
		}

	}

	@AfterViews
	public void init() {
		TextView tv_username = (TextView) rootView.findViewById(R.id.user_name);
		TextView tv_ordercount = (TextView) rootView.findViewById(R.id.tv_ordercount);
		ImageView iv_avatar = (ImageView) rootView.findViewById(R.id.member_photo);
		mUser = UserBean.getUSerBean();
		if (!TextUtils.isEmpty(mUser.getMobile())) {
			tv_username.setText(mUser.getUserName());
			tv_ordercount.setText(String.valueOf(mUser.getOrderCount()));
			ImageLoader.getInstance().displayImage(mUser.getAvater(), iv_avatar);
		}
		
		tv_ordercount.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String url = getString(R.string.url_me_order) + "?u=" + mUser.getMobile() + "&p=" + mUser.getPassword() + "&t=2&s=1";
				WebViewActivity_.intent(mContext).title("我的订单").main_url(url).start();
			}
		});
	}

}
