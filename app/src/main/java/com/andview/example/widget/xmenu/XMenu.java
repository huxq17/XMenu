package com.andview.example.widget.xmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class XMenu extends RelativeLayout {

    private MenuView mMenuView;
    private ContentView mContentView;

    public XMenu(Context context) {
        super(context);
        init(context);
    }

    public XMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public XMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutParams behindParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mMenuView = new MenuView(context);
        addView(mMenuView, behindParams);

        LayoutParams aboveParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mContentView = new ContentView(context);
        addView(mContentView, aboveParams);
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
}
