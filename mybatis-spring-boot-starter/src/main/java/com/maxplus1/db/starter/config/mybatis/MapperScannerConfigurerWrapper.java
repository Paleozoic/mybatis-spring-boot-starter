package com.maxplus1.db.starter.config.mybatis;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.maxplus1.mybatis")
public class MapperScannerConfigurerWrapper extends MapperScannerConfigurer {
}
