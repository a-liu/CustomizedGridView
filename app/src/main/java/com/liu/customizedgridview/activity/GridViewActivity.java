package com.liu.customizedgridview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;

import com.liu.customizedgridview.R;
import com.liu.customizedgridview.gridview.GridViewCellBean;
import com.liu.customizedgridview.gridview.GridViewRowBean;
import com.liu.customizedgridview.gridview.WrapGridView;

import java.util.ArrayList;
import java.util.List;

public class GridViewActivity extends AppCompatActivity {
    private List<GridViewRowBean> rowHeaders = new ArrayList<>();
    private List<GridViewRowBean> rowDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        initData();
    }

    private void initData() {
        String[] bodyValues = new String[] {
                "准时",  "非常绅士","非常有礼貌","很会照顾女生",
                "我的男神是个大暖男哦","谈吐优雅","送我到楼下",
                "迟到","态度恶劣","有不礼貌行为",
                "有侮辱性语言有暴力倾向","人身攻击",
                "临时改变行程","客户迟到并无理要求延长约会时间客户迟到并无理要求延长约会时间客户迟到并无理要求延长约会时间","F"
        };

        String[] headerValues = new String[] {
                "第一列",  "第二列","第三列","内容分析",
                "客户要求","材料名称","迟到原因",
                "请假时间","穿件","更新内容",
                "内容概要","延长时间",
                "物资编码","治具板编号","特征值"
        };
        for (int i=0; i< 1; i++)
        {
            GridViewRowBean rowBean = new GridViewRowBean();
            int rowIndex = i+1;
            rowBean.setId("row" + rowIndex);
            rowBean.setWrap(false);
            rowBean.setRowNumber(rowIndex);
            List<GridViewCellBean> rowCells = new ArrayList<GridViewCellBean>();
            for(int j=0; j< headerValues.length; j++)
            {
                int colIndex = j+1;
                GridViewCellBean cellBean = new GridViewCellBean("col" + colIndex,  headerValues[j]);
                cellBean.setGravity(Gravity.CENTER);
                rowCells.add(cellBean);
            }

            rowBean.setColumnCells(rowCells);
            rowHeaders.add(rowBean);
        }

        for (int i=0; i< 100; i++)
        {
            GridViewRowBean rowBean = new GridViewRowBean();
            int rowIndex = i+1;
            rowBean.setId("row" + rowIndex);
            rowBean.setWrap(false);
            rowBean.setRowNumber(rowIndex);
            List<GridViewCellBean> rowCells = new ArrayList<GridViewCellBean>();
            for(int j=0; j< bodyValues.length; j++)
            {
                int colIndex = j+1;
//                GridViewCellBean cellBean = new GridViewCellBean("col" + colIndex,  "(" + rowIndex + ":" + colIndex + ")" + bodyValues[j]);
                GridViewCellBean cellBean = new GridViewCellBean("col" + colIndex, String.format("%d%d:%s",rowIndex, colIndex, bodyValues[j]));
                cellBean.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                rowCells.add(cellBean);
            }

            rowBean.setColumnCells(rowCells);
            rowDatas.add(rowBean);
        }
        try {
            WrapGridView gridView =
                    new WrapGridView.Builder(this, R.id.grid_view_data_rows)
                    .headers(rowHeaders)
                    .rowDatas(rowDatas)
                    .allRowExpand(false)
                    .wrapRowFlag(true)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        ListView listDataView = (ListView) findViewById(R.id.grid_view_data_rows);
//        GridViewDataListAdapter listDataAdapter = new GridViewDataListAdapter(this, rowHeaders, rowDatas, false, true, false, null);
//        listDataView.setAdapter(listDataAdapter);


    }

}
