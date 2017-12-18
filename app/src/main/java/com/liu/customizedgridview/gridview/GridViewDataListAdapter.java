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

public class GridViewDataListAdapter extends ArrayAdapter<GridViewRowBean> {



    private DISPLAY_ROW_MEMBER mDisplayFlag;
    private static final String TAG = GridViewDataListAdapter.class.getSimpleName();
    private float mAvatarRadiusDimension;
    private List<GridViewRowBean> mRowDatas;
    private GridViewRowBean mCurrentRow;
    private Activity mContext = null;


//    private GridViewDataListAdapter.GridViewListAdapterListener mListener;
    private Drawable mTintedCheck;
    private boolean mWrapRowFlag;
    private boolean mShortTextFlag;
    private boolean mAllRowExpandFlag = true;
    private int mTotalColumnSpan;
    private int mInitColumnCount;
    private List<GridViewRowBean> mHeaders;
    public GridViewDataListAdapter(Activity context,
                                   List<GridViewRowBean> headers,
                                   List<GridViewRowBean> rows,
                                   int initColumnCount,
                                   boolean wrapRowFlag,
                                   int totalColumnSpan,
                                   boolean autoWidth,
                                   boolean shortText,
                                   Drawable tintedCheck) {
        super(context, -1, rows);
        this.mContext = context;
        this.mHeaders = headers;
        this.mRowDatas = rows;
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
                                   boolean wrapRowFlag,
                                   int totalColumnSpan,
                                   boolean allRowExpandFlag,
                                   boolean autoWidth,
                                   boolean shortText,
                                   Drawable tintedCheck) {
        super(context, -1, rows);
        this.mContext = context;
        this.mHeaders = headers;
        this.mRowDatas = rows;
        this.mWrapRowFlag = wrapRowFlag;
        this.mAllRowExpandFlag = allRowExpandFlag;
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
        if (rowItem != null) {

            /// bind listener
//            final RecyclerView rowRecyclerView = (RecyclerView) convertView.findViewById(R.id.grid_view_row_rv);

            // 行号
            // 折行时显示
            if (mWrapRowFlag)
            {
                int rowNumber = position + 1;
                int rowNumberTextLength = String.valueOf(mRowDatas.size()).length();
                if (rowNumberTextLength < 3)
                {
                    rowNumberTextLength = 3;
                }
                viewHolder.rowNumber.setVisibility(View.VISIBLE);
                viewHolder.rowNumber.setText(String.valueOf(rowNumber));
                viewHolder.rowNumber.setWidth((int)(DisplayUtils.getDisplayFontPx(CustomizedGridView.CELL_FONT_SIZE)
                        * rowNumberTextLength)
                );
            }
            else
            {
                viewHolder.rowNumber.setVisibility(View.GONE);
                int width = (int)(mTotalColumnSpan * DisplayUtils.getDisplayFontPx(CustomizedGridView.CELL_FONT_SIZE));
                viewHolder.rowData.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            }


//            viewHolder.rowData.setOnTouchListener(new View.OnTouchListener() {
//
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
//                    {
//                            Toast.makeText(mContext, "ACTION_DOWN" + motionEvent.getX(),
//                                    Toast.LENGTH_SHORT).show();
//                    } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
//                    Toast.makeText(mContext, "ACTION_MOVE" + motionEvent.getX(),
//                            Toast.LENGTH_SHORT).show();
//                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                        Toast.makeText(mContext, "ACTION_UP" + motionEvent.getX(),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                        else {
//                        return false;
//                    }
//
//
//                    return true;
//                }
//            });
//            viewHolder.expand.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(mContext, "Expand",
//                            Toast.LENGTH_SHORT).show();
//                    GridViewRowBean rowItem = mRowDatas.get(position);
//                    viewHolder.expand.setVisibility(View.GONE);
//                    viewHolder.collapse.setVisibility(View.VISIBLE);
//                    GridViewDataRowAdapter gridViewAdapter = null;
//                    gridViewAdapter = new GridViewDataRowAdapter(mContext, mTotalColumnSpan, rowItem.getColumnCells(), rowItem.getColumnCells().size(), mShortTextFlag);
//                    viewHolder.rowData.setAdapter(gridViewAdapter);
//                }
//            });
//
//            viewHolder.collapse.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(mContext, "Collapse",
//                            Toast.LENGTH_SHORT).show();
//                    GridViewRowBean rowItem = mRowDatas.get(position);
//                    viewHolder.collapse.setVisibility(View.GONE);
//                    viewHolder.expand.setVisibility(View.VISIBLE);
//                    GridViewDataRowAdapter gridViewAdapter = null;
//                    if (mDisplayFlag == DISPLAY_ROW_MEMBER.ROW)
//                    {
//                        gridViewAdapter = new GridViewDataRowAdapter(mContext, mTotalColumnSpan, rowItem.getColumnCells(), mAllRowExpandFlag, mShortTextFlag);
//                    }
//                    else
//                    {
//                        gridViewAdapter = new GridViewDataRowAdapter(mContext, mTotalColumnSpan, rowItem.getColumnCells(), mInitColumnCount, mShortTextFlag);
//                    }
//
//                    viewHolder.rowData.setAdapter(gridViewAdapter);
//                }
//            });

            GridLayoutManager layoutManage = new GridLayoutManager(mContext, mTotalColumnSpan);
            layoutManage.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int result = 0;
                    result = mCurrentRow.getColumnCells().get(position).getColSpan();
                    return result;
                }
            });
            viewHolder.rowData.setLayoutManager(layoutManage);
            GridViewDataRowAdapter gridViewAdapter = null;
            if (mDisplayFlag == DISPLAY_ROW_MEMBER.ROW)
            {
                gridViewAdapter = new GridViewDataRowAdapter(mContext, mTotalColumnSpan, mHeaders.get(0).getColumnCells(), rowItem.getColumnCells(), mWrapRowFlag, mAllRowExpandFlag, this.mShortTextFlag);
            }
            else
            {
                gridViewAdapter = new GridViewDataRowAdapter(mContext, mTotalColumnSpan, mHeaders.get(0).getColumnCells(), rowItem.getColumnCells(), mWrapRowFlag, mInitColumnCount, this.mShortTextFlag);
            }
            gridViewAdapter.setOnGridViewRowCellActionListener(new GridViewDataRowAdapter.OnGridViewRowCellActionListener() {
                @Override
                public void onFirstRowDoubleClicked(Object sender, View v) {
                    viewHolder.rowData.setAdapter((GridViewDataRowAdapter)sender);
                }
            });
            viewHolder.rowData.setAdapter(gridViewAdapter);
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
