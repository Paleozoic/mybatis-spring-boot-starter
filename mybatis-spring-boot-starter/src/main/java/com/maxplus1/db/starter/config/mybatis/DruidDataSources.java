package com.maxplus1.db.starter.config.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;

import java.util.List;

@Data
public class DruidDataSources {

    private List<DruidDataSource> dataSources;
}
