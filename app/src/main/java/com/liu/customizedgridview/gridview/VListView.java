package com.liu.customizedgridview.gridview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;

/**
 * Created by liu.jianfei on 2017/12/18.
 */

public class VListView extends ListView {
    private ListViewListener mListViewListener;
    public ListViewListener getListViewListener() {
        return mListViewListener;
    }

    public void setListViewListener(ListViewListener listViewListener) {
        this.mListViewListener = listViewListener;
    }

    private boolean mTouchEnable = true;
    public boolean GetTouchEnable() {
        return mTouchEnable;
    }
    public void setTouchEnable(boolean touchEnable) {
        this.mTouchEnable = touchEnable;
    }

    public VListView(Context context) {
        super(context);
    }

    public VListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        super.onInterceptTouchEvent(ev)
        boolean result = mTouchEnable;

        return result;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldL, int oldT) {
        super.onScrollChanged(l, t, oldL, oldT);
        if (mListViewListener != null)
        {
            mListViewListener.onScrollChanged(this, l, t, oldL, oldT);
        }
    }

    public interface ListViewListener{
        void onScrollChanged(ListView scrollView, int l, int t, int oldL, int oldT);
    }

    //    public void on
}
