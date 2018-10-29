package com.maxplus1.db.starter.config.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class })
@Import({MyBatisConfigurationConfiguration.class,TransactionManagerConfiguration.class,SqlSessionFactoryConfiguration.class,SqlSessionTemplateConfiguration.class})
public class MybatisAutoConfiguration {



}