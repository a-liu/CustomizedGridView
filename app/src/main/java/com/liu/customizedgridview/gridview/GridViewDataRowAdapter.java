package com.liu.customizedgridview.gridview;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.liu.customizedgridview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allen on 2017/4/14.
 *
 * 评论页面的适配器
 */

public class GridViewDataRowAdapter extends RecyclerView.Adapter<GridViewDataRowAdapter.ViewHolder> {
    private GridViewDataListAdapter.DISPLAY_ROW_MEMBER mDisplayFlag;
    private List<GridViewCellBean> cellList;

    private boolean isSelected = false;

    private List<GridViewCellBean> selectList;

    private boolean mShortTextFlag;
    private Activity mContext;
    private int mDisplayColumnCount;
    private int mFirstRowColumnCount;
    private OnGridViewRowCellActionListener mOnGridViewRowCellActionListener;
    private List<GridViewCellBean> mHeaders;
    private float xDown,yDown, xUp;
    private boolean isLongClickModule = false;
    private boolean isLongClicking = false;
    public void setOnGridViewRowCellActionListener(OnGridViewRowCellActionListener listener)
    {
        mOnGridViewRowCellActionListener = listener;
    }
    public GridViewDataRowAdapter(Activity context, int totalSpan, List<GridViewCellBean> headers, List<GridViewCellBean> cellList, int initColumnCount, boolean shortText) {
        this.cellList = cellList;
        this.mHeaders = headers;
        this.mContext = context;
        this.mDisplayColumnCount = initColumnCount;
        selectList = new ArrayList<>();
        this.mShortTextFlag = shortText;
        this.mDisplayFlag = GridViewDataListAdapter.DISPLAY_ROW_MEMBER.COLUMN;
        // 计算行列数
        this.mFirstRowColumnCount = calculateFirstRowColumnCount(totalSpan, cellList);
    }
    public GridViewDataRowAdapter(Activity context, int totalSpan, List<GridViewCellBean> headers, List<GridViewCellBean> cellList, boolean allRowDisplay, boolean shortText) {
        this.cellList = cellList;
        this.mHeaders = headers;
        this.mContext = context;
        selectList = new ArrayList<>();
        this.mShortTextFlag = shortText;
        this.mDisplayFlag = GridViewDataListAdapter.DISPLAY_ROW_MEMBER.ROW;
        // 计算行列数
        this.mFirstRowColumnCount = calculateFirstRowColumnCount(totalSpan, cellList);

        if (allRowDisplay)
        {
            if (cellList != null)
            {
                this.mDisplayColumnCount = cellList.size();
            }
            else
            {
                this.mDisplayColumnCount = 0;
            }
        }
        else
        {
            mDisplayColumnCount = this.mFirstRowColumnCount;
        }
    }
    @Override
    public GridViewDataRowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_data_row_cell, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final GridViewDataRowAdapter.ViewHolder holder, final int position) {
        if (this.mShortTextFlag)
        {
            holder.mTextView.setText(cellList.get(position).getShortText());
        }
        else{
            holder.mTextView.setText(cellList.get(position).getText());
        }
        holder.itemView.setTag(cellList.get(position));
        holder.mTextView.setGravity(cellList.get(position).getGravity());

        if (cellList.get(position).getColNumber() < mFirstRowColumnCount)
        {
            holder.mHeaderView.setText("");
            holder.mHeaderView.setVisibility(View.GONE);
            holder.mTextView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        xDown= event.getX();
//                        yDown = event.getY();
                        if (mPreviousUpEvent != null
                                && mCurrentDownEvent != null
                                && isConsideredDoubleTap(mCurrentDownEvent,
                                mPreviousUpEvent, event)) {
//                            Toast.makeText(mContext, "double clicked.",
//                                    Toast.LENGTH_SHORT).show();
                            if (mDisplayColumnCount < cellList.size())
                            {
                                mDisplayColumnCount = cellList.size();
                            }
                            else
                            {
                                mDisplayColumnCount = mFirstRowColumnCount;
                            }

                            mOnGridViewRowCellActionListener.onFirstRowDoubleClicked(GridViewDataRowAdapter.this, v);
                        }
                        mCurrentDownEvent = MotionEvent.obtain(event);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        mPreviousUpEvent = MotionEvent.obtain(event);
//
//                        //获取松开时的x坐标
//                        if(isLongClickModule){
//                            isLongClickModule = false;
//                            isLongClicking = false;
//                        }
//                        xUp = event.getX();
//                        //按下和松开绝对值差当大于20时滑动，否则不显示
//                        if ((xUp - xDown) > 20)
//                        {
//                            //添加要处理的内容
//                            Toast.makeText(mContext, "moved: right" + event.getX(),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                        else if((xUp - xDown ) < -20)
//                        {
//                            //添加要处理的内容
//                            Toast.makeText(mContext, "moved: left" + event.getX(),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                        else if( 0 == (xDown - xUp))
//                        {
//                            int viewWidth = v.getWidth();
//                            if( xDown < viewWidth/3 )
//                            {
//                                //靠左点击
//                            }
//                            else if(xDown > viewWidth/3 && xDown < viewWidth * 2 /3)
//                            {
//                                //中间点击
//                            }
//                            else
//                            {
//                                //靠右点击
//                            }
//                        }
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE)
                    {
                        //当滑动时背景为选中状态 //检测是否长按,在非长按时检测
//                        if(!isLongClickModule)
//                        {
//                            isLongClickModule = isLongPressed(xDown, yDown, event.getX(),
//                                    event.getY(),event.getDownTime() ,event.getEventTime(),300);
//                        }
//                        if(isLongClickModule && !isLongClicking){
//                            //处理长按事件
//                            isLongClicking = true; }
                    }
                    else
                    {
                        return false;
                    }

                    return true;
                }
            });
        }
        else
        {
            holder.mHeaderView.setText(mHeaders.get(position).getText());
            holder.mHeaderView.setVisibility(View.VISIBLE);
        }
        // 省略字符时，Toast显示
        if (this.mShortTextFlag)
        {
            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, cellList.get(position).getText(),
                            Toast.LENGTH_SHORT).show();

                }
            });
        }


    }

    @Override
    public int getItemCount() {
//        return cellList.size();
        return mDisplayColumnCount;
    }
    private boolean isLongPressed(float lastX,float lastY,
                                  float thisX,float thisY,
                                  long lastDownTime,long thisEventTime,
                                  long longPressTime){
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        long intervalTime = thisEventTime - lastDownTime;
//        if(offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime){
        if(offsetX <= 10 && intervalTime >= longPressTime){
            return true;
        }
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mHeaderView;
        public TextView mTextView;
        public ViewHolder(View view) {
            super(view);
            mHeaderView = (TextView) view.findViewById(R.id.grid_view_data_row_cell_header_tv);
            mTextView = (TextView) view.findViewById(R.id.grid_view_data_row_cell_tv);
//            mTextView.onTouchEvent()
        }
    }

    public List<GridViewCellBean> getSelectData(){
        return selectList;
    }

    private int calculateFirstRowColumnCount(int totalSpan, List<GridViewCellBean> cells)
    {
        int result = 0;
        // 计算行列数
        int spanCount = 0;
        for(int i=0; i < cells.size(); i++)
        {
            spanCount += cells.get(i).getColSpan();
            if (spanCount >= totalSpan)
            {
                result = i+1;
                break;
            }
        }
        return result;
    }

    private final int DOUBLE_TAP_TIMEOUT = 200;
    private MotionEvent mCurrentDownEvent;
    private MotionEvent mPreviousUpEvent;

    private boolean isConsideredDoubleTap(MotionEvent firstDown,
                                          MotionEvent firstUp, MotionEvent secondDown) {
        if (secondDown.getEventTime() - firstUp.getEventTime() > DOUBLE_TAP_TIMEOUT) {
            return false;
        }
        int deltaX = (int) firstUp.getX() - (int) secondDown.getX();
        int deltaY = (int) firstUp.getY() - (int) secondDown.getY();
        return deltaX * deltaX + deltaY * deltaY < 10000;
    }

    public interface OnGridViewRowCellActionListener {
        /**
         * Called when the click of double touched.
         * All row will be filled by this listener.
         * @param sender Current object.
         * @param v The view whose state has changed.
         */
        void onFirstRowDoubleClicked(Object sender, View v);
    }
}
