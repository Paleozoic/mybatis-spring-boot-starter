package com.maxplus1.db.starter.config.common;

import lombok.Data;

@Data
public class Sort {
    /**
     * 排序字段
     */
    private String column;
    /**
     * 升序/降序
     */
    private String order;
}
