package com.liu.customized.table;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.liu.customized.R;
import com.liu.utils.DisplayUtils;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by liu.jianfei on 2017/12/14.
 */

public final class CustomizedTableView {
    /**
     * 单元格中预设空白长度
     */
    public static final int CELL_BLANK_LENGTH = 0;
    public static final int CELL_FONT_SIZE = 16;
    public static final int DATA_ROW_HEIGHT = 93;
    public static final int HEADER_ROW_HEIGHT = 150;
    //region private Fields
    private CustomizedTableViewLayout mGridViewLayout;

    private ListView mHeaderListLeft;
    private ListView mHeaderListRight;
    private VListView mDataListLeft;
    private VListView mDataListRight;
    private TableViewDataListAdapter mGridViewDataRightAdapter;
    private TableViewDataListAdapter mGridViewDataLeftAdapter;

    private HScrollView mRightHeaderHScroll;
    private HScrollView mRightDataHScroll;
    private VScrollView mLeftDataVScroll;
    private VScrollView mRightDataVScroll;
//    private HScrollView.ScrollViewListener mRightHeaderHScrollListener;
//    private HScrollView.ScrollViewListener mRightDataHScrollListener;
//    private VScrollView.ScrollViewListener mLeftDataVScrollListener;
//    private VScrollView.ScrollViewListener mRightDataVScrollListener;
    private int parentContainerWidth = 0;

    private View mRootView;
    private boolean mLoading;
    private Activity mContext;
    private final int mViewId;
    private List<TableViewRowBean> mHeaders;
    private List<TableViewRowBean> mRowDatas;
    private List<TableViewRowBean> mLeftHeaders;
    private List<TableViewRowBean> mRightHeaders;
    private List<TableViewRowBean> mLeftRowDatas;
    private List<TableViewRowBean> mRightRowDatas;
    private boolean mAllRowExpandFlag;
    private boolean mWrapRowFlag;
    private boolean mAutoFits;
    private boolean mShortTextFlag;
    private int mTotalColumnSpan;
    private int mLeftHeaderTotalColumnSpan;
    private int mRightHeaderTotalColumnSpan;
    private int mLeftDataTotalColumnSpan;
    private int mRightDataTotalColumnSpan;
    private int mFixColumnCount;
    //endregion

    /**
     * Private Constructor to insure WrapGridView can't be initiated the default way
     */
    CustomizedTableView(Builder builder) {
        this.mContext = builder.mContext;
        this.mRootView = builder.mRootView;
        this.mViewId = builder.mViewId;
        this.mHeaders = builder.mHeaders;
        this.mRowDatas = builder.mRowDatas;
        this.mWrapRowFlag = builder.mWrapRowFlag;
        this.mAllRowExpandFlag = builder.mAllRowExpandFlag;
        this.mAutoFits = builder.mAutoFits;
        this.mShortTextFlag = builder.mShortTextFlag;
        this.mFixColumnCount = builder.mfixColumnCount;
    }

    /* Init Grid View */
    private void loadGridView() {
        int height = 0;
        int width = 0;
        this.prevSetting();


        mGridViewLayout = (CustomizedTableViewLayout) mRootView.findViewById(mViewId);
        mGridViewLayout.setGridView(this);
        // 左抬头
        mHeaderListLeft = (ListView) mGridViewLayout.findViewById(R.id.grid_view_header_left_rows);
        if (!mWrapRowFlag)
        {
            width = (int)(mLeftHeaderTotalColumnSpan * DisplayUtils.getDisplayFontPx(CustomizedTableView.CELL_FONT_SIZE));
            mHeaderListLeft.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            TableViewHeaderListAdapter gridViewHeaderLeftAdapter =
                    new TableViewHeaderListAdapter(
                            mContext,
                            mLeftHeaders,
                            mLeftRowDatas,
                            0,
                            mWrapRowFlag,
                            mLeftHeaderTotalColumnSpan,
                            mAllRowExpandFlag,
                            mShortTextFlag, null);
            mHeaderListLeft.setAdapter(gridViewHeaderLeftAdapter);
        }
        else
        {
            mHeaderListLeft.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        }

        // 右抬头
        mHeaderListRight = (ListView) mGridViewLayout.findViewById(R.id.grid_view_header_right_rows);
        TableViewHeaderListAdapter gridViewHeaderRightAdapter =
                new TableViewHeaderListAdapter(
                        mContext,
                        mRightHeaders,
                        mRightRowDatas,
                        mFixColumnCount,
                        mWrapRowFlag,
                        mRightHeaderTotalColumnSpan,
                        mAllRowExpandFlag,
                        mShortTextFlag, null);
        gridViewHeaderRightAdapter.setOnGridViewDataSortListener(new TableViewHeaderListAdapter.OnGridViewDataSortListener()
        {
            @Override
            public void onDataSort(int position, int offset, TableViewBeanComparator.ORDER_TYPE orderType) {
                // 数据排序
                Collections.sort(mRowDatas, new TableViewBeanComparator(true, position+offset, orderType));

                // 行号设定
                resetFixRowNumbers();

                // 数据重新分割
                fixColumnSetting();
                mGridViewDataRightAdapter.setDataRows(mRightRowDatas);
                final TableViewDataListAdapter rightAdapter = mGridViewDataRightAdapter;
                Handler rightHandler= new Handler();
                rightHandler.post(new Runnable(){
                    @Override
                    public void run() {
                        rightAdapter.notifyDataSetChanged();
                    }
                });
                if (!mWrapRowFlag)
                {
                    mGridViewDataLeftAdapter.setDataRows(mLeftRowDatas);
                    final TableViewDataListAdapter leftAdapter = mGridViewDataLeftAdapter;

                    Handler leftHandler= new Handler();
                    leftHandler.post(new Runnable(){
                        @Override
                        public void run() {
                            leftAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
        mHeaderListRight.setAdapter(gridViewHeaderRightAdapter);
        // 左数据
        mDataListLeft = (VListView) mGridViewLayout.findViewById(R.id.grid_view_data_left_rows);
        if (!mWrapRowFlag)
        {
            // 整行显示时，全部加载
            height = (int)(mLeftRowDatas.size() * CustomizedTableView.DATA_ROW_HEIGHT);
            mDataListLeft.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));

            // 逐行加载
//            int parentHeight = DisplayUtils.getDisplayHight(this.mContext);
//            mDataListLeft.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentHeight - HEADER_ROW_HEIGHT));
//            mDataListLeft.setAdjustHeightListener(new VListView.AdjustHeightListener() {
//                @Override
//                public void adjustHeight(int height) {
//                    int totalHeight = (int)(mLeftRowDatas.size() * CustomizedGridView.DATA_ROW_HEIGHT);
//                    if (mDataListLeft.getHeight() + height < totalHeight)
//                    {
//                        mDataListLeft.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mDataListLeft.getHeight() + height));
//                    }
//                }
//            });
            mGridViewDataLeftAdapter = new TableViewDataListAdapter(this.mContext,
                    mLeftHeaders,
                    mLeftRowDatas,
                    0,
                    this.mWrapRowFlag,
                    this.mLeftDataTotalColumnSpan,
                    this.mAllRowExpandFlag,
                    this.mShortTextFlag,
                    null);
            mDataListLeft.setAdapter(mGridViewDataLeftAdapter);
            mDataListLeft.setListViewListener(new VListView.ListViewListener(){
                @Override
                public void onScrollChanged(ListView scrollView, int l, int t, int oldL, int oldT) {

                }

                @Override
                public void onScrollPositionFromTop(ListView scrollView, int position, int top) {
                    if (mDataListRight != null){
                        mDataListRight.smoothScrollToPositionFromTop(position, top);
                    }
                }

                @Override
                public void onScrollOver(ListView scrollView, VListView.SCROLL_DIRECTION direction) {
                    Toast.makeText(mContext, "Over：" + direction,
                            Toast.LENGTH_SHORT).show();
                }


            });

            //        mDataListLeft.setTouchEnable(false);
//        mDataListLeft.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (mDataListRight != null &&
//                    scrollState == SCROLL_STATE_IDLE ||
//                    scrollState == SCROLL_STATE_TOUCH_SCROLL  )
//                {
////                    View subView = view.getChildAt(0);
////                    if (subView != null)
////                    {
////                        int top = subView.getTop();
////                        mDataListRight.smoothScrollToPositionFromTop(view.getFirstVisiblePosition(), top);
////                    }
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem,
//                                 int visibleItemCount, int totalItemCount) {
//                if (mDataListRight != null)
//                {
////                    View subView = view.getChildAt(0);
////                    if (subView != null)
////                    {
////                        int top = subView.getTop();
////                        mDataListRight.smoothScrollToPositionFromTop(firstVisibleItem, top);
////                    }
//                }
//            }
//        });
////        mDataListLeft.setListViewListener(new VListView.ListViewListener(){
////            @Override
////            public void onScrollChanged(ListView scrollView, int l, int t, int oldL, int oldT) {
////                if (mDataListRight != null )
////                {
////                    View subView = scrollView.getChildAt(0);
////                    if (subView != null)
////                    {
////                        int top = subView.getTop();
////                        mDataListRight.smoothScrollToPositionFromTop(scrollView.getFirstVisiblePosition(), top);
////                    }
////                }
////            }
////        });
        }


        // 右数据
        mDataListRight = (VListView) mGridViewLayout.findViewById(R.id.grid_view_data_right_rows);
        if (!mWrapRowFlag)
        {
            // 整行显示时，全部加载
            height = (int)(mRightRowDatas.size() * CustomizedTableView.DATA_ROW_HEIGHT);
            mDataListRight.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
//            // 逐行加载
//            int parentHeight = DisplayUtils.getDisplayHight(this.mContext);
//            mDataListRight.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentHeight - HEADER_ROW_HEIGHT));
//            mDataListRight.setAdjustHeightListener(new VListView.AdjustHeightListener() {
//                @Override
//                public void adjustHeight(int height) {
//                    int totalHeight = (int)(mRightRowDatas.size() * CustomizedGridView.DATA_ROW_HEIGHT);
//                    if (mDataListRight.getHeight() + height < totalHeight)
//                    {
//                        mDataListRight.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mDataListRight.getHeight() + height));
//                    }
//                }
//            });
        }
        else {
            // 满屏显示
            int parentHeight = DisplayUtils.getDisplayHight(this.mContext);
            mDataListRight.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentHeight - HEADER_ROW_HEIGHT));
        }
        if (mRightRowDatas != null)
        {
            mGridViewDataRightAdapter = new TableViewDataListAdapter(this.mContext,
                    mRightHeaders,
                    mRightRowDatas,
                    mFixColumnCount,
                    this.mWrapRowFlag,
                    this.mRightDataTotalColumnSpan,
                    this.mAllRowExpandFlag,
                    this.mShortTextFlag,
                    null);
            mDataListRight.setAdapter(mGridViewDataRightAdapter);
            mDataListRight.setListViewListener(new VListView.ListViewListener() {
                @Override
                public void onScrollChanged(ListView scrollView, int l, int t, int oldL, int oldT) {

                }

                @Override
                public void onScrollPositionFromTop(ListView scrollView, int position, int top) {
                    if (mDataListLeft != null){
                        mDataListLeft.smoothScrollToPositionFromTop(position, top);
                    }
                }

                @Override
                public void onScrollOver(ListView scrollView, VListView.SCROLL_DIRECTION direction) {
                    Toast.makeText(mContext, "Over：" + direction,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 滚动条
        if (!mWrapRowFlag)
        {
            mRightHeaderHScroll = (HScrollView) mGridViewLayout.findViewById(R.id.grid_view_header_right_h_scroll);
            mRightDataHScroll = (HScrollView) mGridViewLayout.findViewById(R.id.grid_view_data_right_h_scroll);
            HScrollView.ScrollViewListener rightDataHScrollListener = new HScrollView.ScrollViewListener() {
                @Override
                public void onScrollChanged(HorizontalScrollView scrollView, int x, int y, int oldX, int oldY) {

                    if (mRightHeaderHScroll != null)
                    {
                        mRightHeaderHScroll.smoothScrollTo(x, y);
                    }
                }
            };

            HScrollView.ScrollViewListener rightHeaderHScrollListener = new HScrollView.ScrollViewListener() {
                @Override
                public void onScrollChanged(HorizontalScrollView scrollView, int x, int y, int oldX, int oldY) {
                    if (mRightDataHScroll != null)
                    {
                        mRightDataHScroll.smoothScrollTo(x, y);
                    }
                }
            };

            mRightDataHScroll.setScrollViewListener(rightDataHScrollListener);
            mRightHeaderHScroll.setScrollViewListener(rightHeaderHScrollListener);

            mRightDataVScroll = (VScrollView) mGridViewLayout.findViewById(R.id.grid_view_data_right_v_scroll);
            mLeftDataVScroll = (VScrollView) mGridViewLayout.findViewById(R.id.grid_view_data_left_v_scroll);
            // 设定高度、宽度
            width = (int)(mLeftDataTotalColumnSpan * DisplayUtils.getDisplayFontPx(CustomizedTableView.CELL_FONT_SIZE));
            mLeftDataVScroll.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));


            VScrollView.ScrollViewListener leftDataVScrollListener = new VScrollView.ScrollViewListener() {
                @Override
                public void onScrollChanged(ScrollView scrollView, int x, int y, int oldX, int oldY) {
                    if (mRightDataVScroll != null)
                    {
                        mRightDataVScroll.smoothScrollTo(x, y);
                    }
                }
            };
            VScrollView.ScrollViewListener rightDataVScrollListener = new VScrollView.ScrollViewListener() {
                @Override
                public void onScrollChanged(ScrollView scrollView, int x, int y, int oldX, int oldY) {

                    if (mLeftDataVScroll != null)
                    {
                        mLeftDataVScroll.smoothScrollTo(x, y);
                    }
                }
            };
            mLeftDataVScroll.setScrollViewListener(leftDataVScrollListener);
            mRightDataVScroll.setScrollViewListener(rightDataVScrollListener);
        }
        else
        {
            mLeftDataVScroll = (VScrollView) mGridViewLayout.findViewById(R.id.grid_view_data_left_v_scroll);
            mLeftDataVScroll.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT));

            mRightHeaderHScroll = (HScrollView) mGridViewLayout.findViewById(R.id.grid_view_header_right_h_scroll);
            mRightHeaderHScroll.setToucheEnabled(false);
            mRightDataHScroll = (HScrollView) mGridViewLayout.findViewById(R.id.grid_view_data_right_h_scroll);
            mRightDataHScroll.setToucheEnabled(false);
        }



    }

//    public int px2dp(int px) {
//        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
//        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
//        return dp;
//    }
//    public int dp2px(int dp) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
//                mContext.getResources().getDisplayMetrics());
//    }

    private void resetFixRowNumbers()
    {
        for (int i=0; i< this.mRowDatas.size(); i++)
        {
            String value = String.valueOf(i + 1);
            this.mRowDatas.get(i).getColumnCells().get(0).setValue(value);
        }
    }

    private void addFixRowNumbers()
    {
        if (mFixColumnCount <= 0 || this.mWrapRowFlag)
        {
            return;
        }

        for (int i=0; i< this.mRowDatas.size(); i++)
        {
            TableViewRowBean row = this.mRowDatas.get(i);
            String id = "dataCol" + i;
            String value = String.valueOf(i + 1);
            TableViewCellBean cell = new TableViewCellBean(id, value);
            this.mRowDatas.get(i).getColumnCells().add(0, cell);
        }
        for (int i=0; i< this.mHeaders.size(); i++)
        {
            if (i == 0)
            {
                TableViewRowBean row = this.mHeaders.get(i);
                String id = "headerCol" + i;
                String value = " No. ";
                TableViewCellBean cell = new TableViewCellBean(id, value);
                this.mHeaders.get(i).getColumnCells().add(0, cell);
            }
            else
            {
                TableViewRowBean row = this.mHeaders.get(i);
                String id = "headerCol" + i;
                String value = "";
                TableViewCellBean cell = new TableViewCellBean(id, value);
                this.mHeaders.get(i).getColumnCells().add(0, cell);
            }
        }
    }

    private void prevSetting()
    {
        this.addFixRowNumbers();
        parentContainerWidth = DisplayUtils.getDisplayWidth(this.mContext);
        int[] initColLength = this.calculateColumnTextLength(this.mHeaders, this.mRowDatas);
        this.calculateRowColSetting(this.mRowDatas);
        if (this.mAutoFits)
        {
            if (this.mWrapRowFlag)
            {
                if(DisplayUtils.isPad(this.mContext))
                {
                    mTotalColumnSpan = (int)(parentContainerWidth / DisplayUtils.getDisplayFontPxForPad(CELL_FONT_SIZE));
                }
                else
                {
                    mTotalColumnSpan = (int)(parentContainerWidth / DisplayUtils.getDisplayFontPx(CELL_FONT_SIZE));
                }
                // 去除行号
                int numberLength = String.valueOf(this.mRowDatas.size()).length();
                if (numberLength < 5)
                {
                    numberLength = 5;
                }
                mTotalColumnSpan -= numberLength;

                this.calculateColSpan(this.mHeaders, this.mRowDatas, initColLength);
                mRightHeaderTotalColumnSpan += mTotalColumnSpan;
                mRightDataTotalColumnSpan += mTotalColumnSpan;
            } else {
                // 非折行时容器定宽显示
                for(int i=0; i< initColLength.length; i++)
                {
                    mTotalColumnSpan += initColLength[i] + CustomizedTableView.CELL_BLANK_LENGTH;
                    if (mFixColumnCount > 0)
                    {
                        if (i < mFixColumnCount)
                        {
                            mLeftHeaderTotalColumnSpan += initColLength[i] + CustomizedTableView.CELL_BLANK_LENGTH;
                            mLeftDataTotalColumnSpan += initColLength[i] + CustomizedTableView.CELL_BLANK_LENGTH;
                        }
                        else
                        {
                            mRightHeaderTotalColumnSpan += initColLength[i] + CustomizedTableView.CELL_BLANK_LENGTH;
                            mRightDataTotalColumnSpan += initColLength[i] + CustomizedTableView.CELL_BLANK_LENGTH;
                        }
                    }
                    else
                    {
                        mRightHeaderTotalColumnSpan += mTotalColumnSpan;
                        mRightDataTotalColumnSpan += mTotalColumnSpan;
                    }

                }
                this.calculateColSpan(this.mHeaders, this.mRowDatas, initColLength);
            }
        }
        // 冻结列的设定
        fixColumnSetting();
    }

    private void fixColumnSetting()
    {
        if (mFixColumnCount <= 0 || this.mWrapRowFlag)
        {
            mLeftHeaders = null;
            mLeftRowDatas = null;

            mRightHeaders = mHeaders;
            mRightRowDatas = mRowDatas;
            return;
        }

        mLeftHeaders = new ArrayList<TableViewRowBean>();
        mRightHeaders = new ArrayList<TableViewRowBean>();
        mLeftRowDatas = new ArrayList<TableViewRowBean>();
        mRightRowDatas = new ArrayList<TableViewRowBean>();

        for (int j=0; j< this.mHeaders.size(); j++)
        {
            TableViewRowBean row = new TableViewRowBean();
            row.setColumnCells(new ArrayList<TableViewCellBean>());
            row.setId(this.mHeaders.get(j).getId());
            row.setRowNumber(this.mHeaders.get(j).getRowNumber());
            mLeftHeaders.add(row);
            for(int i = 0; i< mFixColumnCount; i++)
            {
                row.getColumnCells().add(this.mHeaders.get(j).getColumnCells().get(i));
            }
        }

        for (int j=0; j< this.mRowDatas.size(); j++)
        {
            TableViewRowBean row = new TableViewRowBean();
            row.setColumnCells(new ArrayList<TableViewCellBean>());
            row.setId(this.mRowDatas.get(j).getId());
            row.setRowNumber(this.mRowDatas.get(j).getRowNumber());
            mLeftRowDatas.add(row);
            for(int i = 0; i< mFixColumnCount; i++)
            {
                row.getColumnCells().add(this.mRowDatas.get(j).getColumnCells().get(i));
            }
        }

        for(int j=0; j<this.mHeaders.size(); j++)
        {
            TableViewRowBean row = new TableViewRowBean();
            row.setColumnCells(new ArrayList<TableViewCellBean>());
            row.setId(this.mHeaders.get(j).getId());
            row.setRowNumber(this.mHeaders.get(j).getRowNumber());
            mRightHeaders.add(row);
            for(int i = mFixColumnCount; i< this.mHeaders.get(j).getColumnCells().size(); i++)
            {
                row.getColumnCells().add(this.mHeaders.get(j).getColumnCells().get(i));
            }
        }

        for(int j=0; j<this.mRowDatas.size(); j++)
        {
            TableViewRowBean row = new TableViewRowBean();
            row.setColumnCells(new ArrayList<TableViewCellBean>());
            row.setId(this.mRowDatas.get(j).getId());
            row.setRowNumber(this.mRowDatas.get(j).getRowNumber());
            mRightRowDatas.add(row);
            for(int i = mFixColumnCount; i< this.mRowDatas.get(j).getColumnCells().size(); i++)
            {
                row.getColumnCells().add(this.mRowDatas.get(j).getColumnCells().get(i));
            }
        }

    }

    private int[] calculateColumnTextLength(List<TableViewRowBean> rowHeaders, List<TableViewRowBean> rowDatas)
    {
        if (rowHeaders == null || rowHeaders.size() == 0)
        {
            return null;
        }
        int[] result = new int [rowHeaders.get(0).getColumnCells().size()];
        for (int i=0; i<rowHeaders.size(); i++) {
            for(int j=0;j<rowHeaders.get(i).getColumnCells().size(); j++)
            {
                String cellText = rowHeaders.get(i).getColumnCells().get(j).getText();
                int length = 0;
                try {
                    length = cellText.getBytes("GBK").length;
                } catch (UnsupportedEncodingException e) {
                    length = cellText.getBytes().length;
                }
                if (result[j] < length)
                {
                    result[j] = length;
                }
            }
        }
        for (int i=0; i<rowDatas.size(); i++)
        {
            for(int j=0;j<rowDatas.get(i).getColumnCells().size(); j++)
            {
                int length = 0;
                String cellText = "";
                if (this.mShortTextFlag)
                {
                    cellText = rowDatas.get(i).getColumnCells().get(j).getShortText();
                }
                else
                {
                    cellText = rowDatas.get(i).getColumnCells().get(j).getText();
                }
                try {
                    length = cellText.getBytes("GBK").length;
                } catch (UnsupportedEncodingException e) {
                    length = cellText.getBytes().length;
                }
                if (result[j] < length)
                {
                    result[j] = length;
                }
            }
        }
        return result;
    }

    /**
     * 设定各单元格行列值
     * @param rowDatas
     */
    private void calculateRowColSetting(List<TableViewRowBean> rowDatas) {
        for (int k = 0; k < rowDatas.size(); k++) {
            List<TableViewCellBean> cells = rowDatas.get(k).getColumnCells();
            for(int i=0; i< cells.size(); i++)
            {
                cells.get(i).setRowNumber(k);
                cells.get(i).setColNumber(i);
            }
        }
    }

    /**
     * 计算列宽
     * @param rowDatas
     */
    private void calculateColSpan(List<TableViewRowBean> headers, List<TableViewRowBean> rowDatas, int[] lengthDatas)
    {
        if (lengthDatas == null)
        {
            return;
        }
        for (int k=0; k< rowDatas.size(); k++)
        {
            List<TableViewCellBean> cells = rowDatas.get(k).getColumnCells();
            int colSpan = 0;
            boolean newLine = true;
            for(int i=0; i< cells.size(); i++)
            {
                int span = 0;
                int headerSpan = 0;
                if (!mWrapRowFlag)
                {
                    span = lengthDatas[i] + CustomizedTableView.CELL_BLANK_LENGTH;
                }
                else
                {
                    span = lengthDatas[i];
                }
                if (headers.get(0).getColumnCells().get(i).getText() != null && !newLine)
                {
                    try {
                        headerSpan = headers.get(0).getColumnCells().get(i).getText().getBytes("GBK").length + 1;
                    } catch (UnsupportedEncodingException e) {
                        headerSpan = headers.get(0).getColumnCells().get(i).getText().getBytes().length + 1;
                    }
                }
                colSpan = colSpan + span + headerSpan;
                if (i < cells.size() - 1)
                {
                    int nextSpan = 0;
                    int nextHeaderSpan = 0;
                    if (!mWrapRowFlag)
                    {
                        nextSpan = lengthDatas[i + 1] + CustomizedTableView.CELL_BLANK_LENGTH;
                    }
                    else
                    {
                        nextSpan = lengthDatas[+ 1];
                    }
                    if (headers.get(0).getColumnCells().get(i+1).getText() != null && !newLine)
                    {
                        try {
                            nextHeaderSpan = headers.get(0).getColumnCells().get(i+1).getText().getBytes("GBK").length + 1;
                        } catch (UnsupportedEncodingException e) {
                            nextHeaderSpan = headers.get(0).getColumnCells().get(i+1).getText().getBytes().length + 1;
                        }
                    }
                    nextSpan += nextHeaderSpan;
                    if (colSpan + nextSpan > mTotalColumnSpan)
                    {
                        span += mTotalColumnSpan - colSpan;
                        colSpan = 0;
                        if (newLine)
                        {
                            newLine = false;
                        }
                    }
                }
                else {
                    span += mTotalColumnSpan - colSpan;
                    colSpan = 0;
                    if (newLine)
                    {
                        newLine = false;
                    }
                }
                cells.get(i).setColSpan(span + headerSpan);
            }
        }
        for(int i=0; i<headers.size(); i++)
        {
            List<TableViewCellBean> cells = headers.get(i).getColumnCells();
            for(int j=0; j< cells.size(); j++)
            {
                cells.get(j).setColSpan(rowDatas.get(0).getColumnCells().get(j).getColSpan());
            }
        }
    }

    public void setOnLoadCompleteListener(CustomizedTableViewLayout.OnLoadCompleteListener listener)
    {
        if (this.mGridViewLayout != null)
        {
            mGridViewLayout.setOnLoadCompleteListener(listener);
        }
    }
    public static class Builder {
        private Activity mContext;
        private View mRootView;
        private final int mViewId;
        private List<TableViewRowBean> mHeaders;
        private List<TableViewRowBean> mRowDatas;
        private boolean mAllRowExpandFlag;
        private boolean mWrapRowFlag;
        private boolean mAutoFits;
        private boolean mShortTextFlag;
        private int mfixColumnCount;


        /**
         * @param activity pass the activity where WrapGridView is attached
         * @param viewId   the id specified for WrapGridViewView in your layout
         */
        public Builder(Activity activity, int viewId) {
            this.mContext = activity;
            this.mViewId = viewId;
            this.mRootView = activity.getWindow().getDecorView();
            this.initDefaultValues();
        }

        public Builder headers(List<TableViewRowBean> headers) {
            mHeaders = headers;
            return this;
        }

        public Builder rowDatas(List<TableViewRowBean> rowDatas) {
            this.mRowDatas = rowDatas;
            return this;
        }

        public Builder allRowExpand(boolean allRowExpand) {
            this.mAllRowExpandFlag = allRowExpand;
            return this;
        }

        public Builder shortText(boolean shortText) {
            this.mShortTextFlag = shortText;
            return this;
        }

        public Builder wrapRowFlag(boolean wrapRowFlag) {
            this.mWrapRowFlag = wrapRowFlag;
            return this;
        }

        public Builder fixColumnCount(int fixColumnCount) {
            this.mfixColumnCount = fixColumnCount;
            return this;
        }

        /**
         * @return Instance of {@link CustomizedTableView} initiated with builder settings
         */
        public CustomizedTableView build() {
            CustomizedTableView gridView = new CustomizedTableView(this);
            gridView.loadGridView();
            return gridView;
        }

        private void initDefaultValues() {
            mAllRowExpandFlag = false;
            mAutoFits = true;
            mShortTextFlag = false;
            mWrapRowFlag = false;
        }
    }

    private class InitializeDatesList extends AsyncTask<Void, Void, Void> {

        InitializeDatesList() {
        }

        @Override
        protected void onPreExecute() {
            mLoading = true;
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //ArrayList of dates is set with all the dates between
            //start and end date


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    private static class DateHandler extends Handler {

        private final WeakReference<CustomizedTableView> mWrapGridView;
        public Object data = null;
        public boolean immediate = true;

        public DateHandler(CustomizedTableView wrapGridView, Object defaultData) {
            this.mWrapGridView = new WeakReference<>(wrapGridView);
            this.data = defaultData;
        }

        @Override
        public void handleMessage(Message msg) {
            CustomizedTableView gridView = mWrapGridView.get();
            if (gridView != null) {
                gridView.mLoading = false;

            }
        }
    }

    private class WrapGridViewScrollListener extends RecyclerView.OnScrollListener {

        int lastSelectedItem = -1;
        final Runnable selectedItemRefresher = new SelectedItemRefresher();

        WrapGridViewScrollListener() {

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        }

        private class SelectedItemRefresher implements Runnable {

            SelectedItemRefresher() {

            }

            @Override
            public void run() {

            }
        }
    }
}
