package com.yaya.mobile.ui.view;

import android.content.Context;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView {

	private ScrollViewListener scrollViewListener = null;

	public ObservableScrollView(Context context) {
		super(context);
	}

	public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ObservableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScrollViewListener(ScrollViewListener scrollViewListener) {
		this.scrollViewListener = scrollViewListener;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);
		if (scrollViewListener != null) {
			scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			int offset = computeVerticalScrollOffset() + this.getMeasuredHeight();
			int sumHeight = 0;
			for (int i = 0; i < this.getChildCount(); i++) {
				sumHeight += this.getChildAt(i).getMeasuredHeight();
			}
			if (offset == sumHeight) {
				scrollViewListener.onScrollBotton();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	public interface ScrollViewListener {
		void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
		void onScrollBotton();
	}

}