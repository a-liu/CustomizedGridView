package com.liu.customizedgridview.gridview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.liu.customizedgridview.R;

/**
 * Created by liu.jianfei on 2017/12/15.
 */

public class CustomizedGridViewLayout extends ViewGroup {
    private CustomizedGridView mGridViewManage;
    private OnLoadCompleteListener mOnLoadCompleteListener;
    public void setOnLoadCompleteListener(OnLoadCompleteListener onLoadCompleteListener)
    {
        mOnLoadCompleteListener = onLoadCompleteListener;
    }
    public CustomizedGridViewLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.customized_grid_view_layout, null);
        addView(view);
    }

    public CustomizedGridViewLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.customized_grid_view_layout, null);
        addView(view);
    }

    public CustomizedGridViewLayout(Context context) {
        super(context);
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.customized_grid_view_layout, null);
        addView(view);
    }
    public CustomizedGridView getGridView() {
        return mGridViewManage;
    }

    public void setGridView(CustomizedGridView gridView) {
        this.mGridViewManage = gridView;
    }

    @SuppressLint("NewApi") @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec) {
//        // 计算出所有的childView的宽和高
//        measureChildren(widthMeasureSpec, heightMeasureSpec);
//        //测量并保存layout的宽高(使用getDefaultSize时，wrap_content和match_perent都是填充屏幕)
//        //稍后会重新写这个方法，能达到wrap_content的效果
//        setMeasuredDimension( getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
//                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));

        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);


        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        /**
         * 记录如果是wrap_content是设置的宽和高
         */
        int width = 0;
        int height = 0;

        int cCount = getChildCount();

        int cWidth = 0;
        int cHeight = 0;
        MarginLayoutParams cParams = null;

        // 用于计算左边两个childView的高度
        int lHeight = 0;
        // 用于计算右边两个childView的高度，最终高度取二者之间大值
        int rHeight = 0;

        // 用于计算上边两个childView的宽度
        int tWidth = 0;
        // 用于计算下面两个childiew的宽度，最终宽度取二者之间大值
        int bWidth = 0;

        /**
         * 根据childView计算的出的宽和高，以及设置的margin计算容器的宽和高，主要用于容器是warp_content时
         */
        for (int i = 0; i < cCount; i++)
        {
            View childView = getChildAt(i);
            cWidth = childView.getMeasuredWidth();
            cHeight = childView.getMeasuredHeight();
            if (childView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)
            {
                cParams = (MarginLayoutParams) childView.getLayoutParams();
            } else if(childView.getLayoutParams() instanceof FrameLayout.LayoutParams)
            {
                cParams = (FrameLayout.LayoutParams) childView.getLayoutParams();
            }
            else {
                cParams = new ViewGroup.MarginLayoutParams(childView.getLayoutParams());
            }

            // 上面两个childView
            if (i == 0 || i == 1)
            {
                tWidth += cWidth + cParams.leftMargin + cParams.rightMargin;
            }

            if (i == 2 || i == 3)
            {
                bWidth += cWidth + cParams.leftMargin + cParams.rightMargin;
            }

            if (i == 0 || i == 2)
            {
                lHeight += cHeight + cParams.topMargin + cParams.bottomMargin;
            }

            if (i == 1 || i == 3)
            {
                rHeight += cHeight + cParams.topMargin + cParams.bottomMargin;
            }

        }

        width = Math.max(tWidth, bWidth);
        height = Math.max(lHeight, rHeight);

        /**
         * 如果是wrap_content设置为我们计算的值
         * 否则：直接设置为父容器计算的值
         */
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                : width, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
                : height);
    }

    @Override
    protected void onLayout( boolean changed, int left, int top, int right, int bottom) {
        int cCount = getChildCount();
        int cWidth = 0;
        int cHeight = 0;
        MarginLayoutParams cParams = null;
        /**
         * 遍历所有childView根据其宽和高，以及margin进行布局
         */
        for (int i = 0; i < cCount; i++)
        {
            View childView = getChildAt(i);
            cWidth = childView.getMeasuredWidth();
            cHeight = childView.getMeasuredHeight();
            if (childView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)
            {
                cParams = (MarginLayoutParams) childView.getLayoutParams();
            }
            else if(childView.getLayoutParams() instanceof FrameLayout.LayoutParams)
            {
                cParams = (FrameLayout.LayoutParams) childView.getLayoutParams();
            }
            else {
                cParams = new ViewGroup.MarginLayoutParams(childView.getLayoutParams());
            }


            int cl = 0, ct = 0, cr = 0, cb = 0;

            switch (i)
            {
                case 0:
                    cl = cParams.leftMargin;
                    ct = cParams.topMargin;
                    break;
                case 1:
                    cl = getWidth() - cWidth - cParams.leftMargin
                            - cParams.rightMargin;
                    ct = cParams.topMargin;

                    break;
                case 2:
                    cl = cParams.leftMargin;
                    ct = getHeight() - cHeight - cParams.bottomMargin;
                    break;
                case 3:
                    cl = getWidth() - cWidth - cParams.leftMargin
                            - cParams.rightMargin;
                    ct = getHeight() - cHeight - cParams.bottomMargin;
                    break;

            }
            cr = cl + cWidth;
            cb = cHeight + ct;
            childView.layout(cl, ct, cr, cb);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mOnLoadCompleteListener != null)
        {
            mOnLoadCompleteListener.onLoadComplete(this);
        }
    }

    public interface OnLoadCompleteListener{
        void onLoadComplete(View view);
    }
}
