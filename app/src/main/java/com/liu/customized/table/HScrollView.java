package com.liu.customized.table;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by liu.jianfei on 2017/12/18.
 */

public class HScrollView extends HorizontalScrollView {
    private float mSpeedRate = 0.5F;
    public void setSpeedRate(float rate){
        mSpeedRate = rate;
    }
    private ScrollViewListener mScrollViewListener;
    public ScrollViewListener getScrollViewListener() {
        return mScrollViewListener;
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.mScrollViewListener = scrollViewListener;
    }

    private boolean mToucheEnabled = true;

    public boolean getToucheEnabled() {
        return mToucheEnabled;
    }

    public void setToucheEnabled(boolean toucheEnabled) {
        this.mToucheEnabled = toucheEnabled;
    }

    public HScrollView(Context context) {
        super(context);
    }

    public HScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void fling(int velocityX) {
        int speed = (int)(velocityX / mSpeedRate);
        super.fling(speed);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mToucheEnabled)
        {
            return super.onInterceptTouchEvent(ev);
        }
        else {
            return mToucheEnabled;
        }
    }

    @Override
    public void onScrollChanged(int x, int y, int oldX, int oldY)
     {
         super.onScrollChanged(x, y, oldX, oldY);
         if (mScrollViewListener != null)
         {
             mScrollViewListener.onScrollChanged(this, x, y, oldX, oldY);
         }
     }

    public interface ScrollViewListener {
        void onScrollChanged(HorizontalScrollView scrollView, int x, int y, int oldX, int oldY);
    }
}
