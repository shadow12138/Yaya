package com.yaya.mobile.category;

import java.util.ArrayList;

import java.util.List;

import org.androidannotations.annotations.EFragment;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.yaya.mobile.R;
import com.yaya.mobile.category.adapter.MainGridViewAdapter;
import com.yaya.mobile.category.bean.CategoryEntity;
import com.yaya.mobile.net.ProgressMessage;
import com.yaya.mobile.net.RequestAdapter;
import com.yaya.mobile.net.RequestAdapter.RequestMethod;
import com.yaya.mobile.net.ResponseData;
import com.yaya.mobile.net.ResponseData.ResultState;
import com.yaya.mobile.ui.view.MyGridView;
import com.yaya.mobile.utils.Utils;

@EFragment
public class CategoryFragment extends Fragment implements OnClickListener, OnCheckedChangeListener {
	View rootView;
	Context mContext;
	MainGridViewAdapter adapter;
	View progress;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (null != rootView) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (null != parent) {
				parent.removeView(rootView);
			}
		} else {
			rootView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_category, null);
			mContext = getActivity();
			init();
		}
		return rootView;
	}

	RadioGroup rg;
	ScrollView scrollView;

	void init() {
		rg = (RadioGroup) rootView.findViewById(R.id.rg);
		scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
		progress = rootView.findViewById(R.id.progress);
		requestLeft();
	}

	void requestLeft() {
		progress.setVisibility(View.VISIBLE);
		new RequestAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onReponse(ResponseData data) {
				if (data.getResultState() == ResultState.eSuccess) {
					JSONObject mRootData = data.getMRootData();
					if (mRootData != null) {
						int status = mRootData.optInt("status");
						if (status == 1) {
							// 查询分类成功
							JSONArray menus = mRootData.optJSONArray("menus");
							fillCategory(menus);
						}
					}
				} else {
					toastMsg(data.getMsg(), 1);
					progress.setVisibility(View.GONE);
				}
			}

			@Override
			public void onProgress(ProgressMessage msg) {
				// TODO Auto-generated method stub

			}
		}.setUrl(getString(R.string.url_loadProductCategorys)).setRequestMethod(RequestMethod.eGet).notifyRequest();
	}

	void toastMsg(String msg, int time) {
		Toast.makeText(mContext, msg, time).show();
	}

	void fillCategory(JSONArray array) {
		LayoutParams params = new LayoutParams(-1, -2);
		int padding = Utils.dp2px(mContext, 10);
		int width = Utils.dp2px(mContext, 80);
		LinearLayout.LayoutParams textviewParams = new LinearLayout.LayoutParams(-2, -2);
		textviewParams.setMargins(padding, padding, padding, padding);
		for (int i = 0; i < array.length(); i++) {
			LinearLayout ll_category = new LinearLayout(mContext);
			ll_category.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			ll_category.setOrientation(LinearLayout.VERTICAL);
			JSONObject menuObject = array.optJSONObject(i);
			String title = menuObject.optString("title");
			JSONArray categoryList = menuObject.optJSONArray("right_parent");

			for (int j = 0; j < categoryList.length(); j++) {
				JSONObject categoryObject = categoryList.optJSONObject(j);
				String titleInParent = categoryObject.optString("title");
				TextView tv = new TextView(mContext);
				tv.setText(titleInParent);
				tv.setBackgroundResource(R.drawable.selector_order_unpaid);

				tv.setTextColor(Color.WHITE);
				ll_category.addView(tv, textviewParams);

				JSONArray childArray = categoryObject.optJSONArray("right_child");
				List<CategoryEntity> categorys = new ArrayList<CategoryEntity>();
				for (int k = 0; k < childArray.length(); k++) {
					JSONObject childObject = childArray.optJSONObject(k);
					String name = childObject.optString("title");
					String imgUrl = childObject.optString("thumb");
					int categoryId = childObject.optInt("category_id");
					int id = childObject.optInt("id_0");
					CategoryEntity categoryEntity = new CategoryEntity(imgUrl, categoryId, id, name, titleInParent);
					categorys.add(categoryEntity);
				}
				MyGridView gv = new MyGridView(mContext);
				gv.setAdapter(new MainGridViewAdapter(mContext, categorys));
				gv.setNumColumns(GridView.AUTO_FIT);
				gv.setColumnWidth(width);
				gv.setHorizontalSpacing(padding);
				gv.setVerticalSpacing(padding);
				ll_category.addView(gv);

			}
			RadioButton rb = new RadioButton(mContext);
			rb.setPadding(padding, padding, padding, padding);
			rb.setBackgroundResource(R.drawable.selector_rb_category);
			rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
			rb.setTag(ll_category);
			rb.setText(title);
			rb.setTextSize(14);
			rb.setTextColor(i == 0 ? getResources().getColor(R.color.maincolor) : getResources().getColor(R.color.black));
			rg.addView(rb, params);
		}
		rg.setOnCheckedChangeListener(this);
		RadioButton rb = (RadioButton) rg.getChildAt(0);
		rb.setChecked(true);
		progress.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		RadioButton checkedButton = (RadioButton) arg0.getChildAt(arg1 - arg0.getChildAt(0).getId());
		scrollView.removeAllViews();
		scrollView.addView((View) checkedButton.getTag());
		for (int i = 0; i < arg0.getChildCount(); i++) {
			RadioButton rb = (RadioButton) arg0.getChildAt(i);
			rb.setTextColor(Color.BLACK);
		}
		checkedButton.setTextColor(getResources().getColor(R.color.maincolor));

	}

}
