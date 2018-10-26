package com.maxplus1.db.starter.config.mybatis;

import com.maxplus1.db.starter.config.BeanUtils;
import com.maxplus1.db.starter.config.Const;
import com.maxplus1.db.starter.config.druid.DruidDataSourceCustomizer;
import com.maxplus1.db.starter.config.druid.utils.CharMatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;

@Import(SqlSessionFactoryAutoConfiguration.SqlSessionFactoryImportSelector.class)
@Slf4j
public class SqlSessionFactoryAutoConfiguration {


    @Autowired
    private MybatisProperties mybatisProperties;

    /**
     * 多SQL会话工厂注册
     */
    static class DynamicSqlSessionFactoryRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

        /**
         * 多数据源属性配置 <dataSource,props>
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
                // 添加参数
                BeanDefinition beanDefinition =
                        genericSqlSessionFactoryBeanDefinition(camelName+Const.BEAN_SUFFIX.SqlSessionFactory.val());

                registry.registerBeanDefinition(camelName+ Const.BEAN_SUFFIX.SqlSessionFactory.val(), beanDefinition);

            });
        }


    }

    /**
     * Bean 处理器，将各数据源的自定义配置绑定到 Bean
     *
     */
    static class SqlSessionFactoryBeanPostProcessor implements EnvironmentAware, BeanPostProcessor {

        private final List<DruidDataSourceCustomizer> customizers;
        private Environment environment;
        private Map<String, Object> dataSources;

        public SqlSessionFactoryBeanPostProcessor(ObjectProvider<List<DruidDataSourceCustomizer>> customizers) {
            this.customizers = customizers.getIfAvailable(ArrayList::new);
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
            this.dataSources = Binder.get(environment)
                    .bind(Const.PROP_PREFIX.Druid.val(), Bindable.mapOf(String.class, Object.class))
                    .orElse(emptyMap());
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof SqlSessionFactory) {
                System.out.println(bean);
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

    }

    /**
     * 构造 BeanDefinition，通过 MybatisProperties 实现继承 'spring.maxplus1.mybatis' 的配置
     *
     * @return BeanDefinition
     */
    private static BeanDefinition genericSqlSessionFactoryBeanDefinition(String dataSourceBeanName) {
        return BeanUtils.genericBeanDefinition(SqlSessionFactory.class,"dataSource",dataSourceBeanName);
    }


    static class SqlSessionFactoryImportSelector implements ImportSelector, EnvironmentAware {


        @Override
        public void setEnvironment(Environment environment) {

        }

        @Override
        public String[] selectImports(AnnotationMetadata metadata) {
            Stream.Builder<Class<?>> imposts = Stream.<Class<?>>builder().add(SqlSessionFactoryBeanPostProcessor.class);
            imposts.add(DynamicSqlSessionFactoryRegistrar.class);
            return imposts.build().map(Class::getName).toArray(String[]::new);
        }

    }



}
