package com.yaya.mobile.html;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;

public class MyWebChromeClient extends WebChromeClient {
	MyWebChromeCallBack mCallback;

	public MyWebChromeClient(MyWebChromeCallBack callback) {
		this.mCallback = callback;
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		super.onProgressChanged(view, newProgress);
		mCallback.onProgressChanged(newProgress);
		if (newProgress >= 80) {
			mCallback.onLoadedHalf();
		}
	}

	@Override
	public void onReceivedTitle(WebView view, String title) {

	}

	public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

		final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

		builder.setTitle("对话框").setMessage(message).setPositiveButton("确定", null);

		builder.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				Log.v("onJsAlert", "keyCode==" + keyCode + "event=" + event);
				return true;
			}
		});
		builder.setCancelable(false);
		AlertDialog dialog = builder.create();
		dialog.show();
		result.confirm();
		return true;

	}

	public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
		return super.onJsBeforeUnload(view, url, message, result);
	}

	public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

		builder.setTitle("对话框").setMessage(message).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				result.confirm();
			}
		}).setNeutralButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				result.cancel();
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				result.cancel();
			}
		});

		builder.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				Log.v("onJsConfirm", "keyCode==" + keyCode + "event=" + event);
				return true;
			}
		});
		builder.setCancelable(false);
		AlertDialog dialog = builder.create();
		dialog.show();
		return true;

	}

	public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

		builder.setTitle("对话框").setMessage(message);

		final EditText et = new EditText(view.getContext());
		et.setSingleLine();
		et.setText(defaultValue);
		builder.setView(et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				result.confirm(et.getText().toString());
			}

		}).setNeutralButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				result.cancel();
			}
		});

		builder.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				Log.v("onJsPrompt", "keyCode==" + keyCode + "event=" + event);
				return true;
			}
		});

		builder.setCancelable(false);
		AlertDialog dialog = builder.create();
		dialog.show();
		return true;

	}

	public interface MyWebChromeCallBack {
		public void onLoadedHalf();

		public void onProgressChanged(int progress);
	}

}
