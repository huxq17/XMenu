package com.andview.example.widget.xmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.andview.example.utils.DisplayUtil;
import com.andview.xmenu.R;

public class XMenu extends RelativeLayout {

    /**
     * Constant value for use with setTouchModeAbove(). Allows the SlidingMenu to be opened with a swipe
     * gesture on the screen's margin
     */
    public static final int TOUCHMODE_MARGIN = 0;

    /**
     * Constant value for use with setTouchModeAbove(). Allows the SlidingMenu to be opened with a swipe
     * gesture anywhere on the screen
     */
    public static final int TOUCHMODE_FULLSCREEN = 1;

    /**
     * Constant value for use with setTouchModeAbove(). Denies the SlidingMenu to be opened with a swipe
     * gesture
     */
    public static final int TOUCHMODE_NONE = 2;
    private MenuView mMenuView;
    private ContentView mContentView;

    public XMenu(Context context) {
        this(context, null);
    }

    public XMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public XMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutParams behindParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mMenuView = new MenuView(context);
        addView(mMenuView, behindParams);

        LayoutParams aboveParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mContentView = new ContentView(context);
        addView(mContentView, aboveParams);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.XMenu);
        try {
            Drawable leftShadowDrawable = a
                    .getDrawable(R.styleable.XMenu_LeftShadowDrawable);
            if (null == leftShadowDrawable) {
                leftShadowDrawable = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.TRANSPARENT,
                        Color.argb(99, 0, 0, 0)});
            }
            mContentView.setLeftShadowDrawable(leftShadowDrawable);
            int touchModeAbove = a.getInt(R.styleable.XMenu_touchModeAbove, TOUCHMODE_MARGIN);
            setTouchModeAbove(touchModeAbove);
            int edgeWidth = a.getInt(R.styleable.XMenu_edgeWidth, DisplayUtil.dip2px(getContext(),10));
            mContentView.setEdgeWith(edgeWidth);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }
    }

    public void setMenu(int layoutId) {
        setMenu(LayoutInflater.from(getContext()).inflate(layoutId, null));
    }

    public void setMenu(View v) {
        mMenuView.setView(v);
        mMenuView.invalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    public void setContent(int layoutId) {
        setContent(LayoutInflater.from(getContext()).inflate(layoutId, null));
    }

    public void setContent(View v) {
        mContentView.setView(v);
        mContentView.invalidate();
    }

    public void toggle() {
        mContentView.toggle();
    }

    public boolean isMenuShowing() {
        return mContentView.isMenuShowing();
    }

    public void showContent() {
        mContentView.showContent();
    }

    public void setMenuWidth(int menuWidth) {
        mContentView.setMenuWidth(menuWidth);
        mMenuView.setMenuWidth(menuWidth);
    }

    public void setLeftShadowWidth(int width) {
        mContentView.setLeftShadowWidth(width);
    }

    /**
     * Controls whether the SlidingMenu can be opened with a swipe gesture.
     * Options are {@link #TOUCHMODE_MARGIN TOUCHMODE_MARGIN}, {@link #TOUCHMODE_FULLSCREEN TOUCHMODE_FULLSCREEN},
     * or {@link #TOUCHMODE_NONE TOUCHMODE_NONE}
     *
     * @param i the new touch mode
     */
    public void setTouchModeAbove(int i) {
        if (i != XMenu.TOUCHMODE_FULLSCREEN && i != XMenu.TOUCHMODE_MARGIN
                && i != XMenu.TOUCHMODE_NONE) {
            throw new IllegalStateException("TouchMode must be set to either" +
                    "TOUCHMODE_FULLSCREEN or TOUCHMODE_MARGIN or TOUCHMODE_NONE.");
        }
        mContentView.setTouchMode(i);
    }
}
