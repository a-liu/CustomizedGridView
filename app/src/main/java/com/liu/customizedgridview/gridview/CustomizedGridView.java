package com.liu.customizedgridview.gridview;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.liu.customizedgridview.R;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by liu.jianfei on 2017/12/14.
 */

public final class CustomizedGridView {

    //region private Fields
    private CustomizedGridViewLayout mGridViewLayout;
    private CustomizedGridView mCustomizedGridView;

    private ListView mHeaderListLeft;
    private ListView mHeaderListRight;
    private ListView mDataListLeft;
    private ListView mDataListRight;

    private View mRootView;
    private boolean mLoading;
    GridViewDataListAdapter mGridViewAdapter;
    private Activity mContext;
    private final int mViewId;
    private List<GridViewRowBean> mHeaders;
    private List<GridViewRowBean> mRowDatas;
    private boolean mAllRowExpandFlag;
    private boolean mWrapRowFlag;
    private boolean mAutoFits;
    private boolean mShortTextFlag;
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
        mGridViewLayout = (CustomizedGridViewLayout) mRootView.findViewById(mViewId);
        mGridViewLayout.setGridView(this);
        mDataListRight = (ListView) mGridViewLayout.findViewById(R.id.grid_view_data_right_rows);
        mGridViewAdapter = new GridViewDataListAdapter(this.mContext,
                mHeaders,
                mRowDatas,
                this.mWrapRowFlag,
                this.mAllRowExpandFlag,
                this.mAutoFits,
                this.mShortTextFlag,
                null);
        mDataListRight.setAdapter(mGridViewAdapter);
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
