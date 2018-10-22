package com.maxplus1.db.starter.config.mybatis.common;

import lombok.Data;

import java.util.Map;

@Data
public class ReqData {
    private Integer pageSize = 30;
    private Integer pageNum = 1;
    private String search;
    private String sort;
    private String order;
    private Map params;


}
