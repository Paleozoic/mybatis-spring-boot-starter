package com.maxplus1.db.starter.config.common.sp;

import lombok.Data;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

@Data
public class SpPageData extends SpData{
    private Long total;
    private List<Map> list;
    private String sort;
    private String order;
    private int pageSize = 30;
    private int pageNo = 1;

    public SpPageData(){

    }
    public SpPageData(RowBounds rowBounds,String sort,String order){
        this.sort = sort;
        this.order = order;
        this.pageSize = rowBounds.getLimit();
        this.pageNo = rowBounds.getOffset();
    }
}
