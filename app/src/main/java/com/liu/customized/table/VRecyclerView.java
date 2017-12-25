package com.liu.customized.table;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by liu.jianfei on 2017/12/20.
 */

public class VRecyclerView extends RecyclerView {
    private OnScrollPositionToEndListener mOnScrollPositionToEndListener;
    public void setOnScrollPositionToEndListener(OnScrollPositionToEndListener listener)
    {
        mOnScrollPositionToEndListener = listener;
    }
    public VRecyclerView(Context context) {
        super(context);
    }

    public VRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (computeVerticalScrollRange() == (this.getHeight() + this.computeVerticalScrollOffset()))
        {
            if (mOnScrollPositionToEndListener != null)
            {
                mOnScrollPositionToEndListener.onScrollPositionToEnd();
            }

        }
    }


    public interface OnScrollPositionToEndListener{
        void onScrollPositionToEnd();
    }
}
