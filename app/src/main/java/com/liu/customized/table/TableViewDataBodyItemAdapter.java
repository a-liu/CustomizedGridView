package com.liu.customized.table;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liu.customized.R;
import com.liu.utils.DisplayUtils;

import java.util.List;


public class TableViewDataBodyItemAdapter extends RecyclerView.Adapter<TableViewDataBodyItemAdapter.ViewHolder> {
    private List<TableViewRowBean> mRowDatas;
    private Activity mContext;
    private List<TableViewCellBean> mHeaders;
    private float xDown,yDown, xUp;
    private int mFixColumnCount;
    private int mFixRowCount;
    private int[] mTableFieldWidth;
    private int[] mTableFieldHeight;
    public TableViewDataBodyItemAdapter(Activity context,
                                        int fixRowCount,
                                        int fixColumnCount,
                                        List<TableViewRowBean> rowDatas,
                                        int[] tableFieldWidth,
                                        int[] tableFieldHeight
                                        ) {
        this.mRowDatas = rowDatas;
        this.mFixColumnCount = fixColumnCount;
        this.mFixRowCount = fixRowCount;
        this.mContext = context;
        this.mTableFieldWidth = tableFieldWidth;
        this.mTableFieldHeight = tableFieldHeight;

    }

    public void setRowDatas(List<TableViewRowBean> rowDatas)
    {
        this.mRowDatas = rowDatas;
    }

    @Override
    public TableViewDataBodyItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_view_data_body_item, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(final TableViewDataBodyItemAdapter.ViewHolder holder, final int position) {
        TableViewRowBean rowBean = this.mRowDatas.get(position);
        holder.linearView.removeAllViews();
        for(int i=0;i<rowBean.getColumnCells().size(); i++)
        {
            TextView view = getActualTableTextView(rowBean.getColumnCells().get(i).getText(),
                    rowBean.getColumnCells().get(i).getGravity(),position + mFixRowCount, i + mFixColumnCount
                    );
            holder.linearView.addView(view);
        }
    }

    @Override
    public int getItemCount() {
        // 数据行数
        return mRowDatas.size();
    }

    private TextView getActualTableTextView(String text, int gravity, int rowIndex, int colIndex) {
        TextView textView = new TextView(mContext);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TableViewConstants.CELL_FONT_SIZE);
        textView.setText(text);
        if (gravity == Gravity.NO_GRAVITY)
        {
            textView.setGravity(TableViewConstants.DEFAULT_CELL_GRAVITY);
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
        textView.setBackgroundResource(R.drawable.table_content_text_bg);
        textView.setPadding(TableViewConstants.CELL_PADDING_LEFT,
                TableViewConstants.CELL_PADDING_TOP,
                TableViewConstants.CELL_PADDING_RIGHT,
                TableViewConstants.CELL_PADDING_BOTTOM);
        textView.setLayoutParams(textViewParams);
        int height = mTableFieldHeight[rowIndex];
        int width = mTableFieldWidth[colIndex];
        textViewParams.height = DisplayUtils.dip2px(mContext, height);
        textViewParams.width = DisplayUtils.dip2px(mContext, width);
        textView.setLayoutParams(textViewParams);
        textView.setMaxLines(TableViewConstants.CELL_TEXT_MAX_LINE_COUNT);
        if (TableViewConstants.CELL_TEXT_ELLIPSIZE)
        {
            textView.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        }
        return textView;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearView;
        public ViewHolder(View view) {
            super(view);
            linearView = (LinearLayout) view.findViewById(R.id.customized_table_view_data_body_item_linear);
        }
    }
}
