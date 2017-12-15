package com.liu.customizedgridview.gridview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.liu.customizedgridview.R;

/**
 * Created by liu.jianfei on 2017/12/15.
 */

public class CustomizedGridViewLayout extends ViewGroup {
    private CustomizedGridView mGridViewManage;

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

    /**
     * 要求所有的孩子测量自己的大小，然后根据这些孩子的大小完成自己的尺寸测量
     */
    @SuppressLint("NewApi") @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec) {
        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        //测量并保存layout的宽高(使用getDefaultSize时，wrap_content和match_perent都是填充屏幕)
        //稍后会重新写这个方法，能达到wrap_content的效果
        setMeasuredDimension( getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    /**
     * 为所有的子控件摆放位置.
     */
    @Override
    protected void onLayout( boolean changed, int left, int top, int right, int bottom) {
//        final int count = getChildCount();
//        int childMeasureWidth = 0;
//        int childMeasureHeight = 0;
//        int layoutWidth = 0;    // 容器已经占据的宽度
//        int layoutHeight = 0;   // 容器已经占据的宽度
//        int maxChildHeight = 0; //一行中子控件最高的高度，用于决定下一行高度应该在目前基础上累加多少
//        for(int i = 0; i<count;
//            bottom="top+childMeasureHeight" ;
//            child="getChildAt(i);" childmeasureheight="" childmeasurewidth="child.getMeasuredWidth();" layoutheight="" layoutwidth="0;" left="layoutWidth;" maxchildheight="0;" right="left+childMeasureWidth;" top="layoutHeight;" view="">maxChildHeight){
//            maxChildHeight = childMeasureHeight;
//        }
//
//        //确定子控件的位置，四个参数分别代表（左上右下）点的坐标值
//        child.layout(left, top, right, bottom);
    }
}
