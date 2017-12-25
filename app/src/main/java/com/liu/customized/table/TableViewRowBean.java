package com.liu.customized.table;

import java.util.List;

/**
 * Created by liu.jianfei on 2017/12/11.
 */

public class TableViewRowBean {
    private String id;
    private int rowNumber;
    private List<TableViewCellBean> columnCells;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public List<TableViewCellBean> getColumnCells() {
        return columnCells;
    }

    public void setColumnCells(List<TableViewCellBean> columnCells) {
        this.columnCells = columnCells;
    }
}
