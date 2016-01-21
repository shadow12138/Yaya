package com.yaya.mobile.category;

import org.androidannotations.annotations.AfterInject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import com.yaya.mobile.R;
import com.yaya.mobile.SearchActionBarActivity;
import com.yaya.mobile.category.adapter.CommodityListAdapter;
import com.yaya.mobile.html.WebViewActivity_;
import com.yaya.mobile.html.adapter.WindowRadioCheckChangedListener;
import com.yaya.mobile.net.ProgressMessage;
import com.yaya.mobile.net.RequestAdapter;
import com.yaya.mobile.net.RequestAdapter.RequestContentType;
import com.yaya.mobile.net.RequestAdapter.RequestMethod;
import com.yaya.mobile.net.ResponseData;
import com.yaya.mobile.net.ResponseData.ResultState;
import com.yaya.mobile.ui.view.MyGridView;
import com.yaya.mobile.ui.view.ObservableScrollView;
import com.yaya.mobile.ui.view.ObservableScrollView.ScrollViewListener;
import com.yaya.mobile.utils.Utils;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

@EActivity(R.layout.activity_commodity)
public class CommodityActivity extends SearchActionBarActivity implements ScrollViewListener {
	@Extra
	int category_id;
	@Extra
	int id_0;
	@ViewById(R.id.gv)
	MyGridView gv;
	@ViewById(R.id.rg)
	RadioGroup rg;
	@ViewById(R.id.progress)
	View progress;
	@ViewById(R.id.scrollView)
	ScrollView scrollView;
	@ViewById(R.id.scrollView)
	ObservableScrollView os;
	@ViewById(R.id.iv_empty)
	ImageView iv_empty;
	@ViewById(R.id.et_search)
	EditText et_search;
	private JSONObject conditionsJson;
	CommodityListAdapter adapter;
	JSONArray commodityArray;
	int page = 1;
	String conditions[] = new String[100];
	int sort = 0;
	boolean isSearched = false;
	String keyword;
	PopupWindow popupWindow;
	RadioGroup rg_window;
	@ViewById(R.id.rightBtn)
	Button rightBtn;
	@ViewById(R.id.rb_sell)
	RadioButton rb_sell;
	@ViewById(R.id.rb_price)
	RadioButton rb_price;

	@Click(R.id.leftBtn)
	void back() {
		finish();
	}

	@Click(R.id.rightBtn)
	void showMenu() {
		if (popupWindow == null) {
			View view = View.inflate(this, R.layout.dropdown_menu, null);
			popupWindow = new PopupWindow(view, -1, -2);
			popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			int drawabeles[] = { R.drawable.home, R.drawable.category, R.drawable.member, R.drawable.cart, R.drawable.doors_icon };
			rg_window = (RadioGroup) view.findViewById(R.id.rg);
			int bounds = Utils.dp2px(this, 20);
			for (int i = 0; i < rg_window.getChildCount(); i++) {
				RadioButton rb = (RadioButton) rg_window.getChildAt(i);
				rb.setTag(i);
				Drawable drawableTop = getResources().getDrawable(drawabeles[i]);
				drawableTop.setBounds(0, 0, bounds, bounds);
				rb.setCompoundDrawables(null, drawableTop, null, null);
			}
			rg_window.setOnCheckedChangeListener(new WindowRadioCheckChangedListener(this, popupWindow));
		}
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			popupWindow.showAsDropDown(rightBtn);

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (popupWindow != null) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	void search() {
		keyword = et_search.getText().toString().trim();
		if (TextUtils.isEmpty(keyword)) {
			toastMsg("搜索内容不能为空", 1);
			return;
		}
		requestSearch(sort, true);
	}

	void requestSearch(int sort, final boolean clear) {
		progress.setVisibility(View.VISIBLE);
		if (!isSearched) {
			isSearched = true;
			page = 1;
		}
		String json = "{\"page\":" + page + ",\"keyword\":\"" + keyword + "\"" + ",\"order_id\":" + sort + "}";
		String price_id = conditions[0];
		if (!TextUtils.isEmpty(price_id)) {
			StringBuilder sb = new StringBuilder(json);
			sb.deleteCharAt(sb.length() - 1);
			sb.append(",\"price_id\":" + price_id + "}");
			json = sb.toString();
		}
		requestSearchCondition();

		new RequestAdapter() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onReponse(ResponseData data) {
				progress.setVisibility(View.GONE);
				if (data.getResultState() == ResultState.eSuccess) {
					JSONObject mRootData = data.getMRootData();
					if (mRootData != null) {
						maxPage = mRootData.optInt("maxPage");
						if (maxPage < page) {
							progress.setVisibility(View.GONE);
							toastMsg("木有啦", 0);
							return;
						}
						JSONArray array = mRootData.optJSONArray("productData");
						if (clear)
							commodityArray = array;
						else {
							for (int i = 0; i < array.length(); i++) {
								JSONObject object = array.optJSONObject(i);
								commodityArray.put(object);
							}
						}
						if (commodityArray != null)
							fillData(commodityArray);

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
		}.setJSON(json).setUrl(getString(R.string.url_search)).setRequestContentType(RequestContentType.eJSON).setRequestMethod(RequestMethod.ePost).notifyRequest();
	}

	void requestSearchCondition() {
		new RequestAdapter() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onReponse(ResponseData data) {
				if (data.getResultState() == ResultState.eSuccess) {
					conditionsJson = data.getMRootData();
				}

			}

			@Override
			public void onProgress(ProgressMessage msg) {
				// TODO Auto-generated method stub

			}
		}.setUrl(getString(R.string.url_search_filter)).setRequestMethod(RequestMethod.eGet).notifyRequest();
	}

	@Click(R.id.rb_sell)
	void sellSort() {
		sort = sort == 0 ? 1 : 0;
		Drawable drawableRight = sort == 0 ? getResources().getDrawable(R.drawable.selector_rb_desc) : getResources().getDrawable(R.drawable.selector_rb_asc);
		drawableRight.setBounds(0, 0, drawableRight.getIntrinsicWidth(), drawableRight.getIntrinsicHeight());
		rb_sell.setCompoundDrawables(null, null, drawableRight, null);
		if (isSearched)
			requestSearch(sort, true);
		else
			requestCommodity(conditions, page, sort, true);
	}

	@Click(R.id.rb_price)
	void priceSort() {
		sort = sort == 2 ? 3 : 2;
		Drawable drawableRight = sort == 2 ? getResources().getDrawable(R.drawable.selector_rb_desc) : getResources().getDrawable(R.drawable.selector_rb_asc);
		drawableRight.setBounds(0, 0, drawableRight.getIntrinsicWidth(), drawableRight.getIntrinsicHeight());
		rb_price.setCompoundDrawables(null, null, drawableRight, null);
		if (isSearched)
			requestSearch(sort, true);
		else
			requestCommodity(conditions, page, sort, true);
	}

	@Click(R.id.rb_condition)
	void conditionSort() {
		if (conditionsJson == null) {
			Toast t = Toast.makeText(CommodityActivity.this, "筛选条件为空，请重试", Toast.LENGTH_LONG);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
			return;
		}

		// ConditionDialog_.intent(CommodityActivity.this).json(conditionsJson.toString()).startForResult(0);
		ConditionActivity_.intent(CommodityActivity.this).json(conditionsJson.toString()).results(conditions).startForResult(0);
	}

	@AfterInject
	void initData() {
		requestConditions();
	}

	@AfterViews
	void initUI() {
		et_search.setImeActionLabel("搜索", EditorInfo.IME_ACTION_SEARCH);
		et_search.setSingleLine();
		et_search.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		et_search.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					search();
					return true;
				}
				return false;
			}
		});
		RadioButton rb = (RadioButton) rg.getChildAt(0);
		rb.setChecked(true);
		conditions[1] = id_0 + "";
		requestCommodity(conditions, 1, sort, true);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				JSONObject commodityObject = commodityArray.optJSONObject(arg2);
				String url = commodityObject.optString("url") + "?u=&p=&s=1";
				String title = commodityObject.optString("title");
				WebViewActivity_.intent(CommodityActivity.this).title(title).main_url(url).start();

			}
		});

		os.setScrollViewListener(this);

	}

	private void requestConditions() {
		String json = "{\"category_id\":" + category_id + "}";
		new RequestAdapter() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onReponse(ResponseData data) {

				if (data.getResultState() == ResultState.eSuccess) {
					conditionsJson = data.getMRootData();
				}
			}

			@Override
			public void onProgress(ProgressMessage msg) {

			}
		}.setUrl(getString(R.string.url_loadProductCategoryFilters)).setRequestContentType(RequestContentType.eJSON).setRequestMethod(RequestMethod.ePost).setJSON(json).notifyRequest();

	}

	int maxPage = -1;

	void requestCommodity(String[] conditions, int page, int sort, final boolean clear) {
		progress.setVisibility(View.VISIBLE);
		if (isSearched) {
			isSearched = false;
			page = 1;
		}
		String json = "{\"category_id\":" + category_id + ",\"page\":" + page + ",\"order_id\":" + sort + "}";
		if (conditions != null) {
			StringBuilder sb = new StringBuilder(json);
			sb.deleteCharAt(sb.length() - 1);
			sb.append(",");
			if (!TextUtils.isEmpty(conditions[0])) {
				sb.append("\"price_id\":" + conditions[0] + ",");
			}
			for (int i = 1; i < conditions.length; i++) {
				String condition = conditions[i];
				if (!TextUtils.isEmpty(condition))
					sb.append("\"id_" + (i - 1) + "\":" + condition + ",");

			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("}");
			json = sb.toString();
		}
		isSearched = false;
		new RequestAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onReponse(ResponseData data) {
				String msg = data.getMsg();
				if (data.getResultState() == ResultState.eSuccess) {
					JSONObject object = data.getMRootData();
					if (object != null) {
						maxPage = object.optInt("maxPage");
						if (maxPage < CommodityActivity.this.page) {
							progress.setVisibility(View.GONE);
							toastMsg("木有啦", 0);
							return;
						}
						JSONArray array = object.optJSONArray("productData");
						if (!clear) {
							for (int i = 0; i < array.length(); i++)
								commodityArray.put(array.optJSONObject(i));
						} else {
							commodityArray = array;
						}
						if (array != null) {
							iv_empty.setVisibility(commodityArray.length() == 0 ? View.VISIBLE : View.GONE);
							progress.setVisibility(array.length() == 0 ? View.GONE : View.VISIBLE);
							if (array.length() == 0 && commodityArray.length() != 0) {
								toastMsg("木有啦", 1);
							} else
								fillData(commodityArray);
						} else {
							toastMsg("没有找到所需商品，请重试", 1);
							progress.setVisibility(View.GONE);
						}

					}
				} else {
					Toast.makeText(CommodityActivity.this, msg, Toast.LENGTH_SHORT).show();
				}

			}

			@Override
			public void onProgress(ProgressMessage msg) {

			}
		}.setUrl(getString(R.string.url_loadProductsPageData)).setRequestMethod(RequestMethod.ePost).setRequestContentType(RequestContentType.eJSON).setJSON(json).notifyRequest();
	}

	int scrollViewHeight = 0;

	void fillData(JSONArray array) {
		scrollViewHeight = 0;
		if (adapter == null) {
			adapter = new CommodityListAdapter(this, array);
			gv.setAdapter(adapter);
		} else {
			adapter.refresh(array);
		}
		iv_empty.setVisibility(array.length() == 0 ? View.VISIBLE : View.GONE);
		progress.setVisibility(View.GONE);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < os.getChildCount(); i++)
					scrollViewHeight += os.getChildAt(i).getMeasuredHeight();
			}
		}, 3000);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			conditions = data.getStringArrayExtra("conditions");
			page = 1;
			if (isSearched)
				requestSearch(sort, true);
			else
				requestCommodity(conditions, page, sort, true);
		} else if (resultCode == RESULT_FIRST_USER) {
			conditions = null;
			page = 1;
			if (isSearched)
				requestSearch(sort, true);
			else
				requestCommodity(conditions, page, sort, true);
		}
	}

	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
		int position = y + scrollView.getMeasuredHeight();
		if (position == scrollViewHeight && progress.getVisibility() == View.GONE) {
			page++;
			if (isSearched)
				requestSearch(sort, false);
			else
				requestCommodity(conditions, page, sort, false);

		}

	}

	@Override
	public void onScrollBotton() {
		/*
		 * if (progress.getVisibility() == View.GONE) { page++; if (isSearched)
		 * requestSearch(sort, false); else requestCommodity(conditions, page,
		 * sort, false);
		 * 
		 * }
		 */
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
