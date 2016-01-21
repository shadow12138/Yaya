package com.yaya.mobile.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.yaya.mobile.utils.Utils;

public class LabelGroup extends ViewGroup {

	Context mContext;

	public LabelGroup(Context context) {
		super(context);
		mContext = context;
	}

	public LabelGroup(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		mContext = context;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int childCount = getChildCount();
		int maxWidth = r - l, x = 0, y = 0, row = 0, horizontalPadding = Utils.dp2px(mContext, 2),verticalPadding = Utils.dp2px(mContext, 5);
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
		int childCount = getChildCount();
		int x = 0, y = 0, row = 0, verticalPadding = Utils.dp2px(mContext, 5);
		for (int index = 0; index < childCount; index++) {
			final View child = getChildAt(index);
			if (child.getVisibility() != View.GONE) {
				child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
				int width = child.getMeasuredWidth();
				int height = child.getMeasuredHeight();
				x += width;
				y = row * (height + verticalPadding) + height + 20;
				if (x > maxWidth) {
					x = width;
					row++;
					y = row * (height + verticalPadding) + height + 20;
				}
			}
		}

		setMeasuredDimension(maxWidth, y);
	}

}
