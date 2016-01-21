package com.yaya.mobile.me;

import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import com.yaya.mobile.R;
import com.yaya.mobile.me.adapter.DoorsAdapter;
import com.yaya.mobile.me.adapter.DoorsAdapter.DoorCallBack;
import com.yaya.mobile.net.ProgressMessage;
import com.yaya.mobile.net.RequestAdapter;
import com.yaya.mobile.net.RequestAdapter.RequestContentType;
import com.yaya.mobile.net.RequestAdapter.RequestMethod;
import com.yaya.mobile.net.ResponseData.ResultState;
import com.yaya.mobile.utils.RegxUtils;
import com.yaya.mobile.net.ResponseData;

public class SelfFragment extends Fragment implements OnClickListener {
	Context mContext;
	View rootView;
	View progress;
	DoorsAdapter adapter;
	ListView lv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView != null) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null) {
				parent.removeView(rootView);
			}
		} else {
			mContext = getActivity();
			rootView = View.inflate(mContext, R.layout.listview, null);
			init();
		}
		return rootView;
	}

	public void onDestroy() {
		super.onDestroy();
		if (sendDialog != null) {
			sendDialog.dismiss();
			sendDialog = null;
		}
	};

	void init() {
		lv = (ListView) rootView.findViewById(R.id.lv);
		requestData();
	}

	void requestData() {
		progress = rootView.findViewById(R.id.pb);
		progress.setVisibility(View.VISIBLE);
		new RequestAdapter() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onReponse(ResponseData data) {
				progress.setVisibility(View.GONE);
				String msg = data.getMsg();
				if (data.getResultState() == ResultState.eSuccess) {
					JSONObject mRootData = data.getMRootData();
					if (mRootData != null) {
						JSONArray doorList = mRootData.optJSONArray("mendianList");
						fillData(doorList);
					} else {
						Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onProgress(ProgressMessage msg) {

			}
		}.setUrl(getString(R.string.url_door_ziti)).setRequestMethod(RequestMethod.eGet).notifyRequest();
	}

	EditText et_phone;
	EditText et_code;
	TextView tv_code;
	View code_progress;
	String mendian_id;

	void fillData(JSONArray array) {
		if (adapter == null) {
			adapter = new DoorsAdapter(mContext, array, null, new DoorCallBack() {

				@Override
				public void send(String id) {
					mendian_id = id;
					Builder builder = new Builder(mContext);
					View view = View.inflate(mContext, R.layout.dialog_send, null);
					builder.setView(view);

					et_phone = (EditText) view.findViewById(R.id.et_phone);
					et_code = (EditText) view.findViewById(R.id.et_code);
					tv_code = (TextView) view.findViewById(R.id.tv_code);
					TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
					TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);

					code_progress = view.findViewById(R.id.code_progress);
					tv_ok.setOnClickListener(SelfFragment.this);
					tv_cancel.setOnClickListener(SelfFragment.this);
					tv_code.setOnClickListener(SelfFragment.this);
					tv_code.performClick();
					sendDialog = builder.show();
				}
			});
			lv.setAdapter(adapter);
		} else {
			adapter.refresh(array, null);
		}
	}

	String sessionId;

	void getCode() {
		code_progress.setVisibility(View.VISIBLE);
		new RequestAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onReponse(ResponseData data) {
				code_progress.setVisibility(View.GONE);
				if (data.getResultState() == ResultState.eSuccess) {
					JSONObject mRootData = data.getMRootData();
					if (mRootData != null) {
						boolean status = mRootData.optInt("status") == 1;
						if (status) {
							String verifyCode = mRootData.optString("verifyCode");
							tv_code.setText(verifyCode);
							sessionId = mRootData.optString("sessionId");
						} else {
							String msg = mRootData.optString("msg");
							Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
						}
					}
				} else {
					String msg = data.getMsg();
					Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onProgress(ProgressMessage msg) {
				// TODO Auto-generated method stub

			}
		}.setUrl(getString(R.string.url_getcode)).setRequestMethod(RequestMethod.eGet).notifyRequest();
	}

	Dialog sendDialog;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_ok:
			String phone = et_phone.getText().toString().trim();
			if (TextUtils.isEmpty(phone)) {
				Toast.makeText(mContext, "请输入手机号码", Toast.LENGTH_LONG).show();
				return;
			}
			if (!RegxUtils.isPhone(phone)) {
				Toast.makeText(mContext, "请输入正确的手机号码", Toast.LENGTH_LONG).show();
				return;
			}
			String code = et_code.getText().toString().trim();
			if (TextUtils.isEmpty(code)) {
				Toast.makeText(mContext, "请输入验证码", Toast.LENGTH_LONG).show();
				return;
			}
			sendMsg(phone, code);
			break;
		case R.id.tv_code:
			getCode();
			break;
		case R.id.tv_cancel:
			sendDialog.dismiss();
			break;

		default:
			break;
		}
	}

	void sendMsg(String phone, String code) {
		// {mobile:"接收者手机号",mendian_id:"门店自提点id",vcode:"用户输入的验证码",sessionId:"验证码校验Id"}
		String json = "{\"mobile\":\"" + phone + "\",\"mendian_id\":\"" + mendian_id + "\",\"vcode\":\"" + code + "\",\"sessionId\":\"" + sessionId + "\"}";
		new RequestAdapter() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onReponse(ResponseData data) {
				if (data.getResultState() == ResultState.eSuccess) {
					JSONObject mRootData = data.getMRootData();
					if (mRootData != null) {
						String msg = mRootData.optString("msg");
						boolean status = mRootData.optInt("status") == 1;
						if (status)
							sendDialog.dismiss();
						else
							tv_code.performClick();
						Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
					} else {
						String msg = data.getMsg();
						Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
					}
				} else {
					String msg = data.getMsg();
					Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onProgress(ProgressMessage msg) {
				// TODO Auto-generated method stub

			}
		}.setUrl(getString(R.string.url_sendmsg)).setRequestMethod(RequestMethod.ePost).setRequestContentType(RequestContentType.eJSON).setJSON(json).notifyRequest();
	}

}
