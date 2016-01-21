package com.yaya.mobile.category;

import org.androidannotations.annotations.AfterInject;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.yaya.mobile.MainActionBarActivity;
import com.yaya.mobile.R;
import com.yaya.mobile.ui.view.RadioLabelGroup;
import com.yaya.mobile.utils.Utils;

@EActivity(R.layout.condition)
public class ConditionActivity extends MainActionBarActivity implements OnCheckedChangeListener {

	@Extra
	String json;
	@Extra
	String[] results;
	JSONObject conditionsJson;

	@ViewById(R.id.ll)
	LinearLayout ll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@AfterInject
	void initData() {
		try {
			conditionsJson = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@AfterViews
	void initUI() {
		backButton.setText("筛选");
		JSONArray filterData = conditionsJson.optJSONArray("filterData");
		int titlePadding = Utils.dp2px(this, 10);

		JSONArray priceArray = conditionsJson.optJSONArray("priceData");
		int has_price = conditionsJson.optInt("has_price");
		if (has_price == 1 && priceArray != null && priceArray.length() > 0) {
			TextView tv = new TextView(this);
			tv.setText("价格");
			tv.setTextSize(14);
			tv.setPadding(0, 0, 0, titlePadding);
			tv.setTextColor(getResources().getColor(R.color.maincolor_dark));
			ll.addView(tv);
			RadioLabelGroup rg = new RadioLabelGroup(this);
			rg.setTag(0);
			RadioButton rb1 = new RadioButton(this);
			rb1.setBackgroundResource(R.drawable.selector_rb_condition);
			rb1.setButtonDrawable(android.R.color.transparent);
			rb1.setText("不限");
			rb1.setTextColor(Color.BLACK);
			rb1.setTag(0);
			rb1.setTextSize(12);
			rg.addView(rb1);
			String indexId = results[0];
			int checkedPosition = -1;
			for (int i = 0; i < priceArray.length(); i++) {
				JSONObject priceObject = priceArray.optJSONObject(i);
				int valueId = priceObject.optInt("id");
				RadioButton rb = new RadioButton(this);
				rb.setTag("0," + valueId);
				rb.setBackgroundResource(R.drawable.selector_rb_condition);
				rb.setButtonDrawable(android.R.color.transparent);
				rb.setText(priceObject.optString("title"));
				rb.setTextColor(Color.BLACK);
				rb.setTextSize(12);
				rg.addView(rb);
				if (!TextUtils.isEmpty(indexId) && Integer.parseInt(indexId) == valueId) {
					checkedPosition = i + 1;
				}
			}
			rg.setOnCheckedChangeListener(this);
			ll.addView(rg);
			if (TextUtils.isEmpty(results[0]) || checkedPosition == -1)
				((RadioButton) rg.getChildAt(0)).setChecked(true);
			else
				((RadioButton) rg.getChildAt(checkedPosition)).setChecked(true);
		}
		for (int i = 0; i < filterData.length(); i++) {
			JSONObject filterObject = filterData.optJSONObject(i);
			int index = filterObject.optInt("Index") + 1;
			int checkedPosition = -1;
			TextView tv = new TextView(this);
			tv.setText(filterObject.optString("Title"));
			tv.setTextSize(14);
			tv.setPadding(0, titlePadding, 0, titlePadding);
			tv.setTextColor(getResources().getColor(R.color.maincolor_dark));
			ll.addView(tv);
			JSONArray filterArray = filterObject.optJSONArray("Filter");
			RadioLabelGroup lg = new RadioLabelGroup(this);
			RadioButton rb1 = new RadioButton(this);
			rb1.setBackgroundResource(R.drawable.selector_rb_condition);
			rb1.setButtonDrawable(android.R.color.transparent);
			rb1.setText("不限");
			rb1.setTextSize(12);
			rb1.setTextColor(Color.BLACK);
			rb1.setTag(index);

			lg.addView(rb1);
			lg.setTag(0);
			String indexId = results[index];
			for (int j = 0; j < filterArray.length(); j++) {
				JSONObject filter = filterArray.optJSONObject(j);
				int id = filter.optInt("id");
				RadioButton rb = new RadioButton(this);
				rb.setBackgroundResource(R.drawable.selector_rb_condition);
				rb.setButtonDrawable(android.R.color.transparent);
				rb.setText(filter.optString("title"));
				rb.setTextColor(Color.BLACK);
				rb.setTextSize(12);
				rb.setTag(index + "," + id);
				lg.addView(rb);
				if (!TextUtils.isEmpty(indexId) && Integer.parseInt(indexId) == id) {
					checkedPosition = j + 1;
				}
			}
			lg.setOnCheckedChangeListener(this);
			ll.addView(lg);
			if (TextUtils.isEmpty(results[index]) || checkedPosition == -1)
				((RadioButton) lg.getChildAt(0)).setChecked(true);
			else
				((RadioButton) lg.getChildAt(checkedPosition)).setChecked(true);
		}

	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		RadioButton rb = (RadioButton) findViewById(arg1);
		int isFirst = (Integer) arg0.getTag();
		if (rb.getText().toString().equals("不限")) {
			int index = (Integer) rb.getTag();
			results[index] = "";
		} else {
			String[] result = ((String) rb.getTag()).split(",");
			results[Integer.parseInt(result[0])] = result[1];
		}
		if (isFirst == 0) {
			arg0.setTag(1);
		} else {
			onBackPressed();
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra("conditions", results);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void backButtonClick(View v) {
		onBackPressed();
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
