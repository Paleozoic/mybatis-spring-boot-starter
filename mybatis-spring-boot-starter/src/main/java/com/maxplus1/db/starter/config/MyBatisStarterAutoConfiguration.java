package com.maxplus1.db.starter.config;

import com.maxplus1.db.starter.config.druid.DruidAutoConfiguration;
import com.maxplus1.db.starter.config.mybatis.MybatisAutoConfiguration;
import com.maxplus1.db.starter.config.pagehelper.PageHelperAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Import({DruidAutoConfiguration.class,
        MybatisAutoConfiguration.class,
        PageHelperAutoConfiguration.class})
@Configuration
@AutoConfigureBefore(
        name = {"com.maxplus1.access.starter.config.shiro.ShiroStarterAutoConfiguration"}
)
public class MyBatisStarterAutoConfiguration {
}
