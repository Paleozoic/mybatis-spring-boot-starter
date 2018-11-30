package com.maxplus1.db.starter.config.mybatis;


import org.apache.ibatis.session.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.maxplus1.mybatis.configuration")
public class ConfigurationWrapper extends Configuration {
}
