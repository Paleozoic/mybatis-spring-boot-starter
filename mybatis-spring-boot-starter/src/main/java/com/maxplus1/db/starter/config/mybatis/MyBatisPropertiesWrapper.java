package com.maxplus1.db.starter.config.mybatis;

import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.maxplus1.mybatis")
public class MyBatisPropertiesWrapper extends MybatisProperties {



}
