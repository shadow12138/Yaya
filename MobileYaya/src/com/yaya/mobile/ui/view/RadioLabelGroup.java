package com.yaya.mobile.ui.view;

import android.content.Context;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.yaya.mobile.utils.Utils;

public class RadioLabelGroup extends RadioGroup {

	Context mContext;

	public RadioLabelGroup(Context context) {
		super(context);
		mContext = context;
	}

	public RadioLabelGroup(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		mContext = context;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int childCount = getChildCount();
		int maxWidth = r - l, x = 0, y = 0, row = 0, horizontalPadding = Utils.dp2px(mContext, 2), verticalPadding = Utils.dp2px(mContext, 2);
		for (int i = 0; i < childCount; i++) {
			final View child = this.getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				int width = child.getMeasuredWidth();
				int height = child.getMeasuredHeight();
				if ((x + width + horizontalPadding) > maxWidth) {
					row++;
					x = 0;
				}
				y = row * (height + verticalPadding);
				child.layout(x, y, x + width, y + height);
				x += width + horizontalPadding;
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
		int x = 0, y = 0, row = 1, verticalPadding = Utils.dp2px(mContext, 2), height = 0;
		for (int index = 0; index < getChildCount(); index++) {
			View child = this.getChildAt(index);
			child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			int width = child.getMeasuredWidth() + verticalPadding;
			height = child.getMeasuredHeight();
			x += width;
			if (x > maxWidth) {
				x = width;
				row++;
			}
		}
		y = row * (height + verticalPadding);
		setMeasuredDimension(maxWidth, y);
	}

}
