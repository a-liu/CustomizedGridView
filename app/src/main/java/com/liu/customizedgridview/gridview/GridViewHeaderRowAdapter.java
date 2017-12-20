package com.liu.customizedgridview.gridview;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.liu.customizedgridview.R;

import java.util.ArrayList;
import java.util.List;

public class GridViewHeaderRowAdapter extends RecyclerView.Adapter<GridViewHeaderRowAdapter.ViewHolder> {
    private GridViewDataListAdapter.DISPLAY_ROW_MEMBER mDisplayFlag;
    private List<GridViewCellBean> cellList;

    private boolean isSelected = false;

    private List<GridViewCellBean> selectList;

    private boolean mShortTextFlag;
    private Activity mContext;
    private boolean mWrapRowFlag;
    private int mDisplayColumnCount;
    private int mFirstRowColumnCount;
    private OnGridViewRowCellActionListener mOnGridViewRowCellActionListener;
    private List<GridViewCellBean> mHeaders;
    private int mFixColumnCount;
    public void setOnGridViewRowCellActionListener(OnGridViewRowCellActionListener listener)
    {
        mOnGridViewRowCellActionListener = listener;
    }
    public GridViewHeaderRowAdapter(Activity context, int totalSpan, int fixColumnCount, List<GridViewCellBean> headers, List<GridViewCellBean> cellList, boolean wrapRowFlag, int initColumnCount, boolean shortText) {
        this.cellList = cellList;
        this.mHeaders = headers;
        this.mContext = context;
        this.mFixColumnCount = fixColumnCount;
        this.mWrapRowFlag = wrapRowFlag;
        this.mDisplayColumnCount = initColumnCount;
        selectList = new ArrayList<>();
        this.mShortTextFlag = shortText;
        this.mDisplayFlag = GridViewDataListAdapter.DISPLAY_ROW_MEMBER.COLUMN;
        // 计算行列数
        this.mFirstRowColumnCount = calculateFirstRowColumnCount(totalSpan, cellList);
    }
    public GridViewHeaderRowAdapter(Activity context, int totalSpan, int fixColumnCount, List<GridViewCellBean> headers, List<GridViewCellBean> cellList, boolean wrapRowFlag, boolean allRowDisplay, boolean shortText) {
        this.cellList = cellList;
        this.mHeaders = headers;
        this.mContext = context;
        this.mFixColumnCount = fixColumnCount;
        this.mWrapRowFlag = wrapRowFlag;
        selectList = new ArrayList<>();
        this.mShortTextFlag = shortText;
        this.mDisplayFlag = GridViewDataListAdapter.DISPLAY_ROW_MEMBER.ROW;
        // 计算行列数
        this.mFirstRowColumnCount = calculateFirstRowColumnCount(totalSpan, cellList);

        if (allRowDisplay)
        {
            mDisplayColumnCount = this.mFirstRowColumnCount;
//            if (cellList != null)
//            {
//                this.mDisplayColumnCount = cellList.size();
//            }
//            else
//            {
//                this.mDisplayColumnCount = 0;
//            }
        }
        else
        {
            mDisplayColumnCount = this.mFirstRowColumnCount;
        }
    }
    @Override
    public GridViewHeaderRowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_header_row_cell, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final GridViewHeaderRowAdapter.ViewHolder holder, final int position) {
        if (this.mShortTextFlag)
        {
            holder.mTextView.setText(this.mHeaders.get(position).getShortText());
        }
        else{
            holder.mTextView.setText(this.mHeaders.get(position).getText());
        }
        holder.mTextView.setVisibility(View.VISIBLE);
        holder.mAscTextView.setText("△");
        holder.mDescTextView.setText("▽");
        holder.itemView.setTag(this.mHeaders.get(position));
        holder.mTextView.setGravity(this.mHeaders.get(position).getGravity());
        holder.mTextView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (mPreviousUpEvent != null
                                && mCurrentDownEvent != null
                                && isConsideredDoubleTap(mCurrentDownEvent,
                                mPreviousUpEvent, event)) {
                            if (holder.mAscTextView.getVisibility() == View.VISIBLE)
                            {
                                holder.mDescTextView.setVisibility(View.VISIBLE);
                                holder.mAscTextView.setVisibility(View.GONE);
                                mOnGridViewRowCellActionListener.onDataSort(GridViewHeaderRowAdapter.this, v, position, mFixColumnCount, GridViewBeanComparator.ORDER_TYPE.DESC);
                            }
                            else
                            {
                                holder.mDescTextView.setVisibility(View.GONE);
                                holder.mAscTextView.setVisibility(View.VISIBLE);
                                mOnGridViewRowCellActionListener.onDataSort(GridViewHeaderRowAdapter.this, v, position, mFixColumnCount, GridViewBeanComparator.ORDER_TYPE.ASC);
                            }

                        }
                        mCurrentDownEvent = MotionEvent.obtain(event);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        mPreviousUpEvent = MotionEvent.obtain(event);
                    }
                    return true;
                }
            });
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, mHeaders.get(position).getText(),
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mDisplayColumnCount;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public TextView mAscTextView;
        public TextView mDescTextView;
//        public ImageView mAscImage;
//        public ImageView mDescImage;
        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.grid_view_header_row_cell_tv);
            mAscTextView = (TextView) view.findViewById(R.id.grid_view_header_row_cell_asc_tv);
            mDescTextView = (TextView) view.findViewById(R.id.grid_view_header_row_cell_desc_tv);
//            mAscImage = (ImageView) view.findViewById(R.id.sort_asc_img);
//            mDescImage = (ImageView) view.findViewById(R.id.sort_desc_img);
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
         * @param position The data of set number.
         * @param positionOffset The column of offset.
         */
        void onFirstRowDoubleClicked(Object sender, View v, int position, int positionOffset);

        void onDataSort(Object sender, View v, int position, int positionOffset, GridViewBeanComparator.ORDER_TYPE orderType);
    }


}
