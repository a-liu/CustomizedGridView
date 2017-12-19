package com.liu.customizedgridview.gridview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by liu.jianfei on 2017/12/18.
 */

public class VScrollView extends ScrollView {
    private ScrollViewListener mScrollViewListener;
    public ScrollViewListener getScrollViewListener() {
        return mScrollViewListener;
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.mScrollViewListener = scrollViewListener;
    }


    public VScrollView(Context context) {
        super(context);
    }

    public VScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
        void onScrollChanged(ScrollView scrollView, int x, int y, int oldX, int oldY);
    }
}
