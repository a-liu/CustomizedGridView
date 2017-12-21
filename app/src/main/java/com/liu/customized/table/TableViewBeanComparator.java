package com.liu.customized.table;

import java.util.Comparator;

/**
 * Created by liu.jianfei on 2017/12/20.
 */

public class TableViewBeanComparator implements Comparator {
    /**
     * 排序方式
     */
    public enum ORDER_TYPE
    {
        ASC,
        DESC,
    }
    private boolean mConvertIntegerFlag;
    private int mPosition;
    private ORDER_TYPE mOrderType;


    public TableViewBeanComparator(boolean convertIntegerFlag, int position, ORDER_TYPE orderType) {
        super();
        this.mConvertIntegerFlag = convertIntegerFlag;
        this.mPosition = position;
        this.mOrderType = orderType;
    }

    private Object getCellValue(TableViewRowBean bean, int position)
    {
        Object result = null;
        if (bean != null && position >= 0 && position <bean.getColumnCells().size())
        {
            result = bean.getColumnCells().get(position).getValue();
        }

        return result;
    }
    @Override
    public int compare(Object o1, Object o2) {
        int result = 0;

        if(null != o1 && null != o2)
        {
            try {
                Object obj1=getCellValue((TableViewRowBean) o1, mPosition);
                Object obj2=getCellValue((TableViewRowBean) o2, mPosition);
                if(mConvertIntegerFlag && obj1 instanceof Integer){
                    int num1;
                    int num2;
                    num1=(Integer)obj1;
                    num2=(Integer)obj2;
                    if(num1 > num2){
                        result = 1;
                    }else if(num1 < num2){
                        result = -1;
                    }else{
                        return 0;
                    }
                }else{
                    result = obj1.toString().compareTo(obj2.toString());
                }
            }  catch (Exception e) {
                // 出错不做任何处理
                result = 0;
            }
        }
        if (this.mOrderType == ORDER_TYPE.DESC)
        {
            result *= -1;
        }
        return result;
    }
}
