package com.andview.example.widget.xmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class MenuView extends ViewGroup {

    private int menuWidth = 400;

    public MenuView(Context context) {
        super(context);
    }

    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
        final int menuWidthSpec = getChildMeasureSpec(widthMeasureSpec, 0,
                this.menuWidth);
        final int menuHeightSpec = getChildMeasureSpec(heightMeasureSpec, 0,
                height);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if(child.getVisibility()!=GONE){
                child.measure(menuWidthSpec, menuHeightSpec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if(child.getVisibility()!=GONE){
                child.layout(l,t,r,b);
            }
        }
    }

    public void setMenuWidth(int menuWidth) {
        this.menuWidth = menuWidth;
    }

    public void setView(View v) {
        if (getChildCount() > 0) {
            removeAllViews();
        }
        addView(v);
    }
}
