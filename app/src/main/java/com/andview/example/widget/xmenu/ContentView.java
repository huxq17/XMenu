package com.andview.example.widget.xmenu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.andview.example.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class ContentView extends ViewGroup {

    private int duration = 500;
    /**
     * 菜单宽度
     */
    public int menuWidth = 400;
    /**
     * 速率采样间隔
     */
    private static final int SNAP_VELOCITY = 1000;
    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;

    public int mTouchState = TOUCH_STATE_REST;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    /**
     * 系统所能识别的最小滑动距离
     */
    private int mTouchSlop;
    /**
     * 手指点击屏幕的最初位置
     */
    private int mInitialMotionX;
    private int mLastMotionX;
    private int mLastMotionY;

    private int mShadowWidth = 15;
    private Drawable mShadowDrawable;

    private int mTouchMode = XMenu.TOUCHMODE_MARGIN;
    private List<View> mIgnoredViews = new ArrayList<View>();
    private View mContentView;
    /**
     * 当TouchMode为TOUCHMODE_MARGIN时，设定屏幕左边缘可滑动菜单的距离
     */
    private int mEdgeWidth;

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
        if (mContentView.getVisibility() != GONE) {
            mContentView.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mContentView.getVisibility() != GONE) {
            l = getPaddingLeft();
            t = getPaddingTop();
            r = l + mContentView.getMeasuredWidth();
            b = l + mContentView.getMeasuredHeight();
            mContentView.layout(l, t, r, b);
        }
    }

    private void init() {
        //默认情况下ViewGroup不会回调onDraw方法，但是为了绘制侧滑的阴影部分，
        //通过设置setWillNotDraw来让viewgroup回调onDraw
        setWillNotDraw(false);
        mScroller = new Scroller(getContext());
        final ViewConfiguration vf = ViewConfiguration.get(getContext());
        mTouchSlop = vf.getScaledTouchSlop();
        mEdgeWidth = vf.getScaledEdgeSlop() * 2;
    }

    public void setView(View v) {
        if (getChildCount() > 0) {
            removeAllViews();
        }
        if (v.getParent() != null) {
            throw new RuntimeException(
                    "the view has parent,please detach this view first");
        }
        mContentView = v;
        addView(mContentView);
    }

    public void setLeftShadowDrawable(Drawable drawable) {
        mShadowDrawable = drawable;
    }

    /**
     * 设置阴影的宽度
     *
     * @param width
     */
    public void setLeftShadowWidth(int width) {
        mShadowWidth = width;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null != mShadowDrawable) {
            int left = -mShadowWidth;
            int right = left + mShadowWidth;
            int top = 0;
            int bottom = top + getHeight();
            mShadowDrawable.setBounds(left, top, right, bottom);
            mShadowDrawable.draw(canvas);
        }
    }

    private MotionEvent mLastMoveEvent;
    private boolean isCloseMenu = false;
    private boolean isIntercept = true;
    private boolean mHasSendCancelEvent = false;
    private int oldScrollX = 0;
    /**
     * 是否应该滑动菜单
     */
    private boolean mShouldMove = true;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final int x = (int) ev.getX();
        final int y = (int) ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mHasSendCancelEvent = false;
                mShouldMove = false;
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.addMovement(ev);
                mLastMotionX = mInitialMotionX = x;
                mLastMotionY = y;
                if (isMenuShowing() && mLastMotionX >= menuWidth) {
                    //当菜单显示并且点击的位置处于contentView上，那么在手指抬起时关闭菜单
                    isCloseMenu = true;
                }
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = ev;
                oldScrollX = getScrollX();
                if (isIntercept && oldScrollX == 0 && !thisTouchAllowed(ev)) {
                    //当菜单关闭时，不允许打开菜单则返回默认值不拦截
                    return super.dispatchTouchEvent(ev);
                } else {
                    //如果已经达到打开菜单的条件了，则不再进行判断了，直接拦截接着进行菜单滑动的操作
                    isIntercept = false;
                }
                final int xDiff = (int) Math.abs(x - mLastMotionX);
                final int yDiff = (int) Math.abs(y - mLastMotionY);
                //避免与竖直方向的滑动产生冲突
                if (xDiff > yDiff && xDiff > mTouchSlop) {
                    mShouldMove = true;
                }
                //滑动菜单
                if (mShouldMove) {
                    sendCancelEvent();
                    final int deltaX = mLastMotionX - x;
                    mLastMotionX = x;
                    int scrollX = oldScrollX + deltaX;
                    final int leftBound = 0;
                    final int rightBound = -menuWidth;
                    //边界控制，防止越界
                    if (scrollX > leftBound) {//到达左边
                        scrollX = leftBound;
                    } else if (scrollX < rightBound) {//到达右边
                        scrollX = rightBound;
                    }
                    scrollTo(scrollX, getScrollY());
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                LogUtils.i("ACTION_UP");
                if (isCloseMenu && isMenuShowing()) {
                    showContent();
                } else {
                    oldScrollX = getScrollX();
                    if (oldScrollX < 0 && oldScrollX > -menuWidth) {
                        int dx = 0;
                        if (oldScrollX < -menuWidth / 2) {
                            dx = -menuWidth - oldScrollX;
                        } else {
                            dx = -oldScrollX;
                        }
                        smoothScrollTo(dx, 500);
                    }
                }
                isCloseMenu = false;
                isIntercept = true;
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void sendCancelEvent() {
        if (!mHasSendCancelEvent) {
            mHasSendCancelEvent = true;
            MotionEvent last = mLastMoveEvent;
            MotionEvent e = MotionEvent.obtain(
                    last.getDownTime(),
                    last.getEventTime()
                            + ViewConfiguration.getLongPressTimeout(),
                    MotionEvent.ACTION_CANCEL, last.getX(), last.getY(),
                    last.getMetaState());
            dispatchTouchEventSupper(e);
        }
    }

    public boolean dispatchTouchEventSupper(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (getScrollX() == 0) {
                    // 当菜单隐藏时，消费此事件解决点击穿透的问题
                    return true;
                }
        }
        return false;
    }

    public void toggle() {
        int oldScrollX = getScrollX();
        if (oldScrollX == 0) {
            smoothScrollTo(-menuWidth, duration);
        } else if (oldScrollX == -menuWidth) {
            smoothScrollTo(menuWidth, duration);
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
            smoothScrollTo(menuWidth, duration);
        }
    }

    public void setMenuWidth(int menuWidth) {
        this.menuWidth = menuWidth;
    }

    public void addIgnoredView(View v) {
        if (!mIgnoredViews.contains(v)) {
            mIgnoredViews.add(v);
        }
    }

    public void removeIgnoredView(View v) {
        mIgnoredViews.remove(v);
    }

    public void clearIgnoredViews() {
        mIgnoredViews.clear();
    }

    private boolean isInIgnoredView(MotionEvent ev) {
        Rect rect = new Rect();
        for (View v : mIgnoredViews) {
            v.getHitRect(rect);
            if (rect.contains((int) ev.getX(), (int) ev.getY())) return true;
        }
        return false;
    }

    private void smoothScrollTo(int dx, int duration) {
        int oldScrollX = getScrollX();
        mScroller.startScroll(oldScrollX, getScrollY(), dx, getScrollY(),
                duration);
        invalidate();
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

    private void enableChildrenCache() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View layout = (View) getChildAt(i);
            layout.setDrawingCacheEnabled(true);
        }
    }

    private void clearChildrenCache() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View layout = (View) getChildAt(i);
            layout.setDrawingCacheEnabled(false);
        }
    }

    public void setTouchMode(int touchMode) {
        mTouchMode = touchMode;
    }

    public int getTouchMode() {
        return mTouchMode;
    }

    /**
     * 是否允许滑动菜单，可通过设置TouchMode来避免与viewpager的冲突
     *
     * @param ev
     * @return
     */
    private boolean thisTouchAllowed(MotionEvent ev) {
        int x = (int) (ev.getX());
        if (isMenuShowing()) {
            return true;
        }
        switch (mTouchMode) {
            case XMenu.TOUCHMODE_FULLSCREEN:
                return !isInIgnoredView(ev);
            case XMenu.TOUCHMODE_NONE:
                return false;
            case XMenu.TOUCHMODE_MARGIN:
                return x <= mEdgeWidth;
        }
        return false;
    }

    /**
     * 设置屏幕边缘的宽度，建议使用系统的配置
     */
    @Deprecated
    public void setEdgeWidth(int width) {
        mEdgeWidth = width;
    }
}