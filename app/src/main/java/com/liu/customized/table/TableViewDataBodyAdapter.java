package com.liu.customized.table;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liu.customized.R;
import com.liu.utils.DisplayUtils;

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

    private Activity mContext;
    private int mFixColumnCount;
    private int mFixRowCount;

    private int[] mTableFieldWidth;
    private int[] mTableFieldHeight;

//    private OnViewLoadOverListener mOnViewLoadOverListener;
//    public void setOnViewLoadOverListener(OnViewLoadOverListener listener)
//    {
//        mOnViewLoadOverListener = listener;
//    }

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
        holder.mDataLeftView.setFocusable(false);
        if (mLeftTableViewDataBodyItemAdapter == null)
        {
            mLeftTableViewDataBodyItemAdapter =
                    new TableViewDataBodyItemAdapter(mContext,
                            mFixRowCount,
                            0,
                            mLeftRowDatas,
                            mTableFieldWidth,
                            mTableFieldHeight);
            holder.mDataLeftView.setAdapter(mLeftTableViewDataBodyItemAdapter);
        }
        else
        {
            mLeftTableViewDataBodyItemAdapter.setRowDatas(mLeftRowDatas);
            mLeftTableViewDataBodyItemAdapter.notifyDataSetChanged();
        }

        LinearLayoutManager rightLayoutManager = new LinearLayoutManager(mContext);
        rightLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        holder.mDataRightView.setLayoutManager(rightLayoutManager);
        holder.mDataRightView.setFocusable(false);
        if (mRightTableViewDataBodyItemAdapter == null)
        {
            mRightTableViewDataBodyItemAdapter =
                    new TableViewDataBodyItemAdapter(mContext,
                            mFixRowCount,
                            mFixColumnCount,
                            mRightRowDatas,
                            mTableFieldWidth,
                            mTableFieldHeight);
            holder.mDataRightView.setAdapter(mRightTableViewDataBodyItemAdapter);
        }
        else
        {
            mRightTableViewDataBodyItemAdapter.setRowDatas(mRightRowDatas);
            mRightTableViewDataBodyItemAdapter.notifyDataSetChanged();
        }

        // 水平滑动，标题同步
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

//    public interface OnViewLoadOverListener {
//        void onViewLoadOver();
//    }


}
