package com.maxplus1.db.starter.config.mybatis;

import com.maxplus1.db.starter.config.Const;
import com.maxplus1.db.starter.config.druid.utils.CharMatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;

@Import(MyBatisConfigurationAutoConfiguration.MyBatisConfigurationImportSelector.class)
@Slf4j
@ConfigurationProperties(prefix = MyBatisConfigurationAutoConfiguration.MYBATIS_PREFIX)
public class MyBatisConfigurationAutoConfiguration extends MybatisProperties {

    public static final String MYBATIS_PREFIX = "spring.maxplus1.mybatis";

    /**
     * 多数据源的MyBatis配置注册
     * 加载顺序：EnvironmentAware=>ImportBeanDefinitionRegistrar=>ApplicationContextAware
     * 读取环境配置=》注册Bean=》生成上下文
     */
    static class DynamicSqlSessionFactoryRegistrar implements EnvironmentAware,ImportBeanDefinitionRegistrar {

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
                String dataSourceCamelName = CharMatcher.separatedToCamel().apply(dataSourceName);

                // 注册MyBatis Configuration
                BeanDefinition beanDefinition =
                        genericMyBatisPropertiesWrapperBeanDefinition();

                // TODO 个性化配置覆盖公共配置

                registry.registerBeanDefinition(dataSourceCamelName + Const.BEAN_SUFFIX.MyBatisConfiguration.val(), beanDefinition);

            });
        }


    }

    /**
     * 构造 BeanDefinition，通过 MyBatisPropertiesWrapper 实现继承 'spring.maxplus1.mybatis' 的配置
     *
     * @return BeanDefinition
     */
    private static BeanDefinition genericMyBatisPropertiesWrapperBeanDefinition() {
        return BeanDefinitionBuilder.genericBeanDefinition(MyBatisPropertiesWrapper.class)
                .getBeanDefinition();
    }


    /**
     * 构造 BeanDefinition，通过 MybatisProperties 实现继承 'spring.maxplus1.mybatis' 的配置
     *
     * @return BeanDefinition
     */
    private static BeanDefinition genericMyBatisConfigurationBeanDefinition(MybatisProperties mybatisProperties) {
        Configuration configuration = mybatisProperties.getConfiguration();
        if(configuration==null){
            configuration = new Configuration();
        }
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(Configuration.class);

        beanDefinitionBuilder.addPropertyValue("safeRowBoundsEnabled",configuration.isSafeRowBoundsEnabled());
        beanDefinitionBuilder.addPropertyValue("safeResultHandlerEnabled",configuration.isSafeResultHandlerEnabled());
        beanDefinitionBuilder.addPropertyValue("mapUnderscoreToCamelCase",configuration.isMapUnderscoreToCamelCase());
        beanDefinitionBuilder.addPropertyValue("aggressiveLazyLoading",configuration.isAggressiveLazyLoading());
        beanDefinitionBuilder.addPropertyValue("multipleResultSetsEnabled",configuration.isMultipleResultSetsEnabled());
        beanDefinitionBuilder.addPropertyValue("useGeneratedKeys",configuration.isUseGeneratedKeys());
        beanDefinitionBuilder.addPropertyValue("useColumnLabel",configuration.isUseColumnLabel());
        beanDefinitionBuilder.addPropertyValue("cacheEnabled",configuration.isCacheEnabled());
        beanDefinitionBuilder.addPropertyValue("callSettersOnNulls",configuration.isCallSettersOnNulls());
        beanDefinitionBuilder.addPropertyValue("useActualParamName",configuration.isUseActualParamName());
        beanDefinitionBuilder.addPropertyValue("returnInstanceForEmptyRow",configuration.isReturnInstanceForEmptyRow());
        beanDefinitionBuilder.addPropertyValue("logPrefix",configuration.getLogPrefix());
        beanDefinitionBuilder.addPropertyValue("logImpl",configuration.getLogImpl());
        beanDefinitionBuilder.addPropertyValue("vfsImpl",configuration.getVfsImpl());
        beanDefinitionBuilder.addPropertyValue("localCacheScope",configuration.getLocalCacheScope());
        beanDefinitionBuilder.addPropertyValue("jdbcTypeForNull",configuration.getJdbcTypeForNull());
        beanDefinitionBuilder.addPropertyValue("lazyLoadTriggerMethods",configuration.getLazyLoadTriggerMethods());
        beanDefinitionBuilder.addPropertyValue("defaultStatementTimeout",configuration.getDefaultStatementTimeout());
        beanDefinitionBuilder.addPropertyValue("defaultFetchSize",configuration.getDefaultFetchSize());
        beanDefinitionBuilder.addPropertyValue("defaultExecutorType",configuration.getDefaultExecutorType());
        beanDefinitionBuilder.addPropertyValue("autoMappingBehavior",configuration.getAutoMappingBehavior());
        beanDefinitionBuilder.addPropertyValue("autoMappingUnknownColumnBehavior",configuration);
        beanDefinitionBuilder.addPropertyValue("variables",configuration);
        beanDefinitionBuilder.addPropertyValue("reflectorFactory",configuration);
        beanDefinitionBuilder.addPropertyValue("objectFactory",configuration);
        beanDefinitionBuilder.addPropertyValue("objectWrapperFactory",configuration);
        beanDefinitionBuilder.addPropertyValue("lazyLoadingEnabled",configuration);
        beanDefinitionBuilder.addPropertyValue("proxyFactory",configuration);
        beanDefinitionBuilder.addPropertyValue("databaseId",configuration);


        return  beanDefinitionBuilder.getBeanDefinition();
    }


    /**
     * Bean 处理器，将各数据源的自定义配置绑定到 Bean
     *
     */
    static class MyBatisConfigurationBeanPostProcessor implements EnvironmentAware, BeanPostProcessor {

        private Map<String, Object> dataSources;



        @Override
        public void setEnvironment(Environment environment) {
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







    static class MyBatisConfigurationImportSelector implements ImportSelector, EnvironmentAware {


        @Override
        public void setEnvironment(Environment environment) {

        }

        @Override
        public String[] selectImports(AnnotationMetadata metadata) {
            Stream.Builder<Class<?>> imposts = Stream.<Class<?>>builder().add(MyBatisConfigurationBeanPostProcessor.class);
            imposts.add(DynamicSqlSessionFactoryRegistrar.class);
            return imposts.build().map(Class::getName).toArray(String[]::new);
        }

    }



}