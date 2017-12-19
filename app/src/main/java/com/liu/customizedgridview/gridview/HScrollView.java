package com.liu.customizedgridview.gridview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Created by liu.jianfei on 2017/12/18.
 */

public class HScrollView extends HorizontalScrollView {
    private ScrollViewListener mScrollViewListener;
    public ScrollViewListener getScrollViewListener() {
        return mScrollViewListener;
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.mScrollViewListener = scrollViewListener;
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
    public void onScrollChanged(int x, int y, int oldX, int oldY)
     {
         super.onScrollChanged(x, y, oldX, oldY);
         mScrollViewListener.onScrollChanged(this, x, y, oldX, oldY);
     }

    public interface ScrollViewListener {
        void onScrollChanged(HorizontalScrollView scrollView, int x, int y, int oldX, int oldY);
    }
}