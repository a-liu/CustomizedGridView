package com.liu.customized.table;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liu.customized.R;
import com.liu.customized.dialog.WaitingDialog;
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
    public static final String WAIT_DIALOG_TAG = "WAIT_DIALOG";

    private OnTableViewRowHeaderActionListener mOnTableViewRowHeaderActionListener;
    public void setOnTableViewRowHeaderActionListener(OnTableViewRowHeaderActionListener listener)
    {
        mOnTableViewRowHeaderActionListener = listener;
    }
    /**
     * private Fields
     */
    private CustomizedTableViewLayout mGridViewLayout;

    private View mRootView;

    private Activity mContext;
    private final int mViewId;
    private List<TableViewRowBean> mHeaders;
    private List<TableViewRowBean> mRowDatas;
    private List<TableViewRowBean> mLeftHeaders;
    private List<TableViewRowBean> mRightHeaders;
    private List<TableViewRowBean> mLeftRowDatas;
    private List<TableViewRowBean> mRightRowDatas;
    private TableViewDataBodyAdapter mDataBodyAdapter;
    private boolean mWrapRowFlag;
    private boolean mAutoFits;
    private boolean mShortTextFlag;
    private static int mPageCount;
    private int mFixColumnCount;
    private int mFixRowCount;
    private int mMaxFieldWidth;
    private int mMaxFieldHeight;
    private int mMinFieldWidth;
    private int mMinFieldHeight;

    private int[] mTableFieldWidth;
    private int[] mTableFieldHeight;

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
        this.mAutoFits = builder.mAutoFits;
        this.mShortTextFlag = builder.mShortTextFlag;
        this.mFixColumnCount = builder.mFixColumnCount;
        if (mPageCount == 0)
        {
            mPageCount = builder.mPageCount;
        }
        this.mFixRowCount = builder.mFixRowCount;
        this.mMinFieldWidth = builder.mMinFieldWidth;
        this.mMinFieldHeight = builder.mMinFieldHeight;
        this.mMaxFieldWidth = builder.mMaxFieldWidth;
        this.mMaxFieldHeight = builder.mMaxFieldHeight;
    }

    /* Init Grid View */
    private void loadGridView() {
        mGridViewLayout = (CustomizedTableViewLayout) mRootView.findViewById(mViewId);

        mGridViewLayout.setGridView(this);

        // 调整标题列和数据列
        this.fixHeaderAndDataColumns();

        // 行号
        this.addFixRowNumbers();

        // 计算行高、列宽
        this.calculateTableFieldWidthAndHeight();

        // 固定列
        this.fixColumnSetting();

        // 左标题
        if (this.mLeftHeaders != null && this.mLeftHeaders.size() > 0) {
            LinearLayout layoutView = (LinearLayout)mGridViewLayout.findViewById(
                    R.id.customized_table_layout_left_header);
            layoutView.removeAllViews();
            for(int i=0; i<this.mLeftHeaders.size(); i++) {
                for (int j=0;j<this.mLeftHeaders.get(i).getColumnCells().size(); j++) {
                    final TextView view = this.getActualTableHeaderTextView(
                            this.mLeftHeaders.get(i).getColumnCells().get(j).getText() +
                                    TableViewConstants.HEADER_SORT_TEXT_ASC,
                            this.mLeftHeaders.get(i).getColumnCells().get(j).getGravity(),
                            i,
                            j);
                    layoutView.addView(view);
                    // 分隔线
                    if (j != this.mLeftHeaders.get(i).getColumnCells().size()) {
                        View splitView = new View(mContext);
                        ViewGroup.LayoutParams splitViewParmas = new ViewGroup.LayoutParams(DisplayUtils.dip2px(mContext, 1),
                                ViewGroup.LayoutParams.MATCH_PARENT);
                        splitView.setLayoutParams(splitViewParmas);
                        splitView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGridHeaderSplitLine));
                        layoutView.addView(splitView);
                    }

                    final int colIndex = j;
                    view.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                if (mPreviousUpEvent != null
                                        && mCurrentDownEvent != null
                                        && isConsideredDoubleTap(mCurrentDownEvent,
                                        mPreviousUpEvent, event)) {
                                    String text = view.getText() + "";
                                    if (text.endsWith(TableViewConstants.HEADER_SORT_TEXT_ASC))
                                    {
                                        text = text.substring(0, text.length() - 1);
                                        text += TableViewConstants.HEADER_SORT_TEXT_DESC;
                                        view.setText(text);
                                        if (mOnTableViewRowHeaderActionListener != null)
                                        {
                                            mOnTableViewRowHeaderActionListener.onDataSort(colIndex, mFixColumnCount, TableViewBeanComparator.ORDER_TYPE.DESC);
                                        }

                                    }
                                    else
                                    {
                                        text = text.substring(0, text.length() - 1);
                                        text += TableViewConstants.HEADER_SORT_TEXT_ASC;
                                        view.setText(text);
                                        if (mOnTableViewRowHeaderActionListener != null)
                                        {
                                            mOnTableViewRowHeaderActionListener.onDataSort(colIndex, mFixColumnCount, TableViewBeanComparator.ORDER_TYPE.ASC);
                                        }

                                    }
                                }
                                mCurrentDownEvent = MotionEvent.obtain(event);
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                mPreviousUpEvent = MotionEvent.obtain(event);
                            }
                            return true;
                        }
                    });
                }
            }
        }

        // 右标题
        if (this.mRightHeaders != null && this.mRightHeaders.size() > 0) {
            LinearLayout layoutView = (LinearLayout)mGridViewLayout.findViewById(
                    R.id.customized_table_layout_right_header);
            layoutView.removeAllViews();
            for(int i=0; i<this.mRightHeaders.size(); i++) {
                for (int j=0;j<this.mRightHeaders.get(i).getColumnCells().size(); j++) {
                    final TextView view = this.getActualTableHeaderTextView(
                            this.mRightHeaders.get(i).getColumnCells().get(j).getText() +
                                    TableViewConstants.HEADER_SORT_TEXT_ASC,
                            this.mRightHeaders.get(i).getColumnCells().get(j).getGravity(),
                            i,
                            j + mFixColumnCount);
                    layoutView.addView(view);
                    // 分隔线
                    if (j != this.mRightHeaders.get(i).getColumnCells().size() - 1) {
                        View splitView = new View(mContext);
                        ViewGroup.LayoutParams splitViewParmas = new ViewGroup.LayoutParams(DisplayUtils.dip2px(mContext, 1),
                                ViewGroup.LayoutParams.MATCH_PARENT);
                        splitView.setLayoutParams(splitViewParmas);
                        splitView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGridHeaderSplitLine));
                        layoutView.addView(splitView);
                    }
                    final int colIndex = j;
                    view.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                if (mPreviousUpEvent != null
                                        && mCurrentDownEvent != null
                                        && isConsideredDoubleTap(mCurrentDownEvent,
                                        mPreviousUpEvent, event)) {
                                    String text = view.getText() + "";
                                    if (text.endsWith(TableViewConstants.HEADER_SORT_TEXT_ASC))
                                    {
                                        text = text.substring(0, text.length() - 1);
                                        text += TableViewConstants.HEADER_SORT_TEXT_DESC;
                                        view.setText(text);
                                        mOnTableViewRowHeaderActionListener.onDataSort(colIndex, mFixColumnCount, TableViewBeanComparator.ORDER_TYPE.DESC);
                                    }
                                    else
                                    {
                                        text = text.substring(0, text.length() - 1);
                                        text += TableViewConstants.HEADER_SORT_TEXT_ASC;
                                        view.setText(text);
                                        mOnTableViewRowHeaderActionListener.onDataSort(colIndex, mFixColumnCount, TableViewBeanComparator.ORDER_TYPE.ASC);
                                    }
                                }
                                mCurrentDownEvent = MotionEvent.obtain(event);
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                mPreviousUpEvent = MotionEvent.obtain(event);
                            }
                            return true;
                        }
                    });
                }
            }
        }

        // 数据
        if (mRowDatas != null && this.mRowDatas.size() > 0 )
        {
            final VRecyclerView view = (VRecyclerView)mGridViewLayout.findViewById(
                    R.id.customized_table_layout_body);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            view.setLayoutManager(layoutManager);
            if (mDataBodyAdapter == null)
            {
                mDataBodyAdapter = new TableViewDataBodyAdapter(
                        mContext,
                        mFixRowCount,
                        mFixColumnCount,
                        mLeftRowDatas,
                        mRightRowDatas,
                        mTableFieldWidth,
                        mTableFieldHeight);
                view.setAdapter(mDataBodyAdapter);
            }
            else
            {
                mDataBodyAdapter.setLeftRowDatas(mLeftRowDatas);
                mDataBodyAdapter.setRightRowDatas(mRightRowDatas);
                mDataBodyAdapter.notifyRowDataChanged();
            }

//            mDataBodyAdapter.setOnViewLoadOverListener(new TableViewDataBodyAdapter.OnViewLoadOverListener() {
//                @Override
//                public void onViewLoadOver() {
//                    if (mOnLoadCompleteListener != null)
//                    {
//                        mOnLoadCompleteListener.onLoadComplete(view);
//                    }
//                }
//            });

            // 滑动到末尾
            view.setOnScrollPositionToEndListener(new VRecyclerView.OnScrollPositionToEndListener() {
                @Override
                public void onScrollPositionToEnd() {
                    if (mRowDatas.size() == mLeftRowDatas.size()){
                        return;
                    }
                    if (mLeftRowDatas.size() / TableViewConstants.PER_PAGE_OF_ITEM_COUNT < mPageCount)
                    {
                        return;
                    }
                    // 加载下一页数据
                    mPageCount += 1;

                    showWaitDialog();
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            // 行号设定
                            resetFixRowNumbers();

                            // 数据重新分割
                            fixColumnSetting();

                            mDataBodyAdapter.setLeftRowDatas(mLeftRowDatas);
                            mDataBodyAdapter.setRightRowDatas(mRightRowDatas);

                            mDataBodyAdapter.notifyRowDataChanged();
                        }
                    }, 1000);

                }
            });

            // 数据加载
            view.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            //At this point the layout is complete and the 
                            //dimensions of recyclerView and any child views are known.
                            if (mOnLoadCompleteListener != null)
                            {
                                mOnLoadCompleteListener.onLoadComplete(view);
                                Fragment frag = ((FragmentActivity)mContext).getSupportFragmentManager().findFragmentByTag( CustomizedTableView.WAIT_DIALOG_TAG);
                                if (frag != null && frag instanceof DialogFragment) {
                                    final DialogFragment dialog = (DialogFragment) frag;
                                    (new Handler()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                        }
                                    }, 2000);

                                }
                            }
                        }
                    });

            // 水平滚动条
            final HScrollView hHeaderScrollView = (HScrollView)mGridViewLayout.findViewById(
                    R.id.table_header_right_h_scroll);
            hHeaderScrollView.setToucheEnabled(false);
            hHeaderScrollView.smoothScrollTo(0, 0);
            mDataBodyAdapter.setScrollViewListener(new HScrollView.ScrollViewListener() {
                @Override
                public void onScrollChanged(HorizontalScrollView scrollView, int x, int y, int oldX, int oldY) {
                    if (hHeaderScrollView != null)
                    {
                        hHeaderScrollView.smoothScrollTo(x, y);
                    }
                }
            });
        }
        if (this.mRowDatas == null || this.mRowDatas.size() == 0)
        {
            if (mOnLoadCompleteListener != null)
            {
                mOnLoadCompleteListener.onLoadComplete(this.mRootView);
            }
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Fragment frag = ((FragmentActivity)mContext).getSupportFragmentManager().findFragmentByTag( CustomizedTableView.WAIT_DIALOG_TAG);
                    final DialogFragment dialog = (DialogFragment) frag;
                    dialog.dismiss();
                }
            }, 2000);
        }

        this.setOnTableViewRowHeaderActionListener(new OnTableViewRowHeaderActionListener() {
            @Override
            public void onDataSort(int position, int positionOffset, TableViewBeanComparator.ORDER_TYPE orderType) {
                if (mRowDatas == null || mRowDatas.size() == 0)
                {
                    return;
                }
                showWaitDialog();
                final int pos = position;
                final int offset = positionOffset;
                final TableViewBeanComparator.ORDER_TYPE type = orderType;
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // 数据排序
                        Collections.sort(mRowDatas, new TableViewBeanComparator(true, pos + offset, type));

                        // 行号设定
                        resetFixRowNumbers();

                        // 数据重新分割
                        fixColumnSetting();

                        mDataBodyAdapter.setLeftRowDatas(mLeftRowDatas);
                        mDataBodyAdapter.setRightRowDatas(mRightRowDatas);
                        mDataBodyAdapter.notifyRowDataChanged();
                    }
                }, 200);


            }
        });

    }

    private void showWaitDialog() {
        final WaitingDialog dialog = WaitingDialog.newInstance();
        dialog.show(((FragmentActivity)mContext).getSupportFragmentManager(), CustomizedTableView.WAIT_DIALOG_TAG);
    }

    private void fixHeaderAndDataColumns()
    {
        // 对齐标题列同数据列
        int colCount = 0;
        if (this.mHeaders == null)
        {
            this.mHeaders = new ArrayList<>();
            this.mHeaders.add(new TableViewRowBean());
            this.mHeaders.get(0).setColumnCells(new ArrayList<TableViewCellBean>());
        }
        else
        {
            if (this.mHeaders.size() == 0)
            {
                this.mHeaders.add(new TableViewRowBean());
                this.mHeaders.get(0).setColumnCells(new ArrayList<TableViewCellBean>());
            }
            else if (this.mHeaders.get(0).getColumnCells() == null)
            {
                this.mHeaders.get(0).setColumnCells(new ArrayList<TableViewCellBean>());
            }
        }
        if (colCount < this.mHeaders.get(0).getColumnCells().size())
        {
            colCount = this.mHeaders.get(0).getColumnCells().size();
        }
        boolean dataRowFixFlag = false;
        boolean headerRowFixFlag = false;
        if (this.mRowDatas != null && this.mRowDatas.size() > 0)
        {
            if (colCount < this.mRowDatas.get(0).getColumnCells().size())
            {
                headerRowFixFlag = true;
                colCount = this.mRowDatas.get(0).getColumnCells().size();
            }
        }
        for(int i=0; this.mRowDatas != null && i<this.mRowDatas.size(); i++)
        {
            if (this.mRowDatas.get(i).getColumnCells() != null &&
                    colCount < this.mRowDatas.get(i).getColumnCells().size())
            {
                colCount = this.mRowDatas.get(i).getColumnCells().size();
                dataRowFixFlag = true;
            }
            if (this.mRowDatas.get(i).getColumnCells() != null &&
                    colCount > this.mRowDatas.get(i).getColumnCells().size())
            {
                dataRowFixFlag = true;
            }
        }

        for(int i=0; headerRowFixFlag && i<this.mHeaders.size(); i++)
        {
            int fixCount = colCount - this.mHeaders.get(i).getColumnCells().size();
            for(int j=0; j<fixCount; j++)
            {
                this.mHeaders.get(i).getColumnCells().add(new TableViewCellBean("", ""));
            }
        }

        for(int i=0; dataRowFixFlag && i<this.mRowDatas.size(); i++)
        {
            int fixCount = colCount - this.mRowDatas.get(i).getColumnCells().size();
            for(int j=0; j<fixCount; j++)
            {
                this.mRowDatas.get(i).getColumnCells().add(new TableViewCellBean("", ""));
            }
        }
    }

    private void fixColumnSetting() {
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


        for (int j=0; this.mHeaders != null && j< this.mHeaders.size(); j++)
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

        for(int j=0; this.mHeaders != null && j<this.mHeaders.size(); j++)
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

        if (this.mRowDatas == null || this.mRowDatas.size() == 0)
        {
            return;
        }
        int itemCount = TableViewConstants.PER_PAGE_OF_ITEM_COUNT * mPageCount;
        if (itemCount > this.mRowDatas.size())
        {
            itemCount = this.mRowDatas.size();
        }
        for (int j=0; j< itemCount; j++)
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

        for(int j=0; j<itemCount; j++)
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

    private void calculateTableFieldWidthAndHeight()
    {

        if (mTableFieldWidth == null)
        {
            int colCount = 0;
            if (this.mHeaders != null && this.mHeaders.size() >0 &&
                    this.mHeaders.get(0).getColumnCells() != null )
            {
                if (colCount < this.mHeaders.get(0).getColumnCells().size())
                {
                    colCount = this.mHeaders.get(0).getColumnCells().size();
                }
            }
            if (this.mRowDatas != null && this.mRowDatas.size() >0 &&
                    this.mRowDatas.get(0).getColumnCells() != null )
            {
                if (colCount < this.mRowDatas.get(0).getColumnCells().size())
                {
                    colCount = this.mRowDatas.get(0).getColumnCells().size();
                }
            }
            mTableFieldWidth = new int[colCount];
        }
        int rowCount = 0;
        if (this.mRowDatas == null || this.mRowDatas.size() == 0)
        {
            if (this.mHeaders != null)
            {
                rowCount = this.mHeaders.size();
            }
        }
        else
        {
            if (this.mHeaders != null)
            {
                rowCount = this.mHeaders.size() + this.mRowDatas.size();
            }
            else
            {
                rowCount = this.mRowDatas.size();
            }

        }
        if (mTableFieldHeight == null)
        {
            mTableFieldHeight = new int[rowCount];
        }
        int[] columnLength = calculateColumnTextLength(mHeaders, mRowDatas);
//        for(int j=0; j < columnLength.length; j++)
//        {
//            String text = String.format("%"+ columnLength[j] + "s","X").replace(" ", "X");
//            TextView textView = getVisualTableTextView(text,
//                    this.mHeaders.get(0).getColumnCells().get(j).getGravity());
//            int width = measureTextWidth(textView);
//            int height = measureTextHeight(textView);
//            if (mTableFieldWidth[j] < width)
//            {
//                mTableFieldWidth[j] = width;
//            }
//            if (mTableFieldHeight[0] < height)
//            {
//                mTableFieldHeight[0] = height;
//            }
//        }

        for (int i=0; this.mHeaders != null && i< this.mHeaders.size(); i++)
        {
            for(int j=0; j < this.mHeaders.get(i).getColumnCells().size(); j++)
            {
                TextView textView = getVisualTableTextView(
                        this.mHeaders.get(i).getColumnCells().get(j).getText() + "   ",
                        this.mHeaders.get(i).getColumnCells().get(j).getGravity());
                int width = measureTextWidth(textView);
                int height = measureTextHeight(textView);
                if (mTableFieldWidth[j] < width)
                {
                    mTableFieldWidth[j] = width;
                }
                if (mTableFieldHeight[i] < height)
                {
                    mTableFieldHeight[i] = height;
                }
            }
        }

        for(int j=0; mRowDatas != null && mRowDatas.size() > 0
                && columnLength != null  && j < columnLength.length; j++)
        {
            String text = String.format("%"+ columnLength[j] + "s","X").replace(" ", "X");
            TextView textView = getVisualTableTextView(text,
            this.mRowDatas.get(0).getColumnCells().get(j).getGravity());
            int width = measureTextWidth(textView);
            int height = measureTextHeight(textView);
            if (mTableFieldWidth[j] < width)
            {
                mTableFieldWidth[j] = width;
            }
            if (mTableFieldHeight[1] < height)
            {
                mTableFieldHeight[1] = height;
            }
        }
        for(int i=2; i<mTableFieldHeight.length; i++)
        {
            mTableFieldHeight[i] = mTableFieldHeight[1];
        }
//        for (int i=0; i< this.mHeaders.size(); i++)
//        {
//            for(int j=0; j < this.mHeaders.get(i).getColumnCells().size(); j++)
//            {
//                TextView textView = getVisualTableTextView(
//                        this.mHeaders.get(i).getColumnCells().get(j).getText() + "   ",
//                        this.mHeaders.get(i).getColumnCells().get(j).getGravity());
//                int width = measureTextWidth(textView);
//                int height = measureTextHeight(textView);
//                if (mTableFieldWidth[j] < width)
//                {
//                    mTableFieldWidth[j] = width;
//                }
//                if (mTableFieldHeight[i] < height)
//                {
//                    mTableFieldHeight[i] = height;
//                }
//            }
//        }
//
//        for (int i=0; i< this.mRowDatas.size(); i++)
//        {
//            for(int j=0; j<this.mRowDatas.get(i).getColumnCells().size(); j++)
//            {
//                TextView textView = getVisualTableTextView(
//                        this.mRowDatas.get(i).getColumnCells().get(j).getText(),
//                        this.mRowDatas.get(i).getColumnCells().get(j).getGravity());
//                int width = measureTextWidth(textView);
//                int height = measureTextHeight(textView);
//                if (mTableFieldWidth[j] < width)
//                {
//                    mTableFieldWidth[j] = width;
//                }
//                if (mTableFieldHeight[i + mFixRowCount] < height)
//                {
//                    mTableFieldHeight[i + mFixRowCount] = height;
//                }
//            }
//        }
    }

    private TextView getVisualTableTextView(String text, int gravity) {
        TextView textView = new TextView(mContext);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TableViewConstants.CELL_FONT_SIZE);
        textView.setText(text);
        if (gravity == Gravity.NO_GRAVITY)
        {
            textView.setGravity(Gravity.CENTER | Gravity.LEFT);
        }
        else
        {
            textView.setGravity(gravity);
        }

        //设置布局
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        textViewParams.setMargins(TableViewConstants.CELL_MARGIN_LEFT,
                TableViewConstants.CELL_MARGIN_TOP,
                TableViewConstants.CELL_MARGIN_RIGHT,
                TableViewConstants.CELL_MARGIN_BOTTOM);
        textView.setLayoutParams(textViewParams);
        textView.setMaxLines(TableViewConstants.CELL_TEXT_MAX_LINE_COUNT);
        if (TableViewConstants.CELL_TEXT_ELLIPSIZE)
        {
            textView.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        }

        return textView;
    }

    private TextView getActualTableHeaderTextView(String text, int gravity, int rowIndex, int colIndex) {
        TextView textView = new TextView(mContext);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TableViewConstants.CELL_FONT_SIZE);
        textView.setText(text);
        if (gravity == Gravity.NO_GRAVITY)
        {
            textView.setGravity(Gravity.CENTER);
        }
        else
        {
            textView.setGravity(gravity);
        }

        //设置布局
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        textViewParams.setMargins(TableViewConstants.CELL_MARGIN_LEFT,
                TableViewConstants.CELL_MARGIN_TOP,
                TableViewConstants.CELL_MARGIN_RIGHT,
                TableViewConstants.CELL_MARGIN_BOTTOM);
        textView.setBackgroundResource(R.drawable.table_header_label_bg);
        textView.setTextColor(Color.WHITE);
        textView.getPaint().setFakeBoldText(true);
        textView.setLayoutParams(textViewParams);
        textView.setPadding(TableViewConstants.CELL_PADDING_LEFT,
                TableViewConstants.CELL_PADDING_TOP,
                TableViewConstants.CELL_PADDING_RIGHT,
                TableViewConstants.CELL_PADDING_BOTTOM);
        int height = mTableFieldHeight[rowIndex];
        int width = mTableFieldWidth[colIndex];
        textViewParams.height = DisplayUtils.dip2px(mContext, height);
        textViewParams.width = DisplayUtils.dip2px(mContext, width);
        textView.setLayoutParams(textViewParams);
        return textView;
    }
    private int measureTextWidth(TextView textView) {
        int width = 0;
        if (textView != null) {
            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) textView.getLayoutParams();
            width = DisplayUtils.px2dip(mContext, layoutParams.leftMargin) +
                    DisplayUtils.px2dip(mContext, layoutParams.rightMargin) +
                    DisplayUtils.px2dip(mContext, TableViewConstants.CELL_PADDING_LEFT) +
                    DisplayUtils.px2dip(mContext, TableViewConstants.CELL_PADDING_RIGHT) +
                    getTextViewWidth(textView);
            if (width < mMinFieldWidth) {
                return mMinFieldWidth;
            } else if (width > mMaxFieldWidth) {
                return mMaxFieldWidth;
            } else {
                return width;
            }
        }
        return width;
    }

    private int measureTextHeight(TextView textView) {
        int height = 0;
        if (textView != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
            height = DisplayUtils.px2dip(mContext, layoutParams.topMargin) +
                    DisplayUtils.px2dip(mContext, layoutParams.bottomMargin) +
                    DisplayUtils.px2dip(mContext, TableViewConstants.CELL_PADDING_TOP) +
                    DisplayUtils.px2dip(mContext, TableViewConstants.CELL_PADDING_BOTTOM) +
                    getTextViewHeight(textView);
            if (height < mMinFieldHeight) {
                return mMinFieldHeight;
            } else if (height > mMaxFieldHeight) {
                return mMaxFieldHeight;
            } else {
                return height;
            }
        }
        return height;
    }

    private int getTextViewHeight(TextView textView) {
        if (textView != null) {
            int width = measureTextWidth(textView);
            TextPaint textPaint = textView.getPaint();
            StaticLayout staticLayout = new StaticLayout(textView.getText(), textPaint, DisplayUtils.dip2px(mContext, width), Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
            int height = DisplayUtils.px2dip(mContext, staticLayout.getHeight());
            return height;
        }
        return 0;
    }

    private int getTextViewWidth(TextView view) {
        if (view != null) {
            TextPaint paint = view.getPaint();
            return DisplayUtils.px2dip(mContext, (int) paint.measureText(view.getText() + ""));
        }
        return 0;
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

    public interface OnTableViewRowHeaderActionListener {
        void onDataSort(int position, int positionOffset, TableViewBeanComparator.ORDER_TYPE orderType);
    }

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

        for (int i=0; this.mRowDatas != null && i< this.mRowDatas.size(); i++)
        {
            TableViewRowBean row = this.mRowDatas.get(i);
            String id = "dataCol" + i;
            String value = String.valueOf(i + 1);
            TableViewCellBean cell = new TableViewCellBean(id, value);
            cell.setGravity(Gravity.CENTER);
            this.mRowDatas.get(i).getColumnCells().add(0, cell);
        }
        for (int i=0; this.mHeaders != null && i< this.mHeaders.size(); i++)
        {
            if (i == 0)
            {
                TableViewRowBean row = this.mHeaders.get(i);
                String id = "headerCol" + i;
                String value = " No. ";
                TableViewCellBean cell = new TableViewCellBean(id, value);
                cell.setGravity(Gravity.CENTER);
                this.mHeaders.get(i).getColumnCells().add(0, cell);
            }
            else
            {
                TableViewRowBean row = this.mHeaders.get(i);
                String id = "headerCol" + i;
                String value = "";
                TableViewCellBean cell = new TableViewCellBean(id, value);
                cell.setGravity(Gravity.CENTER);
                this.mHeaders.get(i).getColumnCells().add(0, cell);
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
        for (int i=0; rowDatas != null && i<rowDatas.size(); i++)
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

    private OnLoadCompleteListener mOnLoadCompleteListener;
    public void setOnLoadCompleteListener(OnLoadCompleteListener onLoadCompleteListener) {
        mOnLoadCompleteListener = onLoadCompleteListener;
    }
    public interface OnLoadCompleteListener{
        void onLoadComplete(View view);
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
        private int mFixColumnCount;
        private int mPageCount;
        private int mMaxFieldWidth;
        private int mMaxFieldHeight;
        private int mMinFieldWidth;
        private int mMinFieldHeight;
        private int mFixRowCount;
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

//        public Builder allRowExpand(boolean allRowExpand) {
//            this.mAllRowExpandFlag = allRowExpand;
//            return this;
//        }
//
//        public Builder shortText(boolean shortText) {
//            this.mShortTextFlag = shortText;
//            return this;
//        }
//
//        public Builder wrapRowFlag(boolean wrapRowFlag) {
//            this.mWrapRowFlag = wrapRowFlag;
//            return this;
//        }

        public Builder fixColumnCount(int fixColumnCount) {
            this.mFixColumnCount = fixColumnCount;
            return this;
        }
//        public Builder pageCount(int pageCount) {
//            this.mPageCount = pageCount;
//            return this;
//        }
//        public Builder fixRowCount(int fixRowCount) {
//            this.mFixRowCount = fixRowCount;
//            return this;
//        }

        public Builder minFieldWidth(int minFieldWidth) {
            this.mMinFieldWidth = minFieldWidth;
            return this;
        }

        public Builder minFieldHeight(int minFieldHeight) {
            this.mMinFieldHeight = minFieldHeight;
            return this;
        }
        public Builder maxFieldWidth(int maxFieldWidth) {
            this.mMaxFieldWidth = maxFieldWidth;
            return this;
        }

        public Builder maxFieldHeight(int maxFieldHeight) {
            this.mMaxFieldHeight = maxFieldHeight;
            return this;
        }

        /**
         * @return Instance of {@link CustomizedTableView} initiated with builder settings
         */
        public CustomizedTableView build() {
            final CustomizedTableView gridView = new CustomizedTableView(this);
            gridView.showWaitDialog();
//            (new Handler()).postDelayed(new Runnable(){
//                @Override
//                public void run() {
//                    // grid 设置无效，焦点混乱
//                }
//            }, 100);
            gridView.loadGridView();
            return gridView;
        }

        private void initDefaultValues() {
            mAllRowExpandFlag = false;
            mAutoFits = true;
            mShortTextFlag = false;
            mWrapRowFlag = false;
            mMinFieldWidth = 30;
            mMinFieldHeight = 40;
            mMaxFieldWidth = 300;
            mMaxFieldHeight = 45;
            mFixRowCount = 1;
            mFixColumnCount = 1;
            if (mPageCount == 0)
            {
                mPageCount = 1;
            }

        }
    }

    private class InitializeDatesList extends AsyncTask<Void, Void, Void> {

        InitializeDatesList() {
        }

        @Override
        protected void onPreExecute() {
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
