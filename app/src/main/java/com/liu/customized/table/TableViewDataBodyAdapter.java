package com.liu.customized.table;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.liu.customized.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TableViewDataBodyAdapter extends RecyclerView.Adapter<TableViewDataBodyAdapter.ViewHolder> {
    private HScrollView.ScrollViewListener mScrollViewListener;
    public void setScrollViewListener(HScrollView.ScrollViewListener listener)
    {
        this.mScrollViewListener = listener;
    }
    private VRecyclerView.OnScrollPositionToEndListener mOnScrollPositionToEndListener;
    public void setOnScrollPositionToEndListener(VRecyclerView.OnScrollPositionToEndListener listener) {
        mOnScrollPositionToEndListener = listener;
    }

    private TableViewDataBodyItemAdapter mLeftTableViewDataBodyItemAdapter;
    private TableViewDataBodyItemAdapter mRightTableViewDataBodyItemAdapter;
    public void notifyRowDataChanged()
    {
        if (mLeftTableViewDataBodyItemAdapter != null)
        {
            mLeftTableViewDataBodyItemAdapter.setRowDatas(mLeftRowDatas);
            mLeftTableViewDataBodyItemAdapter.notifyDataSetChanged();
        }
        if (mRightTableViewDataBodyItemAdapter != null)
        {
            mRightTableViewDataBodyItemAdapter.setRowDatas(mRightRowDatas);
            mRightTableViewDataBodyItemAdapter.notifyDataSetChanged();
        }
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

    private OnViewLoadOverListener mOnViewLoadOverListener;
    public void setOnViewLoadOverListener(OnViewLoadOverListener listener)
    {
        mOnViewLoadOverListener = listener;
    }

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
        mLeftTableViewDataBodyItemAdapter =
                    new TableViewDataBodyItemAdapter(mContext,
                            mFixRowCount,
                            0,
                            mLeftRowDatas,
                            mTableFieldWidth,
                            mTableFieldHeight);
            holder.mDataLeftView.setAdapter(mLeftTableViewDataBodyItemAdapter);
//        holder.mDataLeftView.addItemDecoration(new TableViewDecoration(mContext, TableViewDecoration.HORIZONTAL_LIST));

        LinearLayoutManager rightLayoutManager = new LinearLayoutManager(mContext);
        rightLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        holder.mDataRightView.setLayoutManager(rightLayoutManager);
        mRightTableViewDataBodyItemAdapter =
                    new TableViewDataBodyItemAdapter(mContext,
                            mFixRowCount,
                            mFixColumnCount,
                            mRightRowDatas,
                            mTableFieldWidth,
                            mTableFieldHeight);
        holder.mDataRightView.setAdapter(mRightTableViewDataBodyItemAdapter);
//        holder.mDataRightView.addItemDecoration(new TableViewDecoration(mContext, TableViewDecoration.VERTICAL_LIST));

        holder.mDataRightView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
                public void onGlobalLayout() {
                    //At this point the layout is complete and the 
                    //dimensions of recyclerView and any child views are known.
                    if (mOnViewLoadOverListener != null)
                    {
                        mOnViewLoadOverListener.onViewLoadOver();
                    }
                }
            });
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

    public interface OnViewLoadOverListener {
        void onViewLoadOver();
    }


}
