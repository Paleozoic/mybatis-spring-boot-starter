package com.maxplus1.db.starter.config.mybatis.common.sp;

import lombok.Data;

import java.util.Map;

@Data
public class SpExtData extends SpData{
    private Map<String,Object> map;
}
