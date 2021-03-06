package com.maxplus1.db.starter.config.mybatis;

import com.maxplus1.db.starter.config.Const;
import com.maxplus1.db.starter.config.utils.CharMatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Collections.emptyMap;

@Configuration
@Slf4j
public class TransactionManagerConfiguration implements EnvironmentAware, ImportBeanDefinitionRegistrar, BeanPostProcessor {


    /**
     * 多事务管理器注册
     * 多数据源属性配置
     */
    private Map<String, Object> MyBatis;

    @Override
    public void setEnvironment(Environment environment) {
        this.MyBatis = Binder.get(environment)
                .bind(Const.PROP_PREFIX.MyBatis.val(), Bindable.mapOf(String.class, Object.class))
                .orElse(emptyMap());
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        final AtomicBoolean primary = new AtomicBoolean(true);
        this.MyBatis.keySet().forEach(dataSourceName -> {
            // 注册 BeanDefinition
            String camelName = CharMatcher.separatedToCamel().apply(dataSourceName);
            // 添加数据源
            BeanDefinition beanDefinition =
                    genericTransactionManagerBeanDefinition(camelName + Const.BEAN_SUFFIX.DataSource.val(), primary.getAndSet(false));
            // 注册以 DataSource Name 为别名的TransactionManager  用于@Transactional
            if (!StringUtils.endsWithIgnoreCase(camelName, Const.BEAN_SUFFIX.TransactionManager.val())) {
                registry.registerAlias(camelName + Const.BEAN_SUFFIX.TransactionManager.val(), camelName);
            }
            registry.registerBeanDefinition(camelName + Const.BEAN_SUFFIX.TransactionManager.val(), beanDefinition);

        });
    }


    /**
     * Bean 处理器，将各数据源的自定义配置绑定到 Bean
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSourceTransactionManager) {
            log.info("[INFO===>>>]inject the bean DataSourceTransactionManager[{}]", beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }



    /**
     * 构造 BeanDefinition，通过 MybatisProperties 实现继承 'spring.maxplus1.mybatis' 的配置
     *
     * @return BeanDefinition
     */
    private static BeanDefinition genericTransactionManagerBeanDefinition(String dataSourceBeanName, boolean primary) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class);
        beanDefinitionBuilder.addPropertyReference("dataSource", dataSourceBeanName);
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        beanDefinition.setPrimary(primary);
        return beanDefinition;
    }
}
