/**
 * tfme Android client application
 *
 * @author Andy Scherzinger
 * Copyright (C) 2016 tfme GmbH.
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.liu.customizedgridview.gridview;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.liu.customizedgridview.R;
import com.liu.customizedgridview.utils.DisplayUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * This Adapter populates a ListView with all accounts within the app.
 */
public class GridViewDataListAdapter extends ArrayAdapter<GridViewRowBean> {

    private DISPLAY_ROW_MEMBER mDisplayFlag;
    private static final String TAG = GridViewDataListAdapter.class.getSimpleName();
    private float mAvatarRadiusDimension;
    private List<GridViewRowBean> mRowDatas;
    private GridViewRowBean mCurrentRow;
    private Activity mContext = null;
    private static int mTotalColumnSpan = 40;
    private int parentContainerWidth = 0;

//    private GridViewDataListAdapter.GridViewListAdapterListener mListener;
    private Drawable mTintedCheck;
    private boolean mWrapRowFlag;
    private boolean mShortTextFlag;
    private boolean mAllRowExpandFlag = true;
    private int mGridViewCellFontSize = 16;
    private int mInitColumnCount;
    private List<GridViewRowBean> mHeaders;
    public GridViewDataListAdapter(Activity context,
                                   List<GridViewRowBean> headers,
                                   List<GridViewRowBean> rows,
                                   int initColumnCount,
                                   boolean wrapRowFlag,
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
//        this.mListener = (GridViewDataListAdapter.GridViewListAdapterListener) context;
//        this.mAvatarRadiusDimension = context.getResources().getDimension(R.dimen.list_item_avatar_icon_radius);
        this.mTintedCheck = tintedCheck;
        this.mShortTextFlag = shortText;
        parentContainerWidth = DisplayUtils.getDisplayWidth(context);
        int[] initColLength = this.calculateColumnTextLength(rows);
        this.calculateRowColSetting(rows);
        if (autoWidth)
        {
            if (wrapRowFlag)
            {
                if(DisplayUtils.isPad(this.mContext))
                {
                    mTotalColumnSpan = (int)(parentContainerWidth / DisplayUtils.getDisplayFontPxForPad(mGridViewCellFontSize));
                }
                else
                {
                    mTotalColumnSpan = (int)(parentContainerWidth / DisplayUtils.getDisplayFontPx(mGridViewCellFontSize));
                }
                this.calculateColSpan(headers, rows, initColLength);
            } else {
                mTotalColumnSpan = 0;
                for(int i=0; i< initColLength.length; i++)
                {
                    mTotalColumnSpan += initColLength[i];
                }
                mTotalColumnSpan += initColLength.length;
                this.calculateColSpan(headers, rows, initColLength);

            }
        }

        // 计算完列宽后初始化标题栏
        this.initHeader();
    }


    public GridViewDataListAdapter(Activity context,
                                   List<GridViewRowBean> headers,
                                   List<GridViewRowBean> rows,
                                   boolean wrapRowFlag,
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
        parentContainerWidth = DisplayUtils.getDisplayWidth(context);
        int[] initColLength = this.calculateColumnTextLength(rows);
        this.calculateRowColSetting(rows);
        if (autoWidth)
        {
            if (wrapRowFlag)
            {
                if(DisplayUtils.isPad(this.mContext))
                {
                    mTotalColumnSpan = (int)(parentContainerWidth / DisplayUtils.getDisplayFontPxForPad(mGridViewCellFontSize));
                }
                else
                {
                    mTotalColumnSpan = (int)(parentContainerWidth / DisplayUtils.getDisplayFontPx(mGridViewCellFontSize));
                }
                this.calculateColSpan(headers, rows, initColLength);
            } else {
                mTotalColumnSpan = 0;
                for(int i=0; i< initColLength.length; i++)
                {
                    mTotalColumnSpan += initColLength[i];
                }
                mTotalColumnSpan += initColLength.length;
                this.calculateColSpan(headers, rows, initColLength);
            }
        }

        // 计算完列宽后初始化标题栏
        this.initHeader();
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

    private void initHeader()
    {
        final ListView listHeaderView = (ListView) mContext.findViewById(R.id.grid_view_header_row);
        final GridViewHeaderListAdapter listHeaderAdapter;
        if (this.mDisplayFlag == DISPLAY_ROW_MEMBER.ROW)
        {
            listHeaderAdapter = new GridViewHeaderListAdapter(mContext, mHeaders, mRowDatas, mTotalColumnSpan, mAllRowExpandFlag, mShortTextFlag, null);
        }
        else
        {
            listHeaderAdapter = new GridViewHeaderListAdapter(mContext, mHeaders, mRowDatas, mTotalColumnSpan, mInitColumnCount, mShortTextFlag, null);
        }
        listHeaderView.setAdapter(listHeaderAdapter);
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
                if (!mWrapRowFlag)
                {
                    cells.get(i).setColSpan(lengthDatas[i] + 1);
                    continue;
                }
//                String cellText = "";
//                if (this.mShortTextFlag)
//                {
//                    cellText = cells.get(i).getShortText();
//                }
//                else
//                {
//                    cellText = cells.get(i).getText();
//                }
                int span = 0;
                int headerSpan = 0;
                span = lengthDatas[i];
//                if (cellText != null)
//                {
//                    try {
//                        span = cellText.getBytes("GBK").length;
//                    } catch (UnsupportedEncodingException e) {
//                        span = cellText.getBytes().length;
//                    }
//                }
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
//                    String nextCellText = "";
//                    if (this.mShortTextFlag)
//                    {
//                        nextCellText = cells.get(i+1).getShortText();
//                    }
//                    else
//                    {
//                        nextCellText = cells.get(i+1).getText();
//                    }
//                    if (nextCellText != null)
//                    {
//                        try {
//                            nextSpan = nextCellText.getBytes("GBK").length;
//                        } catch (UnsupportedEncodingException e) {
//                            nextSpan = nextCellText.getBytes().length;
//                        }
//                    }
                    nextSpan = lengthDatas[i+1];
                    if (headers.get(0).getColumnCells().get(i+1).getText() != null && !newLine)
                    {
                        try {
                            nextHeaderSpan = headers.get(0).getColumnCells().get(i+1).getText().getBytes("GBK").length + 1;
                        } catch (UnsupportedEncodingException e) {
                            nextHeaderSpan = headers.get(0).getColumnCells().get(i+1).getText().getBytes().length + 1;
                        }
                    }
                    nextSpan += nextHeaderSpan;
                    if (colSpan + nextSpan >= mTotalColumnSpan)
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final GridViewCellItemViewHolder viewHolder;
        mCurrentRow = mRowDatas.get(position);
        if (convertView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.grid_view_data_row, parent, false);
            viewHolder = new GridViewCellItemViewHolder();
            viewHolder.rowData = (RecyclerView) convertView.findViewById(R.id.grid_view_data_row_rv);
//            viewHolder.expand = (ImageView) convertView.findViewById(R.id.expand_img);
//            viewHolder.collapse = (ImageView) convertView.findViewById(R.id.collapse_img);
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
                viewHolder.rowNumber.setWidth((int)(DisplayUtils.getDisplayFontPx(mGridViewCellFontSize)
                        * rowNumberTextLength)
                );
            }
            else
            {
                viewHolder.rowNumber.setVisibility(View.GONE);
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
