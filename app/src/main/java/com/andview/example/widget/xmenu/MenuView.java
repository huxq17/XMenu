package com.andview.example.widget.xmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class MenuView extends ViewGroup {

	private FrameLayout mContainer;
	private int menuWidth = 400;

	public MenuView(Context context) {
		super(context);
		init();
	}

	public MenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MenuView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(0, widthMeasureSpec);
		int height = getDefaultSize(0, heightMeasureSpec);
		setMeasuredDimension(width, height);
		final int contentHeight = getChildMeasureSpec(heightMeasureSpec, 0,
				height);
		final int menuWidth = getChildMeasureSpec(widthMeasureSpec, 0,
				this.menuWidth);
		mContainer.measure(menuWidth, contentHeight);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mContainer.layout(0, 0, mContainer.getMeasuredWidth(),
				mContainer.getMeasuredHeight());
	}

	public void setMenuWidth(int menuWidth) {
		this.menuWidth = menuWidth;
	}

	private void init() {
		mContainer = new FrameLayout(getContext());
		super.addView(mContainer);
	}

	public void setView(View v) {
		if (mContainer.getChildCount() > 0) {
			mContainer.removeAllViews();
		}
		mContainer.addView(v);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		View child;
		for (int i = 0; i < getChildCount(); i++) {
			child = getChildAt(i);
			child.setFocusable(true);
			child.setClickable(true);
		}
	}
}
