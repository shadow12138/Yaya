package com.yaya.mobile.category.adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yaya.mobile.R;

public class ConditionAdapter extends BaseAdapter {
	private static final String TAG = "ConditionAdapter";
	Context mContext;
	JSONObject mConditions;
	Callback mCallback;
	JSONArray dataList;
	int index;

	public ConditionAdapter(Context context, String conditions, Callback callback) {
		this.mContext = context;
		Log.i(TAG, conditions);
		this.mCallback = callback;
		try {
			mConditions = new JSONObject(conditions);
			index = mConditions.optInt("Index", -1);
			dataList = mConditions.optJSONArray("Filter");
		} catch (JSONException e) {

			try {
				dataList = new JSONArray(conditions);
				index = -1;
			} catch (JSONException e1) {
				e1.printStackTrace(); 
			}
		}

	}

	@Override
	public int getCount() {
		return dataList == null ? 0 : dataList.length();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if (convertView == null)
			convertView = View.inflate(mContext, R.layout.item_condition2, null);
		String condition = dataList.optJSONObject(position).optString("title");
		final String id = String.valueOf(dataList.optJSONObject(position).optInt("id"));
		convertView.setTag(dataList.optJSONObject(position));

		TextView tv = (TextView) convertView.findViewById(R.id.tv_con);
		tv.setText(condition);
		tv.setTag(condition);
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mCallback.callBack(String.valueOf(arg0.getTag()), index, id);
				Log.i(TAG, "index = " + index + ",id = " + id);

			}
		});
		return convertView;
	}

	public interface Callback {
		void callBack(String condition, int index, String value_id);
	}

	public void refresh(String string) {
		try {
			mConditions = new JSONObject(string);

			dataList = mConditions.optJSONArray("Filter");
			index = mConditions.optInt("Index", -1);
		} catch (JSONException e) {

			try {
				dataList = new JSONArray(string);
				index = -1;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		this.notifyDataSetChanged();

	}

}
