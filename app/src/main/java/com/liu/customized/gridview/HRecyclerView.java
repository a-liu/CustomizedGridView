package com.liu.customized.gridview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by liu.jianfei on 2017/12/20.
 */

public class HRecyclerView extends RecyclerView {
    private OnScrollPositionChangedListener mOnScrollPositionChangedListener;
    public void setOnScrollPositionChangedListener(OnScrollPositionChangedListener listener)
    {
        mOnScrollPositionChangedListener = listener;
    }
    public HRecyclerView(Context context) {
        super(context);
    }

    public HRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        // 滑动停止
        if (state == SCROLL_STATE_IDLE)
        {
            LayoutManager loyoutManager = getLayoutManager();
            if (loyoutManager instanceof GridLayoutManager)
            {
                int firstPosition =
                        ((GridLayoutManager)loyoutManager).findFirstVisibleItemPosition();
                int lastPosition = ((GridLayoutManager)loyoutManager).findLastVisibleItemPosition();
                int spanCount = ((GridLayoutManager)loyoutManager).getSpanCount();
                if (mOnScrollPositionChangedListener != null)
                {
                    mOnScrollPositionChangedListener.onScrollPositionChanged(
                            firstPosition, lastPosition, spanCount);
                }
            }
        }
    }

    public interface OnScrollPositionChangedListener{
        void onScrollPositionChanged(int firstPosition, int lastPosition, int totalSpan);
    }
}
