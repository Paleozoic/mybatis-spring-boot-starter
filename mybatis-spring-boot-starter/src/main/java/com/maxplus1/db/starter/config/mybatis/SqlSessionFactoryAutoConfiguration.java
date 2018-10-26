package com.maxplus1.db.starter.config.mybatis;

import com.maxplus1.db.starter.config.BeanUtils;
import com.maxplus1.db.starter.config.Const;
import com.maxplus1.db.starter.config.druid.utils.CharMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.Map;

import static java.util.Collections.emptyMap;

public class SqlSessionFactoryAutoConfiguration {


    @Autowired
    private MybatisProperties mybatisProperties;

    /**
     * 多SQL会话工厂注册
     */
    static class DynamicSqlSessionFactoryRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

        /**
         * 多数据源属性配置
         */
        private Map<String, Object> dataSources;

        @Override
        public void setEnvironment(Environment environment) {
            this.dataSources = Binder.get(environment)
                    .bind(Const.PROP_PREFIX.MyBatis.val(), Bindable.mapOf(String.class, Object.class))
                    .orElse(emptyMap());
        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
            this.dataSources.keySet().forEach(dataSourceName -> {
                // 注册 BeanDefinition
                String camelName = CharMatcher.separatedToCamel().apply(dataSourceName);
                // 添加数据源
                BeanDefinition beanDefinition =
                        genericTransactionManagerBeanDefinition(camelName+Const.BEAN_SUFFIX.DataSource.val());
                registry.registerBeanDefinition(camelName+ Const.BEAN_SUFFIX.TransactionManager.val(), beanDefinition);

            });
        }


    }


    /**
     * 构造 BeanDefinition，通过 MybatisProperties 实现继承 'spring.maxplus1.mybatis' 的配置
     *
     * @return BeanDefinition
     */
    private static BeanDefinition genericTransactionManagerBeanDefinition(String dataSourceBeanName) {
        return BeanUtils.genericBeanDefinition(DataSourceTransactionManager.class,"dataSource",dataSourceBeanName);
    }



}
