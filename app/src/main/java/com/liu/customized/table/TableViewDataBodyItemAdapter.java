package com.liu.customized.table;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.liu.customized.R;
import com.liu.utils.DisplayUtils;

import java.util.List;


public class TableViewDataBodyItemAdapter extends RecyclerView.Adapter<TableViewDataBodyItemAdapter.ViewHolder> {
    private List<TableViewRowBean> mRowDatas;
    private Activity mContext;
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
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_view_data_body_item, null, false);
        final ViewHolder vh = new ViewHolder(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(final TableViewDataBodyItemAdapter.ViewHolder holder, final int position) {
        TableViewRowBean rowBean = this.mRowDatas.get(position);
        holder.linearView.removeAllViews();
        for(int i=0;i<rowBean.getColumnCells().size(); i++)
        {
            View view = getActualTableTextView(rowBean.getColumnCells().get(i).getText(),
                    rowBean.getColumnCells().get(i).getGravity(),position + mFixRowCount, i + mFixColumnCount
                    );
            holder.linearView.addView(view);
            // 分隔线
            if (i != mRowDatas.size() - 1) {
                View splitView = new View(mContext);
                ViewGroup.LayoutParams splitViewParmas = new ViewGroup.LayoutParams(DisplayUtils.dip2px(mContext, 1),
                        ViewGroup.LayoutParams.MATCH_PARENT);
                splitView.setLayoutParams(splitViewParmas);
                splitView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGridDataSplitLine));
                holder.linearView.addView(splitView);
            }
        }
    }

    @Override
    public int getItemCount() {
        // 数据行数
        return mRowDatas.size();
    }

    private View getActualTableTextView(String text, int gravity, int rowIndex, int colIndex) {
        final TextView textView = new TextView(mContext);
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
        if (rowIndex % 2 == 0)
        {
            textView.setBackgroundResource(R.drawable.table_content_text_second_bg);
        }
        else
        {
            textView.setBackgroundResource(R.drawable.table_content_text_first_bg);
        }

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
        if (TableViewConstants.CELL_TEXT_MAX_LINE_COUNT == 0)
        {
            textView.setSingleLine(true);
        }
        if (TableViewConstants.CELL_TEXT_ELLIPSIZE)
        {
            textView.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textView.getText() + "";
                int ellipsisCount = textView.getLayout().getEllipsisCount(textView.getLineCount() - 1);
                if (ellipsisCount > 0) {
                    Toast.makeText(mContext, text,
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    textView.setOnClickListener(null);
                }

            }
        });
        textView.setLongClickable(true);
//        textView.setTextIsSelectable(true);
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TextView textView  =(TextView) v;
                ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(textView.getText().toString().trim());
                Toast.makeText(mContext, "复制文本成功",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        if ((textView.getGravity() == (Gravity.CENTER | Gravity.LEFT)))
        {
            LinearLayout layout  = new LinearLayout(mContext);
            LinearLayout.LayoutParams layoutViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setGravity(textView.getGravity());
            layout.setLayoutParams(layoutViewParams);
            layout.addView(textView);
            return layout;
        }
        return textView;
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
    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearView;
        public ViewHolder(View view) {
            super(view);
            linearView = (LinearLayout) view.findViewById(R.id.customized_table_view_data_body_item_linear);
        }
    }
}
