package com.liu.customized.table;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liu.customized.R;

import java.util.ArrayList;
import java.util.List;


public class TableViewDataRowAdapter extends RecyclerView.Adapter<TableViewDataRowAdapter.ViewHolder> {
    private TableViewDataListAdapter.DISPLAY_ROW_MEMBER mDisplayFlag;
    private List<TableViewCellBean> mDatas;
    private boolean isSelected = false;

    private List<TableViewCellBean> selectList;

    private boolean mShortTextFlag;
    private Activity mContext;
    private int mDisplayColumnCount;
    private int mFirstRowColumnCount;
    private boolean mWrapRowFlag;
    private OnGridViewRowCellActionListener mOnGridViewRowCellActionListener;
    private List<TableViewCellBean> mHeaders;
    private float xDown,yDown, xUp;
    private boolean isLongClickModule = false;
    private boolean isLongClicking = false;
    private int mFixColumnCount;
    public void setOnGridViewRowCellActionListener(OnGridViewRowCellActionListener listener)
    {
        mOnGridViewRowCellActionListener = listener;
    }
    public void setColumnDatas(List<TableViewCellBean> datas)
    {
        this.mDatas = datas;
    }
    public TableViewDataRowAdapter(Activity context, int totalSpan, int fixColumnCount, List<TableViewCellBean> headers, List<TableViewCellBean> cellList, boolean wrapRowFlag, int initColumnCount, boolean shortText) {
        this.mDatas = cellList;
        this.mHeaders = headers;
        this.mContext = context;
        this.mFixColumnCount = fixColumnCount;
        this.mWrapRowFlag = wrapRowFlag;
        this.mDisplayColumnCount = initColumnCount;
        selectList = new ArrayList<>();
        this.mShortTextFlag = shortText;
        this.mDisplayFlag = TableViewDataListAdapter.DISPLAY_ROW_MEMBER.COLUMN;
        // 计算行列数
        this.mFirstRowColumnCount = calculateFirstRowColumnCount(totalSpan, cellList);
    }
    public TableViewDataRowAdapter(Activity context, int totalSpan, int fixColumnCount, List<TableViewCellBean> headers, List<TableViewCellBean> cellList, boolean wrapRowFlag, boolean allRowExpandFlag, boolean shortText) {
        this.mDatas = cellList;
        this.mFixColumnCount = fixColumnCount;
        this.mHeaders = headers;
        this.mContext = context;
        this.mWrapRowFlag = wrapRowFlag;
        selectList = new ArrayList<>();
        this.mShortTextFlag = shortText;
        this.mDisplayFlag = TableViewDataListAdapter.DISPLAY_ROW_MEMBER.ROW;
        // 计算行列数
        this.mFirstRowColumnCount = calculateFirstRowColumnCount(totalSpan, cellList);

        if (allRowExpandFlag)
        {
            if (cellList != null)
            {
                this.mDisplayColumnCount = cellList.size();
            }
            else
            {
                this.mDisplayColumnCount = 0;
            }
        }
        else
        {
            mDisplayColumnCount = this.mFirstRowColumnCount;
        }
    }

    @Override
    public TableViewDataRowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
//        if (!mWrapRowFlag)
//        {
//            view = createViewById();
//        }
//        else
//        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_data_row_cell, parent, false);
//        }
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_data_row_cell, parent, false);

        final ViewHolder vh = new ViewHolder(view);
        return vh;
    }
    public int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                mContext.getResources().getDisplayMetrics());
    }

    private View createViewById()
    {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        TextView tvLabel = new TextView(mContext);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams1.setMargins(dp2px(-1),dp2px(-1),dp2px(-1),dp2px(-1));
        layoutParams1.gravity = Gravity.CENTER;
        tvLabel.setLayoutParams(layoutParams1);
        tvLabel.setPadding(dp2px(5),dp2px(5),dp2px(5),dp2px(5));
        tvLabel.setGravity(Gravity.CENTER);
        tvLabel.setTextSize(16);
        tvLabel.setVisibility(View.GONE);
        tvLabel.setId(R.id.grid_view_data_row_cell_header_tv_new);
        layout.addView(tvLabel);

        TextView tvValue = new TextView(mContext);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams2.weight = 1;
        layoutParams2.setMargins(dp2px(-1),dp2px(-1),dp2px(-1),dp2px(-1));
        tvValue.setLayoutParams(layoutParams2);
        tvValue.setPadding(dp2px(2),dp2px(5),dp2px(5),dp2px(0));
        tvValue.setGravity(Gravity.CENTER);
        tvValue.setTextSize(16);
        tvValue.setId(R.id.grid_view_data_row_cell_tv_new);
        tvValue.setBackgroundResource(R.drawable.grid_view_content_text_bg);
        layout.addView(tvValue);
        return layout;
    }
//
//    private static ViewHolder vh;
//
//    private static ViewHolder getInstance(ViewGroup parent){
//        if(vh == null) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_data_row_cell, parent, false);
//            ViewHolder vh = new ViewHolder(view);
//        }
//        return vh;
//    }

    @Override
    public void onBindViewHolder(final TableViewDataRowAdapter.ViewHolder holder, final int position) {
        if (this.mShortTextFlag)
        {
            holder.mTextView.setText(mDatas.get(position).getShortText());
        }
        else{
            holder.mTextView.setText(mDatas.get(position).getText());
        }
        holder.itemView.setTag(mDatas.get(position));
        holder.mTextView.setGravity(mDatas.get(position).getGravity());

        if (mDatas.get(position).getColNumber() < mFirstRowColumnCount)
        {
            holder.mHeaderView.setText("");
            holder.mHeaderView.setVisibility(View.GONE);
            holder.mTextView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        xDown= event.getX();
//                        yDown = event.getY();
                        if (mPreviousUpEvent != null
                                && mCurrentDownEvent != null
                                && isConsideredDoubleTap(mCurrentDownEvent,
                                mPreviousUpEvent, event)) {
//                            Toast.makeText(mContext, "double clicked.",
//                                    Toast.LENGTH_SHORT).show();
                            if (mDisplayColumnCount < mDatas.size())
                            {
                                mDisplayColumnCount = mDatas.size();
                            }
                            else
                            {
                                mDisplayColumnCount = mFirstRowColumnCount;
                            }

                            mOnGridViewRowCellActionListener.onFirstRowDoubleClicked(TableViewDataRowAdapter.this, v);
                        }
                        mCurrentDownEvent = MotionEvent.obtain(event);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        mPreviousUpEvent = MotionEvent.obtain(event);
//
//                        //获取松开时的x坐标
//                        if(isLongClickModule){
//                            isLongClickModule = false;
//                            isLongClicking = false;
//                        }
//                        xUp = event.getX();
//                        //按下和松开绝对值差当大于20时滑动，否则不显示
//                        if ((xUp - xDown) > 20)
//                        {
//                            //添加要处理的内容
//                            Toast.makeText(mContext, "moved: right" + event.getX(),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                        else if((xUp - xDown ) < -20)
//                        {
//                            //添加要处理的内容
//                            Toast.makeText(mContext, "moved: left" + event.getX(),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                        else if( 0 == (xDown - xUp))
//                        {
//                            int viewWidth = v.getWidth();
//                            if( xDown < viewWidth/3 )
//                            {
//                                //靠左点击
//                            }
//                            else if(xDown > viewWidth/3 && xDown < viewWidth * 2 /3)
//                            {
//                                //中间点击
//                            }
//                            else
//                            {
//                                //靠右点击
//                            }
//                        }
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE)
                    {
                        //当滑动时背景为选中状态 //检测是否长按,在非长按时检测
//                        if(!isLongClickModule)
//                        {
//                            isLongClickModule = isLongPressed(xDown, yDown, event.getX(),
//                                    event.getY(),event.getDownTime() ,event.getEventTime(),300);
//                        }
//                        if(isLongClickModule && !isLongClicking){
//                            //处理长按事件
//                            isLongClicking = true; }
                    }
                    else
                    {
                        return false;
                    }

                    return true;
                }
            });
        }
        else
        {
            if (mWrapRowFlag)
            {
                holder.mHeaderView.setText(mHeaders.get(position).getText());
                holder.mHeaderView.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.mHeaderView.setText("");
                holder.mHeaderView.setVisibility(View.GONE);
            }

        }
        // 省略字符时，Toast显示
        if (this.mShortTextFlag)
        {
            holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, mDatas.get(position).getText(),
                            Toast.LENGTH_SHORT).show();

                }
            });
        }


    }

    @Override
    public int getItemCount() {
//        return mDatas.size();
        return mDisplayColumnCount;
    }
    private boolean isLongPressed(float lastX,float lastY,
                                  float thisX,float thisY,
                                  long lastDownTime,long thisEventTime,
                                  long longPressTime){
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        long intervalTime = thisEventTime - lastDownTime;
//        if(offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime){
        if(offsetX <= 10 && intervalTime >= longPressTime){
            return true;
        }
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mHeaderView;
        public TextView mTextView;
        public ViewHolder(View view) {
            super(view);
//            if (!mWrapRowFlag)
//            {
//                mHeaderView = (TextView) view.findViewById(R.id.grid_view_data_row_cell_header_tv_new);
//                mTextView = (TextView) view.findViewById(R.id.grid_view_data_row_cell_tv_new);
//            }
//            else {
                mHeaderView = (TextView) view.findViewById(R.id.grid_view_data_row_cell_header_tv);
                mTextView = (TextView) view.findViewById(R.id.grid_view_data_row_cell_tv);
//            }

//            mTextView.onTouchEvent()
        }
    }

    public List<TableViewCellBean> getSelectData(){
        return selectList;
    }

    private int calculateFirstRowColumnCount(int totalSpan, List<TableViewCellBean> cells)
    {
        int result = 0;
        // 计算行列数
        int spanCount = 0;
        for(int i=0; i < cells.size(); i++)
        {
            spanCount += cells.get(i).getColSpan();
            if (spanCount >= totalSpan)
            {
                result = i+1;
                break;
            }
        }
        return result;
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

    public interface OnGridViewRowCellActionListener {
        /**
         * Called when the click of double touched.
         * All row will be filled by this listener.
         * @param sender Current object.
         * @param v The view whose state has changed.
         */
        void onFirstRowDoubleClicked(Object sender, View v);
    }
}
