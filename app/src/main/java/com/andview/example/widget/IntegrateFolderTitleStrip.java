package com.andview.example.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.andview.example.utils.DisplayUtil;

import java.util.ArrayList;

public class IntegrateFolderTitleStrip extends View implements
        ViewPager.OnPageChangeListener {

    private final String TAG = "IntegrateFolderTitleStrip";
    /**
     * 字符最大个数
     */
    private final int MAX_LENGTH = 9;

    private ViewPager mViewPager;

    private PagerAdapter mPageAdapter;
    /**
     * 滚动状态
     */
    private int mScrollState = ViewPager.SCROLL_STATE_IDLE;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mNonePrimaryAlpha = 1.0f;
    private Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();
    /**
     * 文字基线
     */
    private float mBaseLine;
    private ArrayList<Rect> mRectList = new ArrayList<Rect>();
    /**
     * mRectList 中的中间值（-）
     */
    private float mCenterX;
    // 0 是当前的透明值 1是其他的
    private int[] mAlphaArray = new int[2];
    private int mScrollPosition;
    // 0 是当前的缩放值 1是其他的
    private float[] mScaleArray = new float[2];

    private float mScaleConstant = 1.5F;

    public IntegrateFolderTitleStrip(Context context, AttributeSet attrs,
                                     int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TypedArray localTypedArray = context.obtainStyledAttributes(attrs,
        // null, defStyleAttr, 0);
        // setTextSize(localTypedArray.getDimensionPixelSize(2, (int)
        // TypedValue.applyDimension(2, 14.0F,
        // context.getResources().getDisplayMetrics())));
        // setTextColor(localTypedArray.getColor(5, -1));
        // localTypedArray.recycle();
        setTextSize(DisplayUtil.sp2px(getContext(),18));
        setTextColor(Color.BLACK);
        initPaint();
    }

    public IntegrateFolderTitleStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    // ViewPager.OnPageChangeListener 接口 滚动过程中调用 0到1 滑动position为0 1到0滑动
    // position为0 position按照前面显示的那页去确定的
    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        // positionOffset 相对于一个页面的百分比 静止状态相当于0
        update(position, positionOffset);
    }

    // ViewPager.OnPageChangeListener 接口 滑动超过一半 滑动完毕后（state =2）调用该方法
    @Override
    public void onPageSelected(int position) {
        if (mScrollState != ViewPager.SCROLL_STATE_IDLE) {
            return;
        }
        update(position, 0.0f);
    }

    // ViewPager.OnPageChangeListener 接口 开始滑动和结束滑动回调这个方法
    // 有三种状态（0，1，2）。state ==1的时辰默示正在滑动，state==2的时辰默示滑动完毕了，state==0的时辰默示什么都没做。
    @Override
    public void onPageScrollStateChanged(int state) {
        this.mScrollState = state;

    }

    public void setTextColor(int color) {
        this.mPaint.setColor(color);
    }

    public void setTextSize(float textSize) {
        this.mPaint.setTextSize(textSize);
        this.mPaint.getFontMetrics(mFontMetrics);
    }

    public void setViewPager(ViewPager viewpager) {
        this.mViewPager = viewpager;
        if (viewpager == null)
            return;
        this.mPageAdapter = mViewPager.getAdapter();
        initRectList();
        update(mViewPager.getCurrentItem(), 0.0F);
    }

    public void setNonePrimaryAlpha(float alpha) {
        mNonePrimaryAlpha = alpha;
        invalidate();
    }

    /**
     * 初始化Paint
     */
    private void initPaint() {
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * 返回标题文字大小
     *
     * @return
     */
    public float getTextSize() {
        return mPaint.getTextSize();
    }

    /**
     * offset 滑动距离相对于一个页面的百分比
     *
     * @param position
     * @param offset
     */
    private void update(int position, float offset) {
        if ((position >= 0) && (position < mRectList.size())){
            mCenterX = -((Rect) mRectList.get(position)).exactCenterX();
        }
        if ((mRectList.size() > 1) && (position < -1 + mRectList.size())){
            mCenterX = mCenterX
                    - offset
                    * (((Rect) mRectList.get(position + 1)).exactCenterX() - ((Rect) mRectList
                    .get(position)).exactCenterX());
        }
        mScrollPosition = position;
        mAlphaArray[0] = (int) (255.0F * (mNonePrimaryAlpha + (1.0F - mNonePrimaryAlpha)
                * (1.0F - offset)));
        mAlphaArray[1] = (int) (255.0F * (mNonePrimaryAlpha + offset
                * (1.0F - mNonePrimaryAlpha)));
        mScaleArray[0] = (1.0F + (this.mScaleConstant - 1.0F) * (1.0F - offset));
        mScaleArray[1] = (1.0F + offset * (this.mScaleConstant - 1.0F));
        invalidate();
    }

    /**
     * 初始化mRectList e()
     */
    public void initRectList() {
        mRectList.clear();
        if (mPageAdapter == null || mPageAdapter.getCount() <= 0) {
            return;
        }
        int count = mPageAdapter.getCount();
        /**
         *文字之间的间隔
         */
        int textSizeWidth = (int) (1.8F * this.mPaint.getTextSize());
        int index = 0;
        int left = 0;
        while (index < count) {
            CharSequence text = mPageAdapter.getPageTitle(index);
            if (text.length() > MAX_LENGTH) {
                text = text.subSequence(0, MAX_LENGTH - 1) + "...";
            }
            int textWidth = (int) (0.5f + mPaint.measureText(text, 0,
                    text.length()));
            mRectList.add(new Rect(left, 0, left + textWidth, 2));
            left = left + textWidth + textSizeWidth;
            index++;
        }
    }

    public int getBaseline() {
        return (int) mBaseLine;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mViewPager = null;
        mPageAdapter = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int clickItem = getClickPosition(event.getX(), event.getY());

                if (clickItem >= 0 && clickItem < mRectList.size()) {
                    mViewPager.setCurrentItem(clickItem, true);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 获取点击的位置
     *
     * @param downX
     * @param downY
     * @return
     */
    private int getClickPosition(float downX, float downY) {
        if ((downY < 0.0F) || (downY > getHeight())) {
            return -1;
        }
        int currentItem = mViewPager.getCurrentItem();
        int halfWidth = getWidth() / 2;
        for (int i = 0; i < mRectList.size(); i++) {

            if ((i < mRectList.size())
                    && (((Rect) mRectList.get(i)).contains((int) (downX
                    - mCenterX - halfWidth), 1)))
                return i;
        }
        return -1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if ((mPageAdapter == null) || (mViewPager == null)
                || (mPageAdapter.getCount() == 0)) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        CharSequence title = mPageAdapter.getPageTitle(0);
        int width = resolveSize(
                (int) (3.0F * mPaint.measureText(title, 0, title.length()))
                        + getPaddingLeft() + getPaddingRight(),
                widthMeasureSpec);
        int height = resolveSize((int) (mFontMetrics.bottom - mFontMetrics.top
                + getPaddingTop() + getPaddingBottom()), heightMeasureSpec);
        setMeasuredDimension(width, height);
        mBaseLine = (getPaddingTop()
                + (height - getPaddingTop() - getPaddingBottom()) / 2.0F - (mFontMetrics.top + mFontMetrics.bottom)
                * Math.max(this.mScaleConstant, 1.0F) / 2.0F);
        update(mViewPager.getCurrentItem(), 0.0F);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPageAdapter == null)
            return;
        int halfWidth = getWidth() / 2;
        int left = (int) (getPaddingLeft() - mCenterX - halfWidth);
        int right = (int) (getWidth() - getPaddingRight() - mCenterX - halfWidth);
        // 不是当前选项透明值
        int alpha = (int) (255.0F * mNonePrimaryAlpha); // i4
        int curItem = mViewPager.getCurrentItem();
        int pageCount = mPageAdapter.getCount();

        Rect curRect = null;
        Object pageTitle = "";
        float scale = 0;

        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
            if ((pageIndex >= 0) && (pageIndex < pageCount)) {
                curRect = (Rect) mRectList.get(pageIndex);
                if (curRect.intersects(left, 0, right, 1)) {
                    pageTitle = mPageAdapter.getPageTitle(pageIndex);
                    if (((CharSequence) pageTitle).length() > MAX_LENGTH)
                        pageTitle = ((CharSequence) pageTitle).subSequence(0,
                                MAX_LENGTH - 1) + "...";
                    if (pageIndex == mScrollPosition + 1) {
                        mPaint.setAlpha(mAlphaArray[1]);
                        scale = mScaleArray[1];
                        // mPaint.setAlpha(mAlphaArray[1]);
                    } else if (pageIndex == mScrollPosition) {
                        mPaint.setAlpha(mAlphaArray[0]);
                        scale = mScaleArray[0];
                    } else {
                        mPaint.setAlpha(alpha);
                        scale = 1.0F;
                    }
                }
            }
            float x = curRect.exactCenterX() + mCenterX + halfWidth;
            float y = mBaseLine;
            if (scale != 1.0F) {
                canvas.save();
                canvas.scale(scale, scale, x, y);
            }
            canvas.drawText((CharSequence) pageTitle, 0,
                    ((CharSequence) pageTitle).length(), x, y, mPaint);
            if (scale != 1.0F)
                canvas.restore();
        }

    }

}
