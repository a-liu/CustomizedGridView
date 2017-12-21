package com.liu.customized.table;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.liu.customized.R;

import java.util.List;


public class TableViewDataBodyAdapter extends RecyclerView.Adapter<TableViewDataBodyAdapter.ViewHolder> {
    private HScrollView.ScrollViewListener mScrollViewListener;
    public void setScrollViewListener(HScrollView.ScrollViewListener listener)
    {
        this.mScrollViewListener = listener;
    }
    private List<TableViewRowBean> mLeftRowDatas;
    private List<TableViewRowBean> mRightRowDatas;

    public void setLeftRowDatas(List<TableViewRowBean> datas)
    {
        mLeftRowDatas = datas;
    }

    public void setRightRowDatas(List<TableViewRowBean> datas)
    {
        mRightRowDatas = datas;
    }
    private boolean mShortTextFlag;
    private Activity mContext;
    private int mDisplayColumnCount;
    private int mFirstRowColumnCount;
    private boolean mWrapRowFlag;
    private List<TableViewCellBean> mHeaders;
    private float xDown,yDown, xUp;
    private boolean isLongClickModule = false;
    private boolean isLongClicking = false;
    private int mFixColumnCount;
    private int mFixRowCount;

    private int[] mTableFieldWidth;
    private int[] mTableFieldHeight;

    public TableViewDataBodyAdapter(Activity context,
                                    int fixRowCount,
                                    int fixColumnCount,
                                    List<TableViewRowBean> leftRowDatas,
                                    List<TableViewRowBean> rightRowDatas,
                                    int[] tableFieldWidth,
                                    int[] tableFieldHeight
                                    ) {
        this.mLeftRowDatas = leftRowDatas;
        this.mRightRowDatas = rightRowDatas;
        this.mFixColumnCount = fixColumnCount;
        this.mContext = context;
        this.mFixRowCount = fixRowCount;
        this.mTableFieldWidth = tableFieldWidth;
        this.mTableFieldHeight = tableFieldHeight;
    }


    @Override
    public TableViewDataBodyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_view_data_body, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(final TableViewDataBodyAdapter.ViewHolder holder, final int position) {
        LinearLayoutManager leftLayoutManager = new LinearLayoutManager(mContext);
        leftLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        holder.mDataLeftView.setLayoutManager(leftLayoutManager);
        TableViewDataBodyItemAdapter leftAdapter =
                new TableViewDataBodyItemAdapter(mContext,
                        mFixRowCount,
                        0,
                        mLeftRowDatas,
                        mTableFieldWidth,
                        mTableFieldHeight);
        holder.mDataLeftView.setAdapter(leftAdapter);

        LinearLayoutManager rightLayoutManager = new LinearLayoutManager(mContext);
        rightLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        holder.mDataRightView.setLayoutManager(rightLayoutManager);
        TableViewDataBodyItemAdapter rightAdapter =
                new TableViewDataBodyItemAdapter(mContext,
                        mFixRowCount,
                        mFixColumnCount,
                        mRightRowDatas,
                        mTableFieldWidth,
                        mTableFieldHeight);
        holder.mDataRightView.setAdapter(rightAdapter);

        holder.mHorizontalScroller.setScrollViewListener(new HScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(HorizontalScrollView scrollView, int x, int y, int oldX, int oldY) {
                if (mScrollViewListener != null)
                {
                    mScrollViewListener.onScrollChanged(scrollView, x, y, oldX, oldY);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        // 一个数据对象
        return 1;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public RecyclerView mDataLeftView;
        public RecyclerView mDataRightView;
        public HScrollView mHorizontalScroller;
        public ViewHolder(View view) {
            super(view);
            mDataLeftView = (RecyclerView) view.findViewById(R.id.customized_table_body_left_view);
            mDataRightView = (RecyclerView) view.findViewById(R.id.customized_table_body_right_view);
            mHorizontalScroller = (HScrollView) view.findViewById(R.id.customized_table_body_right_h_scroll);
        }
    }



}
