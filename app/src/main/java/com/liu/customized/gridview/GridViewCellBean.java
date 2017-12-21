package com.liu.customized.gridview;

/**
 * Created by liu.jianfei on 2017/12/8.
 */
public class GridViewCellBean {
    private String id;
    private int colNumber;
    private int rowNumber;
//    /**
//     * CELLTYPE(HEADER, BODY, FOOTER)
//     */
//    private CELL_TYPE cellType;
//    /**
//     * DATATYPE(Number, Alpha, Select, Check)
//     */
//    private DATA_TYPE dataType;
    private boolean enable;
    private int colSpan;
    private int rowSpan;
    private String displayFormatter;
    private Object value;
    private String text;


    private int shortTextLength = 10;
    private String shortText;
    private int gravity;

    public int getColNumber() {
        return colNumber;
    }

    public void setColNumber(int colNumber) {
        this.colNumber = colNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

//    public CELL_TYPE getCellType() {
//        return cellType;
//    }
//
//    public void setCellType(CELL_TYPE cellType) {
//        this.cellType = cellType;
//    }
//
//    public DATA_TYPE getDataType() {
//        return dataType;
//    }
//
//    public void setDataType(DATA_TYPE dataType) {
//        this.dataType = dataType;
//    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getColSpan() {
        return colSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public String getDisplayFormatter() {
        return displayFormatter;
    }

    public void setDisplayFormatter(String displayFormatter) {
        this.displayFormatter = displayFormatter;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getText() {
        if (displayFormatter == null ||
                displayFormatter.isEmpty())
        {
            if (value != null)
            {
                text = value.toString();
            }

        }
        else
        {
            // TODO BY FORMATER
            if (value != null)
            {
                text = value.toString();
            }
        }
        return text;
    }
    public String getShortText() {
        shortText = this.getText();
        if (shortText != null && shortText.length() > shortTextLength)
        {
            shortText = shortText.substring(0, shortTextLength) + "...";
        }
        return shortText;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public GridViewCellBean(String id, String value)
    {
        this.id = id;
        this.value = value;
    }
}

