package com.yaya.mobile.me;

import org.androidannotations.annotations.AfterViews;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import com.yaya.mobile.MainActionBarActivity;
import com.yaya.mobile.MainActivity_;
import com.yaya.mobile.R;
import com.yaya.mobile.html.WebViewActivity_;
import com.yaya.mobile.me.bean.UserBean;
import com.yaya.mobile.net.ProgressMessage;
import com.yaya.mobile.net.RequestAdapter;
import com.yaya.mobile.net.RequestAdapter.RequestContentType;
import com.yaya.mobile.net.RequestAdapter.RequestMethod;
import com.yaya.mobile.net.ResponseData;
import com.yaya.mobile.net.ResponseData.ResultState;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

@EActivity(R.layout.activity_login)
public class LoginActivity extends MainActionBarActivity {
	@ViewById(R.id.et_account)
	EditText et_account;
	@ViewById(R.id.et_password)
	EditText et_password;
	@ViewById(R.id.cb_auto)
	CheckBox cb;
	@ViewById(R.id.progress)
	View progress;

	@Click(R.id.tv_register)
	void register() {
		RegisterActivity_.intent(this).start();
	}

	@Click(R.id.tv_find)
	void find() {
		String url = getString(R.string.url_findpsw) + "?u=&p=&s=1";
		WebViewActivity_.intent(this).main_url(url).title("找回密码").start();
	}

	@Click(R.id.btn_login)
	void login() {
		final String account = et_account.getText().toString();
		final String password = et_password.getText().toString();
		if (TextUtils.isEmpty(account)) {
			toastMsg("请输入账号", 1);
			return;
		}
		if (TextUtils.isEmpty(password)) {
			toastMsg("请输入密码", 1);
			return;
		}
		progress.setVisibility(View.VISIBLE);
		String json = "{\"user_name\":\"" + account + "\",\"password\":\"" + password + "\"}";
		new RequestAdapter() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onReponse(ResponseData data) {
				if (data.getResultState() == ResultState.eSuccess) {
					JSONObject mRootData = data.getMRootData();
					if (mRootData != null) {

						String msg = mRootData.optString("msg");
						int status = mRootData.optInt("status");
						if (status == 1) {
							SharedPreferences sp = getSharedPreferences("yaya_mobile.pref", MODE_PRIVATE);
							Editor editor = sp.edit();
							editor.putBoolean("remember", cb.isChecked());
							if (cb.isChecked()) {
								editor.putString("account", account);
								editor.putString("password", password);
								editor.commit();
							}
							JSONObject object = mRootData.optJSONObject("userInfo");
							parseToUserBean(account, object);
						}
						toastMsg(msg, 1);
					}
				} else {
					String msg = data.getMsg();
					toastMsg(msg, 1);
				}
				progress.setVisibility(View.GONE);
			}

			@Override
			public void onProgress(ProgressMessage msg) {
				// TODO Auto-generated method stub

			}
		}.setSaveSession(true).setUrl(getString(R.string.url_login)).setJSON(json).setRequestMethod(RequestMethod.ePost).setRequestContentType(RequestContentType.eJSON).notifyRequest();

	}

	void parseToUserBean(String mobile, JSONObject object) {
		String userName = object.optString("user_name");
		String password = object.optString("password");
		String avater = object.optString("avatar");
		int orderCount = object.optInt("order_count");
		UserBean mUserBean = UserBean.getUSerBean();
		
		mUserBean.setAvater(avater);
		mUserBean.setMobile(mobile);
		mUserBean.setOrderCount(orderCount);
		mUserBean.setPassword(password);
		mUserBean.setUserName(userName);
		
		UserBean.setUserBean(mUserBean);

		finish();
	}

	@AfterViews
	void initUI() {
		backButton.setText("登录");
		SharedPreferences sp = getSharedPreferences("yaya_mobile.pref", MODE_PRIVATE);
		if (sp.getBoolean("remember", false)) {
			et_account.setText(sp.getString("account", ""));
			et_password.setText(sp.getString("password", ""));
		}

	}

	@Override
	public void backButtonClick(View v) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent("com.yaya.mobile.skip_page");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.putExtra("page", 0);
		this.sendBroadcast(intent);
		MainActivity_.intent(this).start();
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
