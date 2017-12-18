package com.liu.customizedgridview.gridview;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;

import com.liu.customizedgridview.R;
import com.liu.customizedgridview.utils.DisplayUtils;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
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
    private ListView mDataListLeft;
    private ListView mDataListRight;
    private int parentContainerWidth = 0;

    private View mRootView;
    private boolean mLoading;
    GridViewHeaderListAdapter mGridViewHeaderRightAdapter;
    GridViewDataListAdapter mGridViewDataRightAdapter;
    private Activity mContext;
    private final int mViewId;
    private List<GridViewRowBean> mHeaders;
    private List<GridViewRowBean> mRowDatas;
    private boolean mAllRowExpandFlag;
    private boolean mWrapRowFlag;
    private boolean mAutoFits;
    private boolean mShortTextFlag;
    private int mTotalColumnSpan;
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
    }

    /* Init Grid View */
    private void loadWrapGridView() {
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
                    mTotalColumnSpan += initColLength[i];
                }
                // 填补空白单元格
                mTotalColumnSpan += initColLength.length * CustomizedGridView.CELL_BLANK_LENGTH;
                this.calculateColSpan(this.mHeaders, this.mRowDatas, initColLength);
            }
        }

        mGridViewLayout = (CustomizedGridViewLayout) mRootView.findViewById(mViewId);
        mGridViewLayout.setGridView(this);

        // 左抬头

        // 右抬头
        mHeaderListRight = (ListView) mGridViewLayout.findViewById(R.id.grid_view_header_right_rows);
        mGridViewHeaderRightAdapter =
                new GridViewHeaderListAdapter(
                        mContext, mHeaders, mRowDatas, mWrapRowFlag,
                        mTotalColumnSpan, mAllRowExpandFlag, mShortTextFlag, null);
        mHeaderListRight.setAdapter(mGridViewHeaderRightAdapter);

        // 左数据

        // 右数据
        mDataListRight = (ListView) mGridViewLayout.findViewById(R.id.grid_view_data_right_rows);
        mGridViewDataRightAdapter = new GridViewDataListAdapter(this.mContext,
                mHeaders,
                mRowDatas,
                this.mWrapRowFlag,
                this.mTotalColumnSpan,
                this.mAllRowExpandFlag,
                this.mAutoFits,
                this.mShortTextFlag,
                null);
        mDataListRight.setAdapter(mGridViewDataRightAdapter);


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
        /**
         * @return Instance of {@link CustomizedGridView} initiated with builder settings
         */
        public CustomizedGridView build() {
            CustomizedGridView gridView = new CustomizedGridView(this);
            gridView.loadWrapGridView();
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
