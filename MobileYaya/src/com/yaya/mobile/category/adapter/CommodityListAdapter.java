package com.yaya.mobile.category.adapter;

import org.json.JSONArray;


import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaya.mobile.MyApplication;
import com.yaya.mobile.R;

public class CommodityListAdapter extends BaseAdapter {
	JSONArray mArray;
	Context mContext;

	public CommodityListAdapter(Context context, JSONArray array) {
		this.mContext = context;
		this.mArray = array;
	}

	@Override
	public int getCount() {
		return mArray.length();
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
			convertView = View.inflate(mContext, R.layout.item_commodity, null);
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null)
			holder = new ViewHolder(convertView);
		holder.fillData(position);
		return convertView;
	}

	class ViewHolder {
		public ViewHolder(View convertView) {
			iv = (ImageView) convertView.findViewById(R.id.iv);
			tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			tv_price = (TextView) convertView.findViewById(R.id.tv_price);
			tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
			convertView.setTag(this);
		}

		ImageView iv;
		TextView tv_name;
		TextView tv_price;
		TextView tv_comment;

		void fillData(int position) {
			JSONObject mObject = mArray.optJSONObject(position);
			tv_name.setText(mObject.optString("title"));
			tv_price.setText("￥" + mObject.optDouble("seal_price"));
			int comment_count = mObject.optInt("comment_count");
			String hpl = mObject.optString("hpl");
			tv_comment.setText(hpl + "好评(" + comment_count + ")");
			String imgUrl = mObject.optString("thumb");
			iv.setTag(imgUrl);
			ImageLoader.getInstance().displayImage(imgUrl, iv, MyApplication.getOptions(), MyApplication.getLoadingListener());
		}
	}

	public void refresh(JSONArray array) {
		this.mArray = array;
		this.notifyDataSetChanged();
	}

}
