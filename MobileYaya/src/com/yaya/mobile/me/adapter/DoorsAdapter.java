package com.yaya.mobile.me.adapter;

import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONObject;

import com.yaya.mobile.R;
import com.yaya.mobile.me.DoorsDetailActivity_;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DoorsAdapter extends BaseAdapter {
	Context mContext;
	JSONArray mArray;
	DoorCallBack mCallback;
	JSONArray mDistance;
	String[] distances;
	DecimalFormat df;

	public interface DoorCallBack {
		public void send(String id);
	}

	public DoorsAdapter(Context context, JSONArray array, JSONArray distanceArray, DoorCallBack callBack) {
		this.mContext = context;
		this.mArray = array;
		this.mCallback = callBack;
		if (distanceArray != null) {
			this.distances = distanceArray.toString().split(",");
			distances[0] = distances[0].substring(1);
			distances[distances.length - 1] = distances[distances.length - 1].substring(0, (distances[distances.length - 1]).length() - 1);
		}
		df = new DecimalFormat("######0.00");
	}

	@Override
	public int getCount() {
		return mArray == null ? 0 : mArray.length();
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
			convertView = View.inflate(mContext, R.layout.item_doors, null);
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null)
			holder = new ViewHolder(convertView);
		holder.fillData(position);
		return convertView;
	}

	class ViewHolder {
		TextView tv_name;
		TextView tv_sell;
		TextView tv_experience;
		TextView tv_submit;
		TextView tv_repair;
		TextView tv_address;
		TextView sell_consult;
		TextView after_sell;
		TextView work_time;
		TextView tecent_consult;
		TextView tv_detail;
		TextView tv_send;
		TextView tv_distance;
		TextView tv_type;

		public ViewHolder(View convertView) {
			tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
			tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			tv_type = (TextView) convertView.findViewById(R.id.tv_type);
			tv_address = (TextView) convertView.findViewById(R.id.tv_address);
			sell_consult = (TextView) convertView.findViewById(R.id.sell_consult);
			after_sell = (TextView) convertView.findViewById(R.id.after_sell);
			work_time = (TextView) convertView.findViewById(R.id.work_time);
			tecent_consult = (TextView) convertView.findViewById(R.id.tecent_consult);
			tv_detail = (TextView) convertView.findViewById(R.id.tv_detail);
			tv_send = (TextView) convertView.findViewById(R.id.tv_send);
			tv_distance.setVisibility(distances == null ? View.GONE : View.VISIBLE);
			convertView.setTag(this);
		}

		void fillData(int position) {
			if (distances != null) {
				double distance = Double.parseDouble(distances[position]) / 1000;
				tv_distance.setText(df.format(distance) + "km");
			}
			final JSONObject object = mArray.optJSONObject(position);
			tv_name.setText(object.optString("title"));
			// tv_distance.setText
			tv_address.setText(object.optString("address"));
			sell_consult.setText("销售热线：" + object.optString("tel"));
			after_sell.setText("售后热线：" + object.optString("tel_zx"));
			work_time.setText(object.optString("worktime"));
			tecent_consult.setText("联系QQ：" + object.optString("qq"));
			tv_type.setText(object.optString("service_type"));
			tv_send.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String id = object.optString("id");
					mCallback.send(id);
				}
			});
			tv_detail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					DoorsDetailActivity_.intent(mContext).extra(object.toString()).start();
				}
			});

		}
	}

	public void refresh(JSONArray array, JSONArray distanceList) {
		this.mArray = array;
		this.mDistance = distanceList;
		this.notifyDataSetInvalidated();
	}

}
