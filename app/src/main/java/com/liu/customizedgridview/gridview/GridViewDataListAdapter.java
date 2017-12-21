package com.liu.customizedgridview.gridview;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.liu.customizedgridview.R;
import com.liu.customizedgridview.utils.DisplayUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class GridViewDataListAdapter extends ArrayAdapter<GridViewRowBean> {



    private DISPLAY_ROW_MEMBER mDisplayFlag;
    private static final String TAG = GridViewDataListAdapter.class.getSimpleName();
    private float mAvatarRadiusDimension;
    private List<GridViewRowBean> mRowDatas;
    private GridViewRowBean mCurrentRow;
    private Activity mContext = null;
    private int mFixColumnCount;

//    private GridViewDataListAdapter.GridViewListAdapterListener mListener;
    private Drawable mTintedCheck;
    private boolean mWrapRowFlag;
    private boolean mShortTextFlag;
    private boolean mAllRowExpandFlag = true;
    private int mTotalColumnSpan;
    private int mInitColumnCount;
    private List<GridViewRowBean> mHeaders;
    public void setDataRows(List<GridViewRowBean> rows)
    {
        this.mRowDatas = rows;
    }
    public GridViewDataListAdapter(Activity context,
                                   List<GridViewRowBean> headers,
                                   List<GridViewRowBean> rows,
                                   int fixColumnCount,
                                   int initColumnCount,
                                   boolean wrapRowFlag,
                                   int totalColumnSpan,
                                   boolean shortText,
                                   Drawable tintedCheck) {
        super(context, -1, rows);
        this.mContext = context;
        this.mHeaders = headers;
        this.mRowDatas = rows;
        this.mFixColumnCount = fixColumnCount;
        this.mInitColumnCount = initColumnCount;
        this.mWrapRowFlag = wrapRowFlag;
        this.mDisplayFlag = DISPLAY_ROW_MEMBER.COLUMN;
        this.mTintedCheck = tintedCheck;
        this.mShortTextFlag = shortText;
        this.mTotalColumnSpan = totalColumnSpan;

    }


    public GridViewDataListAdapter(Activity context,
                                   List<GridViewRowBean> headers,
                                   List<GridViewRowBean> rows,
                                   int fixColumnCount,
                                   boolean wrapRowFlag,
                                   int totalColumnSpan,
                                   boolean allRowExpandFlag,
                                   boolean shortText,
                                   Drawable tintedCheck) {
        super(context, -1, rows);
        this.mContext = context;
        this.mHeaders = headers;
        this.mRowDatas = rows;
        this.mWrapRowFlag = wrapRowFlag;
        this.mAllRowExpandFlag = allRowExpandFlag;
        this.mFixColumnCount = fixColumnCount;
        this.mDisplayFlag = DISPLAY_ROW_MEMBER.ROW;
        this.mTintedCheck = tintedCheck;
        this.mShortTextFlag = shortText;
        this.mTotalColumnSpan = totalColumnSpan;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final GridViewCellItemViewHolder viewHolder;
        mCurrentRow = mRowDatas.get(position);
        if (convertView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.grid_view_data_row, parent, false);
            viewHolder = new GridViewCellItemViewHolder();
            viewHolder.rowData = (RecyclerView) convertView.findViewById(R.id.grid_view_data_row_rv);
            viewHolder.rowNumber = (TextView) convertView.findViewById(R.id.grid_view_data_row_number);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (GridViewCellItemViewHolder) convertView.getTag();
        }


        GridViewRowBean rowItem = mRowDatas.get(position);
        GridLayoutManager layoutManage = null;
        if (rowItem != null) {

            /// bind listener
//            final RecyclerView rowRecyclerView = (RecyclerView) convertView.findViewById(R.id.grid_view_row_rv);

            // 行号
            // 折行时显示
            if (mWrapRowFlag)
            {
                int rowNumber = position + 1;
                int rowNumberTextLength = String.valueOf(mRowDatas.size()).length();
                if (rowNumberTextLength < 5)
                {
                    rowNumberTextLength = 5;
                }
                viewHolder.rowNumber.setVisibility(View.VISIBLE);
                viewHolder.rowNumber.setText(String.valueOf(rowNumber));
                viewHolder.rowNumber.setWidth((int)(DisplayUtils.getDisplayFontPx(CustomizedGridView.CELL_FONT_SIZE)
                        * rowNumberTextLength)
                );
                int width = (int)(mTotalColumnSpan * DisplayUtils.getDisplayFontPx(CustomizedGridView.CELL_FONT_SIZE));
                viewHolder.rowData.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                layoutManage = new GridLayoutManager(mContext, mTotalColumnSpan);
            }
            else
            {
                viewHolder.rowNumber.setVisibility(View.GONE);
//                if (mFixColumnCount == 0)
//                {
                // 全部显示
                int width = (int)(mTotalColumnSpan * DisplayUtils.getDisplayFontPx(CustomizedGridView.CELL_FONT_SIZE));
                viewHolder.rowData.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
                layoutManage = new GridLayoutManager(mContext, mTotalColumnSpan);
//                }
//                else {
//                    // 逐屏显示
//                    int width = (int)(DisplayUtils.getDisplayWidth(mContext));
//                    viewHolder.rowData.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
//                    layoutManage = new GridLayoutManager(mContext, mTotalColumnSpan);
//                }
            }


            layoutManage.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int result = 0;
                    result = mCurrentRow.getColumnCells().get(position).getColSpan();
                    return result;
                }
            });
            viewHolder.rowData.setLayoutManager(layoutManage);
            GridViewDataRowAdapter gridViewAdapter = (GridViewDataRowAdapter)viewHolder.rowData.getAdapter();
            if (gridViewAdapter != null)
            {
                final GridViewDataRowAdapter adapter = gridViewAdapter;
                adapter.setColumnDatas(rowItem.getColumnCells());
                android.os.Handler handler = new android.os.Handler();
                handler.post(new Runnable(){
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            else
            {
                if (mDisplayFlag == DISPLAY_ROW_MEMBER.ROW)
                {
                    gridViewAdapter = new GridViewDataRowAdapter(mContext, mTotalColumnSpan, mFixColumnCount, mHeaders.get(0).getColumnCells(), rowItem.getColumnCells(), mWrapRowFlag, mAllRowExpandFlag, this.mShortTextFlag);
                }
                else
                {
                    gridViewAdapter = new GridViewDataRowAdapter(mContext, mTotalColumnSpan, mFixColumnCount, mHeaders.get(0).getColumnCells(), rowItem.getColumnCells(), mWrapRowFlag, mInitColumnCount, this.mShortTextFlag);
                }
                gridViewAdapter.setOnGridViewRowCellActionListener(new GridViewDataRowAdapter.OnGridViewRowCellActionListener() {
                    @Override
                    public void onFirstRowDoubleClicked(Object sender, View v) {
                        viewHolder.rowData.setAdapter((GridViewDataRowAdapter)sender);
                    }
                });
                viewHolder.rowData.setAdapter(gridViewAdapter);
            }

        }

        return convertView;
    }

    /**
     * Account ViewHolderItem to get smooth scrolling.
     */
    class GridViewCellItemViewHolder {
//        ImageView expand;
//        ImageView collapse;
        TextView rowNumber;
        RecyclerView rowData;
    }

    public enum DISPLAY_ROW_MEMBER
    {
        COLUMN,
        ROW,
    }

//    private final int DOUBLE_TAP_TIMEOUT = 200;
//    private MotionEvent mCurrentDownEvent;
//    private MotionEvent mPreviousUpEvent;
//    private boolean isConsideredDoubleTap(MotionEvent firstDown,
//                                          MotionEvent firstUp, MotionEvent secondDown) {
//        if (secondDown.getEventTime() - firstUp.getEventTime() > DOUBLE_TAP_TIMEOUT) {
//            return false;
//        }
//        int deltaX = (int) firstUp.getX() - (int) secondDown.getX();
//        int deltaY = (int) firstUp.getY() - (int) secondDown.getY();
//        return deltaX * deltaX + deltaY * deltaY < 10000;
//    }
}
