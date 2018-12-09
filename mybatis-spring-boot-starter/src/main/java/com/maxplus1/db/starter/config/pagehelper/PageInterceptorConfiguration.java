package com.maxplus1.db.starter.config.pagehelper;

import com.maxplus1.db.starter.config.Const;
import com.maxplus1.db.starter.config.utils.CharMatcher;
import com.maxplus1.db.starter.config.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

import static java.util.Collections.emptyMap;

@Configuration
@Slf4j
public class PageInterceptorConfiguration implements EnvironmentAware, ImportBeanDefinitionRegistrar, BeanPostProcessor {

    private Environment environment;
    /**
     * 多数据源分页拦截器PageInterceptor注册
     * 多数据源属性配置
     */
    private Map<String, Object> MyBatisPageHelperProps;


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        this.MyBatisPageHelperProps = Binder.get(environment)
                .bind(Const.PROP_PREFIX.MyBatisPageHelper.val(), Bindable.mapOf(String.class, Object.class))
                .orElse(emptyMap());
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        this.MyBatisPageHelperProps.keySet().forEach(dataSourceName -> {
            // 注册 BeanDefinition
            String camelName = CharMatcher.separatedToCamel().apply(dataSourceName);
            BeanDefinition beanDefinition =
                    genericPageInterceptorBeanDefinition();
            registry.registerBeanDefinition(camelName + Const.BEAN_SUFFIX.PageInterceptor.val(), beanDefinition);

        });
    }


    /**
     * Bean 处理器，将各数据源的自定义配置绑定到 Bean
     * Bean初始化之前的拦截器，会遍历所有的Bean
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof PageInterceptorWrapper) {
            // 设置 Druid 名称
            PageInterceptorWrapper pageInterceptorWrapper = (PageInterceptorWrapper) bean;
            // 将 'spring.maxplus1.mybatis.data-sources.${name}' 的配置绑定到 Bean
            if (!MyBatisPageHelperProps.isEmpty()) {
                Binder.get(environment).bind(Const.PROP_PREFIX.MyBatisPageHelper.val() + "." + StringUtils.getFirstCamelName(beanName), Bindable.ofInstance(pageInterceptorWrapper));
            }
        }
        return bean;
    }


    /**
     * 构造 BeanDefinition
     *
     * @return BeanDefinition
     */
    private static BeanDefinition genericPageInterceptorBeanDefinition() {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(PageInterceptorWrapper.class);
        return beanDefinitionBuilder.getBeanDefinition();
    }


 }
