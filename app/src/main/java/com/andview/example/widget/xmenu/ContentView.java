package com.andview.example.widget.xmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class ContentView extends ViewGroup {

	/** 菜单宽度 */
	public int menuWidth = 400;
	/** 速率采样间隔 */
	private static final int SNAP_VELOCITY = 1000;
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;

	public int mTouchState = TOUCH_STATE_REST;

	private FrameLayout mContainer;

	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	/** 系统所能识别的最小滑动距离 */
	private int mTouchSlop;
	/** 手指点击屏幕的最初位置 */
	private float mLastMotionX;
	private float mLastMotionY;

	public ContentView(Context context) {
		super(context);
		init();
	}

	public ContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ContentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mContainer.measure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int width = r - l;
		final int height = b - t;
		mContainer.layout(0, 0, width, height);
	}

	private void init() {
		mContainer = new FrameLayout(getContext());
		mScroller = new Scroller(getContext());
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		super.addView(mContainer);
	}

	public void setView(View v) {
		if (mContainer.getChildCount() > 0) {
			mContainer.removeAllViews();
		}
		if (v.getParent() != null) {
			throw new RuntimeException(
					"the view has parent,please detach this view first");
		}
		mContainer.addView(v);
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		postInvalidate();
	}

	@Override
	public void computeScroll() {
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				int oldX = getScrollX();
				int oldY = getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY();
				if (oldX != x || oldY != y) {
					scrollTo(x, y);
				}
				// Keep on drawing until the animation has finished.
				postInvalidate();
			}
		}
	}

	//
	// @Override
	// public boolean onInterceptTouchEvent(MotionEvent ev) {
	//
	// final int action = ev.getAction();
	// if ((action == MotionEvent.ACTION_MOVE)
	// && (mTouchState != TOUCH_STATE_REST)) {
	// Log.e("ad", "return true");
	// return true;
	// }
	//
	// final float x = ev.getX();
	// final float y = ev.getY();
	//
	// switch (action) {
	// case MotionEvent.ACTION_DOWN:
	// // Remember location of down touch
	// mLastMotionX = x;
	// mLastMotionY = y;
	//
	// mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
	// : TOUCH_STATE_SCROLLING;
	// Log.e("ad", "onInterceptTouchEvent  ACTION_DOWN  mTouchState=="
	// + (mTouchState == TOUCH_STATE_SCROLLING));
	//
	// break;
	// case MotionEvent.ACTION_MOVE:
	// final int xDiff = (int) Math.abs(x - mLastMotionX);
	// final int yDiff = (int) Math.abs(y - mLastMotionY);
	//
	// final int touchSlop = mTouchSlop;
	// boolean xMoved = xDiff > touchSlop;
	// boolean yMoved = yDiff > touchSlop;
	//
	// if (xMoved || yMoved) {
	// if (xMoved) {
	// // Scroll if the user moved far enough along the X axis
	// mTouchState = TOUCH_STATE_SCROLLING;
	// Log.e("ad",
	// "onInterceptTouchEvent  ACTION_MOVE  mTouchState=="
	// + (mTouchState == TOUCH_STATE_SCROLLING));
	// enableChildrenCache();
	// }
	// }
	// break;
	//
	// case MotionEvent.ACTION_CANCEL:
	// case MotionEvent.ACTION_UP:
	// // Release the drag
	// clearChildrenCache();
	// mTouchState = TOUCH_STATE_REST;
	// Log.e("ad", "onInterceptTouchEvent  ACTION_UP  mTouchState=="
	// + (mTouchState == TOUCH_STATE_SCROLLING));
	// break;
	// }
	//
	// /*
	// * The only time we want to intercept motion events is if we are in the
	// * drag mode.
	// */
	// return mTouchState != TOUCH_STATE_REST;
	// }
	MotionEvent mLastMoveEvent;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
		case MotionEvent.ACTION_MOVE:

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			//
			if (isMenuShowing() && mLastMotionX >= menuWidth) {
				showContent();
				return true;
			}
			break;

		default:
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			int oldScrollX = getScrollX();
			if (oldScrollX == 0) {
				// 当菜单隐藏时，消费此事件解决点击穿透的问题
				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			// final int xDiff = (int) Math.abs(x - mLastMotionX);
			// final int yDiff = (int) Math.abs(y - mLastMotionY);
			// final int touchSlop = mTouchSlop;
			// boolean xMoved = xDiff > touchSlop;
			// if (xMoved) {
			// mTouchState = TOUCH_STATE_SCROLLING;
			// Log.e("ad", "onInterceptTouchEvent  ACTION_MOVE  mTouchState=="
			// + (mTouchState == TOUCH_STATE_SCROLLING));
			// enableChildrenCache();
			// }
			// if (mTouchState == TOUCH_STATE_SCROLLING) {
			// final float deltaX = mLastMotionX - x;
			// mLastMotionX = x;
			// float oldScrollX = getScrollX();
			// float scrollX = oldScrollX + deltaX;
			// final float leftBound = 0;
			// final float rightBound = -MENU_WIDTH;
			// if (scrollX > leftBound) {
			// scrollX = leftBound;
			// } else if (scrollX < rightBound) {
			// scrollX = rightBound;
			// }
			// scrollTo((int) scrollX, getScrollY());
			//
			// }
			break;
		case MotionEvent.ACTION_UP:
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(SNAP_VELOCITY);
				int velocityX = (int) velocityTracker.getXVelocity();
				oldScrollX = getScrollX();
				Log.e("ad", "oldScrollX  ==  " + oldScrollX);
				int dx = 0;
				if (oldScrollX < -menuWidth / 2) {
					dx = -menuWidth - oldScrollX;
				} else {
					dx = -oldScrollX;
				}
				smoothScrollTo(dx);
				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
			} else {
				showContent();
			}
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			return true;
		}

		return false;
	}

	public void toggle() {
		int oldScrollX = getScrollX();
		if (oldScrollX == 0) {
			smoothScrollTo(-menuWidth);
		} else if (oldScrollX == -menuWidth) {
			smoothScrollTo(menuWidth);
		}
	}

	public boolean isMenuShowing() {
		int oldScrollX = getScrollX();
		if (oldScrollX == 0) {
			return false;
		} else if (oldScrollX == -menuWidth) {
			return true;
		}
		return false;
	}

	public void showContent() {
		if (isMenuShowing()) {
			smoothScrollTo(menuWidth);
		}
	}

	public void setMenuWidth(int menuWidth) {
		this.menuWidth = menuWidth;
	}

	void smoothScrollTo(int dx) {
		int duration = 500;
		int oldScrollX = getScrollX();
		mScroller.startScroll(oldScrollX, getScrollY(), dx, getScrollY(),
				duration);
		invalidate();
	}

	void enableChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(true);
		}
	}

	void clearChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(false);
		}
	}

}
