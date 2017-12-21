package com.liu.customizedgridview.gridview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.Toast;

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

    private AdjustHeightListener mAdjustHeightListener;
    public void setAdjustHeightListener(AdjustHeightListener listener) {
        this.mAdjustHeightListener = listener;
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
        this.setScrollListener();
    }

    public VListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setScrollListener();
    }

    public VListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setScrollListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setScrollListener();
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 4);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    private void setScrollListener()
    {
        this.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE ||
                        scrollState == SCROLL_STATE_TOUCH_SCROLL  )
                {
                    View subView = view.getChildAt(0);
                    if (subView != null)
                    {
                        int top = subView.getTop();
                        if (mListViewListener != null)
                        {
                            mListViewListener.onScrollPositionFromTop(VListView.this, view.getFirstVisiblePosition(), top);
                        }
                    }
                }

                switch (scrollState) {
                    // 当不滚动时
                    case OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        if (view.getLastVisiblePosition() >= (view.getCount() - 1)) {
                            if (mListViewListener != null)
                            {
                                mListViewListener.onScrollOver(VListView.this, SCROLL_DIRECTION.DOWN);
                            }
                        }
                        // 判断滚动到顶部
                        if (view.getLastVisiblePosition() <= 0) {
                            if (mListViewListener != null)
                            {
                                mListViewListener.onScrollOver(VListView.this,SCROLL_DIRECTION.UP);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View subView = view.getChildAt(0);
                if (subView != null)
                {
                    int top = subView.getTop();
                    mListViewListener.onScrollPositionFromTop(VListView.this, firstVisibleItem, top);

                    //判断顶部底部
                    if (firstVisibleItem == 0) {
                        View firstVisibleItemView = view.getChildAt(0);
                        if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
                            if (mListViewListener != null)
                            {
//                                mListViewListener.onScrollOver(VListView.this,SCROLL_DIRECTION.UP);
                            }
                        }
                    } else if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                        View lastVisibleItemView = view.getChildAt(view.getChildCount() - 1);
                        if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == view.getHeight()) {
                            if (mListViewListener != null)
                            {
//                                mListViewListener.onScrollOver(VListView.this, SCROLL_DIRECTION.DOWN);
                            }
                        }
                    }

//                    // 判断滚动到底部
//                    if (visibleItemCount >= totalItemCount) {
//                        if (mListViewListener != null)
//                        {
//                            mListViewListener.onScrollOver(VListView.this, SCROLL_DIRECTION.DOWN);
//                        }
//                    }
//                    // 判断滚动到顶部
//                    if (firstVisibleItem <= 0) {
//                        if (mListViewListener != null)
//                        {
//                            mListViewListener.onScrollOver(VListView.this,SCROLL_DIRECTION.UP);
//                        }
//                    }
                }
            }
        });
    }
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
////        super.onInterceptTouchEvent(ev)
//        boolean result = mTouchEnable;
//
//        return result;
//    }
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//
//        if ( getFirstVisiblePosition() == 0 &&
//                getChildAt(0).getTop() == 0) {
//            //到头部了
//            getParent().requestDisallowInterceptTouchEvent(false);//放行
//        } else {
//            getParent().requestDisallowInterceptTouchEvent(true);//拦截
//            if (mAdjustHeightListener != null)
//            {
//                this.mAdjustHeightListener.adjustHeight(93 * getChildCount());
//            }
//        }
//        return super.onInterceptTouchEvent(ev);
//    }

    @Override
    protected void onScrollChanged(int l, int t, int oldL, int oldT) {
        super.onScrollChanged(l, t, oldL, oldT);
        if (mListViewListener != null)
        {
            mListViewListener.onScrollChanged(this, l, t, oldL, oldT);
        }
    }

//    @Override
//    public void onViewAdded(View child) {
//        super.onViewAdded(child);
//        if (mAdjustHeightListener != null)
//        {
//            this.mAdjustHeightListener.adjustHeight(93);
//        }
//    }

    public interface AdjustHeightListener{
        void adjustHeight(int height);
    }
    public interface ListViewListener{
        void onScrollChanged(ListView scrollView, int l, int t, int oldL, int oldT);
        void onScrollPositionFromTop(ListView scrollView, int position, int top);
        void onScrollOver(ListView scrollView, SCROLL_DIRECTION direction);
    }
    public enum SCROLL_DIRECTION{
        UP,
        DOWN,
    }
}
