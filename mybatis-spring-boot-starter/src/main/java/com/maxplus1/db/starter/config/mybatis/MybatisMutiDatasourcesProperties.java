package com.maxplus1.db.starter.config.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties("spring.maxplus1.mybatis")
public class MybatisMutiDatasourcesProperties {

    private List<MybatisProperties> multiDatasources;
    
}
