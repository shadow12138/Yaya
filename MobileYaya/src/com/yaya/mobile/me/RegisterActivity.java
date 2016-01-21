package com.yaya.mobile.me;

import org.androidannotations.annotations.AfterViews;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import com.yaya.mobile.MainActionBarActivity;
import com.yaya.mobile.R;
import com.yaya.mobile.net.ProgressMessage;
import com.yaya.mobile.net.RequestAdapter;
import com.yaya.mobile.net.RequestAdapter.RequestContentType;
import com.yaya.mobile.net.RequestAdapter.RequestMethod;
import com.yaya.mobile.net.ResponseData;
import com.yaya.mobile.net.ResponseData.ResultState;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

@EActivity(R.layout.activity_register)
public class RegisterActivity extends MainActionBarActivity {
	private static final String TAG = "RegisterActivity";
	String vcode;
	@ViewById(R.id.et_psw)
	EditText et_psw;
	@ViewById(R.id.et_mobile)
	EditText et_phone;
	@ViewById(R.id.et_confirm)
	EditText et_confirm;
	@ViewById(R.id.et_code)
	EditText et_code;
	@ViewById(R.id.tv_code)
	TextView tv_code;
	@ViewById(R.id.et_username)
	EditText et_username;
	@ViewById(R.id.pb)
	View progress;
	String sessionId = null;

	@Click(R.id.btn_register)
	void register() {
		String phone = et_phone.getText().toString();
		String psw = et_psw.getText().toString();
		String confirm = et_confirm.getText().toString();
		String code = et_code.getText().toString().trim();
		String username = et_username.getText().toString();

		if (TextUtils.isEmpty(username)) {
			toastMsg("请输入用户名", 1);
			return;
		}

		if (TextUtils.isEmpty(phone)) {
			toastMsg("请输入手机号码", 1);
			return;
		}

		if (TextUtils.isEmpty(psw)) {
			toastMsg("请输入密码", 1);
			return;
		}

		if (TextUtils.isEmpty(confirm)) {
			toastMsg("请确认密码", 1);
			return;
		}

		if (TextUtils.isEmpty(code)) {
			toastMsg("请输入验证码", 1);
			return;
		}

		progress.setVisibility(View.VISIBLE);
		String json = "{\"user_name\":\"" + username + "\",\"password\":\"" + psw + "\",\"mobile\":\"" + phone + "\",\"vcode\":\"" + code + "\",\"sessionId\":\"" + sessionId + "\"}";
		Log.i(TAG, json);

		new RequestAdapter() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onReponse(ResponseData data) {
				progress.setVisibility(View.GONE);
				if (data.getResultState() == ResultState.eSuccess) {
					JSONObject mRootData = data.getMRootData();
					String msg = mRootData.optString("msg");
					int status = mRootData.optInt("status");
					if (status == 1)
						finish();
					toastMsg(msg, 1);
				} else {
					String msg = data.getMsg();
					toastMsg(msg, 1);
				}
			}

			@Override
			public void onProgress(ProgressMessage msg) {

			}
		}.setUrl(getString(R.string.url_register)).setJSON(json).setRequestMethod(RequestMethod.ePost).setRequestContentType(RequestContentType.eJSON).notifyRequest();
	}

	@Click(R.id.tv_login)
	void login() {
		finish();
	}

	@Click(R.id.tv_code)
	void getCode() {
		progress.setVisibility(View.VISIBLE);
		
		new RequestAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onReponse(ResponseData data) {
				progress.setVisibility(View.GONE);
				if (data.getResultState() == ResultState.eSuccess) {
					JSONObject mRootData = data.getMRootData();
					if (mRootData != null) {
						boolean status = mRootData.optInt("status") == 1;
						if (status) {
							String verifyCode = mRootData.optString("verifyCode");
							vcode = verifyCode;
							tv_code.setText(verifyCode);
							sessionId = mRootData.optString("sessionId");
						} else {
							String msg = mRootData.optString("msg");
							toastMsg(msg, 1);
						}
					}
				} else {
					String msg = data.getMsg();
					toastMsg(msg, 1);
				}
			}

			@Override
			public void onProgress(ProgressMessage msg) {
				// TODO Auto-generated method stub

			}
		}.setUrl(getString(R.string.url_getcode)).setRequestMethod(RequestMethod.eGet).notifyRequest();
	}

	@AfterViews
	void initUI() {
		backButton.setText("注册");
		tv_code.performClick();
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
