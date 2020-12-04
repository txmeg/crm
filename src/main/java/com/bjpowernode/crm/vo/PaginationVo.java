package com.bjpowernode.crm.vo;

import java.util.List;

public class PaginationVo<T> {

    private String total;
    private List<T> dataList;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
