package com.maxplus1.db.starter.config.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.maxplus1.db.starter.config.mybatis.MybatisAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnClass(DruidDataSource.class)
@AutoConfigureBefore(MybatisAutoConfiguration.class)
@Import({DruidDataSourceConfiguration.class})
@Slf4j
public class DruidAutoConfiguration {
}
