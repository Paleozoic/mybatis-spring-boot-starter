package com.maxplus1.db.starter.config.common;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 此类是一个通用类，用于接收前端提交到后台的数据。
 * 通常用于表单查询的接收。
 * 如果需要扩展，可以继承ReqData。
 */
@Data
public class ReqData {
    /**
     * 分页大小
     */
    private Integer pageSize = 30;
    /**
     * 当前页码
     */
    private Integer pageNum = 1;
    /**
     * 查询关键字
     */
    private String search;

    /**
     * 多列排序
     * SQL排序的处理
     */
    private List<Sort> sorts;
    /**
     * 单列排序
     * SQL排序的处理
     */
    private Sort sort;
    /**
     * 通用参数
     */
    private Map params;
    /**
     * 访问者，通常用于处理权限
     */
    private String userId;


}
