package com.yaya.mobile;

import java.util.ArrayList;

import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yaya.mobile.adapter.SplashPagerAdapter;
import com.yaya.mobile.adapter.SplashPagerChangedListener;
import com.yaya.mobile.net.ProgressMessage;
import com.yaya.mobile.net.RequestAdapter;
import com.yaya.mobile.net.RequestAdapter.RequestContentType;
import com.yaya.mobile.net.RequestAdapter.RequestMethod;
import com.yaya.mobile.net.ResponseData;
import com.yaya.mobile.net.ResponseData.ResultState;
import com.yaya.mobile.utils.NetworkUtils;
import com.yaya.mobile.utils.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

@EActivity(R.layout.activity_splash)
public class SplashActivity extends Activity {
	AlphaAnimation aa;
	@ViewById(R.id.iv)
	ImageView iv;
	@ViewById(R.id.vp)
	ViewPager vp;
	SharedPreferences sp;
	@ViewById(R.id.ll)
	LinearLayout ll;
	@ViewById(R.id.introduce)
	View introduce;
	@ViewById(R.id.guide)
	View guide;
	@ViewById(R.id.tv_go)
	TextView tv_go;
	boolean isGuideNeeded = true;
	boolean isAnimationEnd = true;

	@AfterViews
	void initUI() {
		aa = new AlphaAnimation(0, 1);
		aa.setDuration(3000);
		aa.setAnimationListener(new MyAnimationListener());

		sp = getSharedPreferences("yaya_mobile.pref", MODE_PRIVATE);
		isGuideNeeded = sp.getBoolean("isGuideNeeded", true);
		guide.setVisibility(isGuideNeeded ? View.VISIBLE : View.GONE);

		if (NetworkUtils.isNetworkAvailable(this)) {
			requestImage();
		} else {
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					MainActivity_.intent(SplashActivity.this).start();
				}
			}, 2000);
		}
	}

	void requestGuide() {
		String json = "{\"source\":\"android\"}";
		new RequestAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onReponse(ResponseData data) {
				if (data.getResultState() == ResultState.eSuccess) {
					JSONObject mRootData = data.getMRootData();
					if (mRootData != null) {
						initImage(mRootData);
					} else {
						if (isAnimationEnd)
							runFinish();
					}
				} else {
					runFinish();
				}
			}

			@Override
			public void onProgress(ProgressMessage msg) {

			}
		}.setTimeOutLimit(5000).setUrl(getString(R.string.url_yindao)).setJSON(json).setRequestContentType(RequestContentType.eJSON).setRequestMethod(RequestMethod.ePost).notifyRequest();
	}

	void requestImage() {
		String json = "{\"source\":\"android\"}";
		new RequestAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onReponse(ResponseData data) {
				if (data.getResultState() == ResultState.eSuccess) {
					JSONObject mRootData = data.getMRootData();
					boolean status = mRootData.optInt("status") == 1;
					if (status) {
						String img_url = mRootData.optString("img_url");
						iv.setScaleType(ScaleType.CENTER_CROP);
						ImageLoader.getInstance().displayImage(img_url, iv, new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri, View view) {
								if (isGuideNeeded)
									requestGuide();
							}

							@Override
							public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
								runFinish();
							}

							@Override
							public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
								iv.setAnimation(aa);
								aa.start();
							}

							@Override
							public void onLoadingCancelled(String imageUri, View view) {

							}
						});
					}
				} else {
					runFinish();
				}
			}

			@Override
			public void onProgress(ProgressMessage msg) {

			}
		}.setTimeOutLimit(6000).setJSON(json).setUrl(getString(R.string.url_introduce)).setRequestContentType(RequestContentType.eJSON).setRequestMethod(RequestMethod.ePost).notifyRequest();
	}

	void runFinish() {
		MainActivity_.intent(SplashActivity.this).start();
		finish();
	}

	void initImage(JSONObject mRootData) {
		int dp = Utils.dp2px(this, 5);

		List<String> urls = new ArrayList<String>();
		JSONObject imageObject = mRootData.optJSONObject("guidscreen");
		String pic1 = imageObject.optString("pic1");
		if (!TextUtils.isEmpty(pic1))
			urls.add(pic1);

		String pic2 = imageObject.optString("pic2");
		if (!TextUtils.isEmpty(pic2))
			urls.add(pic2);

		String pic3 = imageObject.optString("pic3");
		if (!TextUtils.isEmpty(pic3))
			urls.add(pic3);

		String pic4 = imageObject.optString("pic4");
		if (!TextUtils.isEmpty(pic4))
			urls.add(pic4);

		String pic5 = imageObject.optString("pic5");
		if (!TextUtils.isEmpty(pic5))
			urls.add(pic5);

		if (urls.size() == 0) {
			if (isAnimationEnd)
				runFinish();
		}

		for (int i = 0; i < urls.size(); i++) {
			ImageView iv = new ImageView(this);
			LayoutParams params = new LayoutParams(-2, -2);
			params.setMargins(dp, dp, dp, dp);
			iv.setLayoutParams(params);
			iv.setImageResource(i == 0 ? R.drawable.page_focused : R.drawable.page_unfocused);
			ll.addView(iv);
		}
		vp.setOnPageChangeListener(new SplashPagerChangedListener(ll, tv_go));

		ImageView[] imgs = new ImageView[urls.size()];
		for (int i = 0; i < imgs.length; i++) {
			ImageView img = new ImageView(this);
			img.setScaleType(ScaleType.CENTER_CROP);
			img.setBackgroundResource(R.color.maincolor);
			ImageLoader.getInstance().displayImage(urls.get(i), img);
			ImageLoader.getInstance().displayImage(urls.get(i), img, new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String imageUri, View view) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					introduce.setVisibility(View.GONE);

				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					introduce.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					// TODO Auto-generated method stub

				}
			});
			imgs[i] = img;
		}
		vp.setAdapter(new SplashPagerAdapter(imgs));

		tv_go.setVisibility(urls.size() == 1 ? View.VISIBLE : View.GONE);

		tv_go.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Editor editor = sp.edit();
				editor.putBoolean("isGuideNeeded", false);
				editor.commit();

				MainActivity_.intent(SplashActivity.this).start();
				finish();
			}
		});
	}

	class MyAnimationListener implements AnimationListener {

		@Override
		public void onAnimationStart(Animation animation) {
			isAnimationEnd = false;
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			isAnimationEnd = true;
			if (!isGuideNeeded)
				runFinish();
			else
				iv.setVisibility(View.GONE);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

	}

}
