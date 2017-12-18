package com.liu.customizedgridview.gridview;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.liu.customizedgridview.R;
import com.liu.customizedgridview.utils.DisplayUtils;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu.jianfei on 2017/12/14.
 */

public final class CustomizedGridView {
    /**
     * 单元格中预设空白长度
     */
    public static final int CELL_BLANK_LENGTH = 0;
    public static final int CELL_FONT_SIZE = 16;

    //region private Fields
    private CustomizedGridViewLayout mGridViewLayout;
    private CustomizedGridView mCustomizedGridView;

    private ListView mHeaderListLeft;
    private ListView mHeaderListRight;
    private VListView mDataListLeft;
    private VListView mDataListRight;
    private HScrollView mRightHeaderScroll;
    private HScrollView mRightDataScroll;
    private HScrollView.ScrollViewListener mRightHeaderScrollListener;
    private HScrollView.ScrollViewListener mRightDataScrollListener;
    private int parentContainerWidth = 0;

    private View mRootView;
    private boolean mLoading;
    private Activity mContext;
    private final int mViewId;
    private List<GridViewRowBean> mHeaders;
    private List<GridViewRowBean> mRowDatas;
    private List<GridViewRowBean> mLeftHeaders;
    private List<GridViewRowBean> mRightHeaders;
    private List<GridViewRowBean> mLeftRowDatas;
    private List<GridViewRowBean> mRightRowDatas;
    private boolean mAllRowExpandFlag;
    private boolean mWrapRowFlag;
    private boolean mAutoFits;
    private boolean mShortTextFlag;
    private int mTotalColumnSpan;
    private int mLeftHeaderTotalColumnSpan;
    private int mRightHeaderTotalColumnSpan;
    private int mLeftDataTotalColumnSpan;
    private int mRightDataTotalColumnSpan;
    private int mfixColumnCount;
    //endregion

    /**
     * Private Constructor to insure WrapGridView can't be initiated the default way
     */
    CustomizedGridView(Builder builder) {
        this.mContext = builder.mContext;
        this.mRootView = builder.mRootView;
        this.mViewId = builder.mViewId;
        this.mHeaders = builder.mHeaders;
        this.mRowDatas = builder.mRowDatas;
        this.mWrapRowFlag = builder.mWrapRowFlag;
        this.mAllRowExpandFlag = builder.mAllRowExpandFlag;
        this.mAutoFits = builder.mAutoFits;
        this.mShortTextFlag = builder.mShortTextFlag;
        this.mfixColumnCount = builder.mfixColumnCount;
    }

    /* Init Grid View */
    private void loadGridView() {
        this.prevSetting();

        mGridViewLayout = (CustomizedGridViewLayout) mRootView.findViewById(mViewId);
        mGridViewLayout.setGridView(this);
        int width = 0;
        // 左抬头
        mHeaderListLeft = (ListView) mGridViewLayout.findViewById(R.id.grid_view_header_left_rows);
        width = (int)(mLeftHeaderTotalColumnSpan * DisplayUtils.getDisplayFontPx(CustomizedGridView.CELL_FONT_SIZE));
        mHeaderListLeft.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        GridViewHeaderListAdapter gridViewHeaderLeftAdapter =
                new GridViewHeaderListAdapter(
                        mContext,
                        mLeftHeaders,
                        mLeftRowDatas,
                        mWrapRowFlag,
                        mLeftHeaderTotalColumnSpan,
                        mAllRowExpandFlag,
                        mShortTextFlag, null);
        mHeaderListLeft.setAdapter(gridViewHeaderLeftAdapter);


        // 右抬头
        mHeaderListRight = (ListView) mGridViewLayout.findViewById(R.id.grid_view_header_right_rows);
        GridViewHeaderListAdapter gridViewHeaderRightAdapter =
                new GridViewHeaderListAdapter(
                        mContext,
                        mRightHeaders,
                        mRightRowDatas,
                        mWrapRowFlag,
                        mRightHeaderTotalColumnSpan,
                        mAllRowExpandFlag,
                        mShortTextFlag, null);
        mHeaderListRight.setAdapter(gridViewHeaderRightAdapter);

        // 左数据
        mDataListLeft = (VListView) mGridViewLayout.findViewById(R.id.grid_view_data_left_rows);
        width = (int)(mLeftDataTotalColumnSpan * DisplayUtils.getDisplayFontPx(CustomizedGridView.CELL_FONT_SIZE));
        mDataListLeft.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        GridViewDataListAdapter gridViewDataLeftAdapter = new GridViewDataListAdapter(this.mContext,
                mLeftHeaders,
                mLeftRowDatas,
                this.mWrapRowFlag,
                this.mLeftDataTotalColumnSpan,
                this.mAllRowExpandFlag,
                this.mShortTextFlag,
                null);
        mDataListLeft.setAdapter(gridViewDataLeftAdapter);
        mDataListLeft.setListViewListener(new VListView.ListViewListener(){
            @Override
            public void onScrollChanged(ListView scrollView, int l, int t, int oldL, int oldT) {
                if (mDataListRight != null)
                {
                    mDataListRight.scrollTo(l, t);
                }
            }
        });
//        // 右数据
        mDataListRight = (VListView) mGridViewLayout.findViewById(R.id.grid_view_data_right_rows);
        GridViewDataListAdapter gridViewDataRightAdapter = new GridViewDataListAdapter(this.mContext,
                mRightHeaders,
                mRightRowDatas,
                this.mWrapRowFlag,
                this.mRightDataTotalColumnSpan,
                this.mAllRowExpandFlag,
                this.mShortTextFlag,
                null);
        mDataListRight.setAdapter(gridViewDataRightAdapter);
        mDataListRight.setListViewListener(new VListView.ListViewListener(){
            @Override
            public void onScrollChanged(ListView scrollView, int l, int t, int oldL, int oldT) {
                if (mDataListLeft != null)
                {
//                    mDataListLeft.smoothScrollToPosition(scrollView.getScrollY());

                    Log.d("GView", "onScrollChanged(" + " l:" + l + " t:" + t + " )");
                    Log.d("GView", "onScrollChangedXY(" + " l:" + mDataListRight.getScrollX() + " t:" + mDataListRight.getScrollY() + " )");
                    mDataListLeft.scrollTo(mDataListRight.getScrollX(), mDataListRight.getScrollY());
                }
            }
        });

        mRightHeaderScroll = (HScrollView) mGridViewLayout.findViewById(R.id.grid_view_header_right_scroll);
        mRightDataScroll = (HScrollView) mGridViewLayout.findViewById(R.id.grid_view_data_right_scroll);
        mRightDataScrollListener = new HScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(HorizontalScrollView scrollView, int x, int y, int oldX, int oldY) {
//                if (mRightHeaderScrollListener != null)
//                {
//                    mRightHeaderScrollListener.onScrollChanged(mRightHeaderScroll, x, y, oldX, oldY);
//                }
                if (mRightHeaderScroll != null)
                {
                    mRightHeaderScroll.smoothScrollTo(x, y);
                }
            }
        };
        mRightHeaderScrollListener = new HScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(HorizontalScrollView scrollView, int x, int y, int oldX, int oldY) {
                if (mRightDataScroll != null)
                {
                    mRightDataScroll.smoothScrollTo(x, y);
//                    mRightDataScrollListener.onScrollChanged(mRightDataScroll, x, y, oldX, oldY);
                }

            }
        };

        mRightDataScroll.setScrollViewListener(mRightDataScrollListener);
        mRightHeaderScroll.setScrollViewListener(mRightHeaderScrollListener);
    }

    private void addFixRowNumbers()
    {
        if (mfixColumnCount <= 0 || this.mWrapRowFlag)
        {
            return;
        }

        for (int i=0; i< this.mRowDatas.size(); i++)
        {
            GridViewRowBean row = this.mRowDatas.get(i);
            String id = "dataCol" + i;
            String value = String.valueOf(i + 1);
            GridViewCellBean cell = new GridViewCellBean(id, value);
            this.mRowDatas.get(i).getColumnCells().add(0, cell);
        }
        for (int i=0; i< this.mHeaders.size(); i++)
        {
            if (i == 0)
            {
                GridViewRowBean row = this.mHeaders.get(i);
                String id = "headerCol" + i;
                String value = "No.";
                GridViewCellBean cell = new GridViewCellBean(id, value);
                this.mHeaders.get(i).getColumnCells().add(0, cell);
            }
            else
            {
                GridViewRowBean row = this.mHeaders.get(i);
                String id = "headerCol" + i;
                String value = "";
                GridViewCellBean cell = new GridViewCellBean(id, value);
                this.mHeaders.get(i).getColumnCells().add(0, cell);
            }
        }
    }

    private void prevSetting()
    {
        this.addFixRowNumbers();
        parentContainerWidth = DisplayUtils.getDisplayWidth(this.mContext);
        int[] initColLength = this.calculateColumnTextLength(this.mRowDatas);
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
                this.calculateColSpan(this.mHeaders, this.mRowDatas, initColLength);
            } else {
                // 非折行时容器定宽显示
                for(int i=0; i< initColLength.length; i++)
                {
                    mTotalColumnSpan += initColLength[i] + CustomizedGridView.CELL_BLANK_LENGTH;
                    if (mfixColumnCount > 0)
                    {
                        if (i < mfixColumnCount)
                        {
                            mLeftHeaderTotalColumnSpan += initColLength[i] + CustomizedGridView.CELL_BLANK_LENGTH;
                            mLeftDataTotalColumnSpan += initColLength[i] + CustomizedGridView.CELL_BLANK_LENGTH;
                        }
                        else
                        {
                            mRightHeaderTotalColumnSpan += initColLength[i] + CustomizedGridView.CELL_BLANK_LENGTH;
                            mRightDataTotalColumnSpan += initColLength[i] + CustomizedGridView.CELL_BLANK_LENGTH;
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
        if (mfixColumnCount <= 0 || this.mWrapRowFlag)
        {
            mLeftHeaders = null;
            mLeftRowDatas = null;

            mRightHeaders = mHeaders;
            mRightRowDatas = mRowDatas;
            return;
        }

        mLeftHeaders = new ArrayList<GridViewRowBean>();
        mRightHeaders = new ArrayList<GridViewRowBean>();
        mLeftRowDatas = new ArrayList<GridViewRowBean>();
        mRightRowDatas = new ArrayList<GridViewRowBean>();

        for (int j=0; j< this.mHeaders.size(); j++)
        {
            GridViewRowBean row = new GridViewRowBean();
            row.setColumnCells(new ArrayList<GridViewCellBean>());
            row.setId(this.mHeaders.get(j).getId());
            row.setRowNumber(this.mHeaders.get(j).getRowNumber());
            mLeftHeaders.add(row);
            for(int i=0; i< mfixColumnCount; i++)
            {
                row.getColumnCells().add(this.mHeaders.get(j).getColumnCells().get(i));
//                this.mHeaders.get(j).getColumnCells().remove(this.mHeaders.get(j).getColumnCells().get(i));
            }
        }

        for (int j=0; j< this.mRowDatas.size(); j++)
        {
            GridViewRowBean row = new GridViewRowBean();
            row.setColumnCells(new ArrayList<GridViewCellBean>());
            row.setId(this.mRowDatas.get(j).getId());
            row.setRowNumber(this.mRowDatas.get(j).getRowNumber());
            mLeftRowDatas.add(row);
            for(int i=0; i< mfixColumnCount; i++)
            {
                row.getColumnCells().add(this.mRowDatas.get(j).getColumnCells().get(i));
//                this.mRowDatas.get(j).getColumnCells().remove(this.mRowDatas.get(j).getColumnCells().get(i));
            }
        }

        for(int j=0; j<this.mHeaders.size(); j++)
        {
            GridViewRowBean row = new GridViewRowBean();
            row.setColumnCells(new ArrayList<GridViewCellBean>());
            row.setId(this.mHeaders.get(j).getId());
            row.setRowNumber(this.mHeaders.get(j).getRowNumber());
            mRightHeaders.add(row);
            for(int i=mfixColumnCount; i< this.mHeaders.get(j).getColumnCells().size(); i++)
            {
                row.getColumnCells().add(this.mHeaders.get(j).getColumnCells().get(i));
//                this.mHeaders.get(j).getColumnCells().remove(this.mHeaders.get(j).getColumnCells().get(i));
            }
        }

        for(int j=0; j<this.mRowDatas.size(); j++)
        {
            GridViewRowBean row = new GridViewRowBean();
            row.setColumnCells(new ArrayList<GridViewCellBean>());
            row.setId(this.mRowDatas.get(j).getId());
            row.setRowNumber(this.mRowDatas.get(j).getRowNumber());
            mRightRowDatas.add(row);
            for(int i=mfixColumnCount; i< this.mRowDatas.get(j).getColumnCells().size(); i++)
            {
                row.getColumnCells().add(this.mRowDatas.get(j).getColumnCells().get(i));
//                this.mRowDatas.get(j).getColumnCells().remove(this.mRowDatas.get(j).getColumnCells().get(i));
            }
        }

    }

    private int[] calculateColumnTextLength(List<GridViewRowBean> rowDatas)
    {
        if (rowDatas == null || rowDatas.size() == 0)
        {
            return null;
        }
        int[] result = new int [rowDatas.get(0).getColumnCells().size()];
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
    private void calculateRowColSetting(List<GridViewRowBean> rowDatas) {
        for (int k = 0; k < rowDatas.size(); k++) {
            List<GridViewCellBean> cells = rowDatas.get(k).getColumnCells();
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
    private void calculateColSpan(List<GridViewRowBean> headers, List<GridViewRowBean> rowDatas, int[] lengthDatas)
    {
        if (lengthDatas == null)
        {
            return;
        }
        for (int k=0; k< rowDatas.size(); k++)
        {
            List<GridViewCellBean> cells = rowDatas.get(k).getColumnCells();
            int colSpan = 0;
            boolean newLine = true;
            for(int i=0; i< cells.size(); i++)
            {
                int span = 0;
                int headerSpan = 0;
                if (!mWrapRowFlag)
                {
                    span = lengthDatas[i] + CustomizedGridView.CELL_BLANK_LENGTH;
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
                        nextSpan = lengthDatas[i + 1] + CustomizedGridView.CELL_BLANK_LENGTH;
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
            List<GridViewCellBean> cells = headers.get(i).getColumnCells();
            for(int j=0; j< cells.size(); j++)
            {
                cells.get(j).setColSpan(rowDatas.get(0).getColumnCells().get(j).getColSpan());
            }
        }
    }

    public static class Builder {
        private Activity mContext;
        private View mRootView;
        private final int mViewId;
        private List<GridViewRowBean> mHeaders;
        private List<GridViewRowBean> mRowDatas;
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

        public Builder headers(List<GridViewRowBean> headers) {
            mHeaders = headers;
            return this;
        }

        public Builder rowDatas(List<GridViewRowBean> rowDatas) {
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
         * @return Instance of {@link CustomizedGridView} initiated with builder settings
         */
        public CustomizedGridView build() {
            CustomizedGridView gridView = new CustomizedGridView(this);
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

        private final WeakReference<CustomizedGridView> mWrapGridView;
        public Object data = null;
        public boolean immediate = true;

        public DateHandler(CustomizedGridView wrapGridView, Object defaultData) {
            this.mWrapGridView = new WeakReference<>(wrapGridView);
            this.data = defaultData;
        }

        @Override
        public void handleMessage(Message msg) {
            CustomizedGridView gridView = mWrapGridView.get();
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
