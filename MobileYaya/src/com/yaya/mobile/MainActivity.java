package com.yaya.mobile;

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import com.yaya.mobile.adapter.MainPagerAdapter;
import com.yaya.mobile.adapter.MainPagerChangedListener;
import com.yaya.mobile.adapter.MainRadioGroupCheckChangedListener;
import com.yaya.mobile.cart.CartFragment_;
import com.yaya.mobile.category.CategoryFragment_;
import com.yaya.mobile.home.HomeFragment_;
import com.yaya.mobile.me.DoorsActivity_;
import com.yaya.mobile.me.MeFragment_;
import com.yaya.mobile.net.ProgressMessage;
import com.yaya.mobile.net.RequestAdapter;
import com.yaya.mobile.net.RequestAdapter.RequestMethod;
import com.yaya.mobile.net.ResponseData;
import com.yaya.mobile.net.ResponseData.ResultState;
import com.yaya.mobile.ui.view.CustomViewPager;
import com.yaya.mobile.utils.NetworkUtils;
import com.yaya.mobile.utils.Utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.LayoutParams;
import android.widget.Toast;

@EActivity(R.layout.activity_main)
public class MainActivity extends FragmentActivity {
	@ViewById(R.id.vp)
	CustomViewPager vp;
	@ViewById(R.id.rg)
	RadioGroup rg;
	@ViewById(R.id.pb)
	View progress;
	@ViewById(R.id.network)
	View wifi;

	List<Fragment> mFragments = new ArrayList<Fragment>();
	long exitTime;

	void requestUpdate() {
		new RequestAdapter() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onReponse(ResponseData data) {
				if (data.getResultState() == ResultState.eSuccess) {
					JSONObject mRootData = data.getMRootData();
					if (mRootData != null) {
						boolean status = mRootData.optInt("status") == 1;
						if (status) {
							JSONObject versionObject = mRootData.optJSONObject("androidVersion");
							String version = versionObject.optString("version");
							String url = versionObject.optString("url");
							Log.i("TAG", url);
							PackageManager pm = getPackageManager();
							try {
								PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
								int versionCode = packageInfo.versionCode;
								if (versionCode < Integer.parseInt(version)) {
									// 有版本更新
									update(url);
								}
							} catch (NameNotFoundException e) {
								e.printStackTrace();
							}
						} else {
							String msg = mRootData.optString("msg");
							Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
						}
					}
				} else {
					Toast.makeText(MainActivity.this, "获取更新信息失败了>-<", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onProgress(ProgressMessage msg) {
				// TODO Auto-generated method stub

			}
		}.setUrl(getString(R.string.url_update)).setRequestMethod(RequestMethod.eGet).notifyRequest();
	}

	AlertDialog dialog;

	void update(final String url) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("报告，检测到新版本啦！");
		builder.setPositiveButton("去下载", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// 下载新版本
				final String downloadUrl = url;
				final ProgressDialog pd; // 进度条对话框
				pd = new ProgressDialog(MainActivity.this);
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.setMessage("正在下载更新");
				pd.show();
				pd.setCancelable(false);
				new Thread() {
					@Override
					public void run() {
						try {
							File file = getFileFromServer(downloadUrl, pd);
							installApk(file);
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									pd.dismiss();// 结束掉进度条对话框

								}
							});
						} catch (Exception e) {
							e.printStackTrace();
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									pd.dismiss();
									Toast.makeText(MainActivity.this, "啊哦，下载更新出错了，请重试", Toast.LENGTH_LONG).show();

								}
							});
						}
					}
				}.start();

			}
		});
		builder.setNegativeButton("不要", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				dialog.dismiss();
			}
		});

		builder.setCancelable(false);

		dialog = builder.show();
	}

	protected void installApk(File file) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		startActivity(intent);
	}

	private static File getFileFromServer(String path, ProgressDialog pd) throws Exception {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			// 获取到文件的大小
			pd.setMax(conn.getContentLength());
			InputStream is = conn.getInputStream();
			File file = new File(Environment.getExternalStorageDirectory(), "updata.apk");
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				total += len;
				// 获取当前下载量
				pd.setProgress(total);
			}
			fos.close();
			bis.close();
			is.close();
			return file;
		} else {
			return null;
		}
	}

	MyBroadcastReceiver receiver;

	@AfterViews
	void registerNetwork() {
		vp.setPagingEnabled(false);
		if (NetworkUtils.isNetworkAvailable(this)) {
			initUI();
		} else {
			wifi.setVisibility(View.VISIBLE);
		}
	}

	public void reload(View v) {
		if (NetworkUtils.isNetworkAvailable(this)) {
			wifi.setVisibility(View.GONE);
			initUI();
		}
	}

	void initUI() {
		requestUpdate();
		requestFooter();
		mFragments.add(new HomeFragment_());
		mFragments.add(new CategoryFragment_());
		mFragments.add(new MeFragment_());
		mFragments.add(new CartFragment_());
		vp.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), mFragments));
		vp.setCurrentItem(0);

		vp.setOnPageChangeListener(new MainPagerChangedListener(rg));
		rg.setOnCheckedChangeListener(new MainRadioGroupCheckChangedListener(vp, this));

		// 注册广播
		receiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter("com.yaya.mobile.skip_page");
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		this.registerReceiver(receiver, filter);

	}

	class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int action = intent.getIntExtra("page", 0);
			if (action == 4)
				DoorsActivity_.intent(MainActivity.this).page(1).start();
			else
				vp.setCurrentItem(action);
		}

	}

	void requestFooter() {
		progress.setVisibility(View.VISIBLE);
		new RequestAdapter() {

			private static final long serialVersionUID = 1L;

			@Override
			public void onReponse(ResponseData data) {
				if (data.getResultState() == ResultState.eSuccess) {
					JSONObject mRootData = data.getMRootData();
					if (mRootData != null) {
						boolean status = mRootData.optInt("status") == 1;
						if (status) {
							JSONArray array = mRootData.optJSONArray("buttonList");
							if (rg.getChildCount() == 0)
								initButtons(array);
						} else {
							progress.setVisibility(View.GONE);
							String msg = mRootData.optString("msg");
							Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
						}
					}
				} else {
					progress.setVisibility(View.GONE);
					Toast.makeText(MainActivity.this, "获取底部菜单失败了>-<", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onProgress(ProgressMessage msg) {
				// TODO Auto-generated method stub

			}
		}.setUrl(getString(R.string.url_footer_button)).setRequestMethod(RequestMethod.eGet).notifyRequest();
	}

	void initButtons(final JSONArray array) {
		if (array == null)
			return;
		final int bound = Utils.dp2px(this, 20);
		final int drawablePadding = Utils.dp2px(this, 2);
		LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
		for (int i = 0; i < array.length(); i++) {

			JSONObject object = array.optJSONObject(i);
			final String icon = object.optString("icon");
			String title = object.optString("title");
			final RadioButton rb = new RadioButton(this);

			rg.addView(rb, params);
			rb.setGravity(Gravity.CENTER);
			rb.setCompoundDrawablePadding(drawablePadding);
			rb.setText(title);
			rb.setPadding(drawablePadding, drawablePadding, drawablePadding, drawablePadding);
			rb.setBackgroundResource(R.drawable.selector_rb_category);
			rb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
			rb.setTag(i);
			rb.setTextSize(12);
			new Thread() {
				public void run() {
					try {
						final Drawable drawableTop = Drawable.createFromStream(new URL(icon).openStream(), "radiobutton.png");
						drawableTop.setBounds(0, 0, bound, bound);

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								rb.setCompoundDrawables(null, drawableTop, null, null);
								if (rg.getChildCount() == array.length()) {
									progress.setVisibility(View.GONE);
									RadioButton radioButton = (RadioButton) rg.getChildAt(0);
									radioButton.setChecked(true);
									radioButton.setOnClickListener(new View.OnClickListener() {

										@Override
										public void onClick(View v) {

											HomeFragment_ homeFragment = (HomeFragment_) mFragments.get(0);
											String url = homeFragment.wv.getUrl();
											if (!url.startsWith("http://myaya.ynyes.net/index.html?u="))
												homeFragment.init();
										}

									});
								}
							}
						});

					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
			}.start();
		}
	}

	@Override
	public void onBackPressed() {
		exit();
	}

	void exit() {
		boolean canExit = true;
		if (vp.getCurrentItem() == 0) {
			HomeFragment_ homeFragment = (HomeFragment_) mFragments.get(0);
			WebView wv = homeFragment.wv;
			if (wv != null && wv.canGoBack()) {
				wv.goBack();
				canExit = false;
			} else
				canExit = true;
		} else if (vp.getCurrentItem() == 3) {
			CartFragment_ cartFragment = (CartFragment_) mFragments.get(3);
			WebView wv = cartFragment.wv;
			if (wv != null && wv.canGoBack()) {
				wv.goBack();
				canExit = false;
			} else
				canExit = true;
		}
		if (canExit) {
			if (System.currentTimeMillis() - exitTime > 2000) {
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				System.exit(0);
			}
		}
	}

	public void skipTo(int page) {
		vp.setCurrentItem(page);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
		if (receiver != null) {
			this.unregisterReceiver(receiver);
			receiver = null;
		}
	}

}
