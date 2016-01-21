package com.yaya.mobile.me;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
import com.yaya.mobile.R;
import com.yaya.mobile.me.adapter.DoorsAdapter;
import com.yaya.mobile.me.adapter.DoorsAdapter.DoorCallBack;
import com.yaya.mobile.net.ProgressMessage;
import com.yaya.mobile.net.RequestAdapter;
import com.yaya.mobile.net.RequestAdapter.RequestContentType;
import com.yaya.mobile.net.RequestAdapter.RequestMethod;
import com.yaya.mobile.net.ResponseData;
import com.yaya.mobile.net.ResponseData.ResultState;
import com.yaya.mobile.utils.RegxUtils;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
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

public class NearDoorsFragment extends Fragment implements OnClickListener {
	Context mContext;
	View rootView;
	DoorsAdapter adapter;
	View progress;

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

	ListView lv;

	void init() {
		lv = (ListView) rootView.findViewById(R.id.lv);
		lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(mContext, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(intent, 0);
			return;
		}

		getLocation();
	}

	LocationManager lm;


	public void getLocation() {
		progress = rootView.findViewById(R.id.pb);
		progress.setVisibility(View.VISIBLE);
		
		String bestProvider = lm.getBestProvider(getCriteria(), true);
		Location location = lm.getLastKnownLocation(bestProvider);
		updateView(location);
		lm.addGpsStatusListener(listener);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30 * 60 * 1000, 1000, locationListener);

	}

	void requestData(Location mLocation) {
		progress.setVisibility(View.VISIBLE);
		String json = "{\"pointX\":\"" + mLocation.getLongitude() + "\",\"pointY\":\"" + mLocation.getLatitude() + "\"}";

		new RequestAdapter() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onReponse(ResponseData data) {
				progress.setVisibility(View.GONE);
				if (data.getResultState() == ResultState.eSuccess) {
					JSONObject mRootData = data.getMRootData();
					if (mRootData != null) {
						JSONArray doorList = mRootData.optJSONArray("mendianList");
						JSONArray distanceList = mRootData.optJSONArray("distanceList");
						fillData(doorList, distanceList);
					}
				}
			}

			@Override
			public void onProgress(ProgressMessage msg) {
				// TODO Auto-generated method stub

			}
		}.setUrl(getString(R.string.url_doors)).setJSON(json).setRequestContentType(RequestContentType.eJSON).setRequestMethod(RequestMethod.ePost).notifyRequest();
	}

	EditText et_phone;
	EditText et_code;
	TextView tv_code;
	View code_progress;
	String mendian_id;

	void fillData(JSONArray array, JSONArray distanceList) {
		if (adapter == null) {
			adapter = new DoorsAdapter(mContext, array, distanceList, new DoorCallBack() {

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
					tv_ok.setOnClickListener(NearDoorsFragment.this);
					tv_cancel.setOnClickListener(NearDoorsFragment.this);
					tv_code.setOnClickListener(NearDoorsFragment.this);
					tv_code.performClick();
					sendDialog = builder.show();
				}
			});
			lv.setAdapter(adapter);
		} else {
			adapter.refresh(array, distanceList);
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

	// 位置监听
	private LocationListener locationListener = new LocationListener() {

		public void onLocationChanged(Location location) {
			updateView(location);
		}

		public void onProviderEnabled(String provider) {
			Location location = lm.getLastKnownLocation(provider);
			updateView(location);
		}

		public void onProviderDisabled(String provider) {
			updateView(null);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	};

	GpsStatus.Listener listener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			switch (event) {
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				GpsStatus gpsStatus = lm.getGpsStatus(null);
				int maxSatellites = gpsStatus.getMaxSatellites();
				Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
				int count = 0;
				while (iters.hasNext() && count <= maxSatellites) {
					// GpsSatellite s = iters.next();
					count++;
				}
				System.out.println("搜索到：" + count + "颗卫星");
				break;
			}
		};
	};

	private void updateView(Location location) {
		if (location != null) {
			requestData(location);
		}
	}

	private Criteria getCriteria() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(false);
		criteria.setBearingRequired(false);
		criteria.setAltitudeRequired(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		return criteria;
	}
}
