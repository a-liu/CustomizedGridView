package com.liu.customizedgridview.gridview;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.liu.customizedgridview.R;
import com.liu.customizedgridview.utils.DisplayUtils;

import java.util.List;

public class GridViewHeaderListAdapter extends ArrayAdapter<GridViewRowBean> {

    private DISPLAY_ROW_MEMBER mDisplayFlag;
    private static final String TAG = GridViewDataListAdapter.class.getSimpleName();
    private float mAvatarRadiusDimension;
    private List<GridViewRowBean> mRowDatas;
    private GridViewRowBean mCurrentRow;
    private Activity mContext = null;
    private int mTotalColumnSpan;
//    private int parentContainerWidth = 0;
    //    private GridViewDataListAdapter.GridViewListAdapterListener mListener;
    private Drawable mTintedCheck;
    private boolean mWrapRowFlag;
    private boolean mShortTextFlag;
    private boolean mAllRowDisplayFlag = true;
//    private int mGridViewCellFontSize = 16;
    private int mInitColumnCount;
    private List<GridViewRowBean> mHeaders;
    private int mFixColumnCount;
    public GridViewHeaderListAdapter(Activity context,
                                   List<GridViewRowBean> headers,
                                   List<GridViewRowBean> rows,
                                     int fixColumnCount,
                                     boolean wrapRowFlag,
                                     int totalColumnSpan,
                                   int initColumnCount,
                                   boolean shortText,
                                   Drawable tintedCheck) {
        super(context, -1, headers);
        this.mContext = context;
        this.mHeaders = headers;
        this.mRowDatas = rows;
        this.mFixColumnCount = fixColumnCount;
        this.mWrapRowFlag = wrapRowFlag;
        this.mInitColumnCount = initColumnCount;
        this.mDisplayFlag = DISPLAY_ROW_MEMBER.COLUMN;
//        this.mListener = (GridViewDataListAdapter.GridViewListAdapterListener) context;
//        this.mAvatarRadiusDimension = context.getResources().getDimension(R.dimen.list_item_avatar_icon_radius);
        this.mTintedCheck = tintedCheck;
        this.mShortTextFlag = shortText;
//        parentContainerWidth = DisplayUtils.getDisplayWidth(context);
        this.calculateRowColSetting(rows);
        mTotalColumnSpan = totalColumnSpan;
    }

    public GridViewHeaderListAdapter(Activity context,
                                   List<GridViewRowBean> headers,
                                   List<GridViewRowBean> rows,
                                     int fixColumnCount,
                                     boolean wrapRowFlag,
                                     int totalColumnSpan,
                                   boolean allRowDisplay,
                                   boolean shortText,
                                   Drawable tintedCheck) {
        super(context, -1, headers);
        this.mContext = context;
        this.mHeaders = headers;
        this.mRowDatas = rows;
        this.mFixColumnCount = fixColumnCount;
        this.mWrapRowFlag = wrapRowFlag;
        this.mAllRowDisplayFlag = allRowDisplay;
        this.mDisplayFlag = DISPLAY_ROW_MEMBER.ROW;
        this.mTintedCheck = tintedCheck;
        this.mShortTextFlag = shortText;
//        parentContainerWidth = DisplayUtils.getDisplayWidth(context);

        this.calculateRowColSetting(rows);
        mTotalColumnSpan = totalColumnSpan;
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final GridViewCellItemViewHolder viewHolder;
        mCurrentRow = mHeaders.get(position);
        if (convertView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.grid_view_header_row, parent, false);
            viewHolder = new GridViewCellItemViewHolder();
            viewHolder.rowData = (RecyclerView) convertView.findViewById(R.id.grid_view_header_row_rv);
            viewHolder.rowBlank = (TextView) convertView.findViewById(R.id.grid_view_header_row_blank);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (GridViewCellItemViewHolder) convertView.getTag();
        }


        GridViewRowBean rowItem = mHeaders.get(position);
        if (rowItem != null)
        {
            // 行号
            // 折行时显示
            if (mWrapRowFlag) {
                viewHolder.rowBlank.setText( " No. ");
                int rowNumberTextLength = String.valueOf(mRowDatas.size()).length();
                if (rowNumberTextLength < 5)
                {
                    rowNumberTextLength = 5;
                }
                viewHolder.rowBlank.setWidth((int)(DisplayUtils.getDisplayFontPx(CustomizedGridView.CELL_FONT_SIZE) * rowNumberTextLength));
                int width = (int)(mTotalColumnSpan * DisplayUtils.getDisplayFontPx(CustomizedGridView.CELL_FONT_SIZE));
                viewHolder.rowData.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            }
            else
            {
                viewHolder.rowBlank.setVisibility(View.GONE);
                int width = (int)(mTotalColumnSpan * DisplayUtils.getDisplayFontPx(CustomizedGridView.CELL_FONT_SIZE));
                viewHolder.rowData.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            }
            GridLayoutManager layoutManage = new GridLayoutManager(mContext, mTotalColumnSpan);
            layoutManage.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int result = 0;
                    result = mRowDatas.get(0).getColumnCells().get(position).getColSpan();
                    return result;
                }
            });
            viewHolder.rowData.setLayoutManager(layoutManage);
            GridViewHeaderRowAdapter gridViewAdapter = null;
            if (mDisplayFlag == DISPLAY_ROW_MEMBER.ROW)
            {
                gridViewAdapter = new GridViewHeaderRowAdapter(mContext, mTotalColumnSpan, mFixColumnCount, mHeaders.get(0).getColumnCells(), mRowDatas.get(0).getColumnCells(), mWrapRowFlag, mAllRowDisplayFlag, this.mShortTextFlag);
            }
            else
            {
                gridViewAdapter = new GridViewHeaderRowAdapter(mContext, mTotalColumnSpan, mFixColumnCount,  mHeaders.get(0).getColumnCells(), mRowDatas.get(0).getColumnCells(), mWrapRowFlag, mInitColumnCount, this.mShortTextFlag);
            }
            gridViewAdapter.setOnGridViewRowCellActionListener(new GridViewHeaderRowAdapter.OnGridViewRowCellActionListener() {
                @Override
                public void onFirstRowDoubleClicked(Object sender, View v, int position, int positionOffset) {
                    Toast.makeText(mContext, "position" + position + ":"+
                                    "offset" + positionOffset + ":"+((TextView)v).getText(),
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDataSort(Object sender, View v, int position, int positionOffset, GridViewBeanComparator.ORDER_TYPE orderType) {
                    // 排序
                    if (mOnGridViewDataSortListener != null)
                    {
                        mOnGridViewDataSortListener.onDataSort(position, positionOffset, orderType);
                    }

                }
            });
            viewHolder.rowData.setAdapter(gridViewAdapter);
        }

        return convertView;
    }
    private OnGridViewDataSortListener mOnGridViewDataSortListener;

    public OnGridViewDataSortListener getOnGridViewDataSortListener() {
        return mOnGridViewDataSortListener;
    }

    public void setOnGridViewDataSortListener(OnGridViewDataSortListener listener) {
        this.mOnGridViewDataSortListener = listener;
    }

    public interface OnGridViewDataSortListener {
        void onDataSort(int position, int offset, GridViewBeanComparator.ORDER_TYPE orderType);
    }
    /**
     * Account ViewHolderItem to get smooth scrolling.
     */
    class GridViewCellItemViewHolder {
        //        ImageView expand;
//        ImageView collapse;
        TextView rowBlank;
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
