package com.yaya.mobile.category.adapter;

import java.util.List;


import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaya.mobile.MyApplication;
import com.yaya.mobile.R;
import com.yaya.mobile.category.CommodityActivity_;
import com.yaya.mobile.category.bean.CategoryEntity;
import com.yaya.mobile.utils.Utils;
 
public class MainGridViewAdapter extends BaseAdapter {
	Context mContext;
	List<CategoryEntity> mCategorys;
	String mImgPath;
	int mPadding;

	public MainGridViewAdapter(Context context, List<CategoryEntity> categorys) {
		this.mContext = context;
		this.mCategorys = categorys;
		this.mImgPath = this.mContext.getString(R.string.url_get_image);  
		this.mPadding = Utils.dp2px(mContext, 20);
	}

	@Override
	public int getCount() {
		return mCategorys.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null || !(convertView instanceof LinearLayout))
			convertView = View.inflate(mContext, R.layout.item_category, null);
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null)
			holder = new ViewHolder(convertView);
		holder.fillData(convertView, position);

		return convertView;
	}

	class ViewHolder {
		public ViewHolder(View convertView) {
			iv = (ImageView) convertView.findViewById(R.id.iv);
			tv = (TextView) convertView.findViewById(R.id.tv);
			convertView.setTag(this);
		}

		ImageView iv;
		TextView tv;

		void fillData(View convertView, int position) {
			final CategoryEntity entity = mCategorys.get(position);
			tv.setText(entity.title);
			iv.setTag(entity.imgUrl);
			ImageLoader.getInstance().displayImage(entity.imgUrl, iv, MyApplication.getOptions(), MyApplication.getLoadingListener());

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					CommodityActivity_.intent(mContext).category_id(entity.categoryId).id_0(entity.id).start();
				}
			});
		}
	}

	public void refresh(List<CategoryEntity> categorys) {
		this.mCategorys = categorys;
		this.notifyDataSetChanged();
	}

}