package com.yaya.mobile.me.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaya.mobile.R;

public class MeGridViewAdapter extends BaseAdapter {
	Context mContext;
	String[] mLabels;
	int[] mIcons = { R.drawable.member01, R.drawable.member03, R.drawable.member09, R.drawable.member04, R.drawable.member05, R.drawable.member06, R.drawable.member07, R.drawable.member08,
			R.drawable.member11, R.drawable.member10, R.drawable.member02, R.drawable.member12 };

	public MeGridViewAdapter(Context context) {
		this.mContext = context;
		mLabels = mContext.getResources().getStringArray(R.array.me_labels);
	}

	@Override
	public int getCount() {
		return mLabels.length;
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
			convertView = View.inflate(mContext, R.layout.me_labels, null);
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null)
			holder = new ViewHolder(convertView);
		holder.fillData(position);
		return convertView;
	}

	class ViewHolder {
		public ViewHolder(View convertView) {
			tv = (TextView) convertView.findViewById(R.id.tv);
			iv = (ImageView) convertView.findViewById(R.id.iv);
			convertView.setTag(this);
		}

		TextView tv;
		ImageView iv;

		void fillData(int position) {
			String text = mLabels[position];
			int icon = mIcons[position];

			tv.setText(text);
			iv.setImageResource(icon);
		}
	}

}
