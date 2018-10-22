package com.maxplus1.db.starter.config.mybatis;

import lombok.Data;

import java.util.List;

@Data
public class DruidDataSources {

    private List<DruidDataSource> dataSources;
}
