package com.maxplus1.db.starter.config.mybatis;

import com.maxplus1.db.starter.config.Const;
import com.maxplus1.db.starter.config.utils.CharMatcher;
import com.maxplus1.db.starter.config.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

import static java.util.Collections.emptyMap;
/**
 * 多数据源的MyBatis配置注册
 * 加载顺序：EnvironmentAware=>ImportBeanDefinitionRegistrar=>ApplicationContextAware
 * 读取环境配置=》注册Bean=》生成上下文
 */
@org.springframework.context.annotation.Configuration
@Slf4j
public class MyBatisConfigurationConfiguration implements EnvironmentAware, ImportBeanDefinitionRegistrar, BeanPostProcessor {


    private Environment environment;
    /**
     * 多数据源属性配置 <dataSource,props>
     * props是个性化配置
     */
    private Map<String, Object> MyBatis;
    private Map<String, Object> Druid;


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        this.MyBatis = Binder.get(environment)
                .bind(Const.PROP_PREFIX.MyBatis.val(), Bindable.mapOf(String.class, Object.class))
                .orElse(emptyMap());
        this.Druid = Binder.get(environment)
                .bind(Const.PROP_PREFIX.Druid.val(), Bindable.mapOf(String.class, Object.class))
                .orElse(emptyMap());
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {

        this.MyBatis.keySet().forEach(dataSourceName -> {
            // 注册 BeanDefinition
            String dataSourceCamelName = CharMatcher.separatedToCamel().apply(dataSourceName);

            // 注册MyBatis Configuration
            BeanDefinition beanDefinition =
                    genericMyBatisConfigurationBeanDefinition();

            registry.registerBeanDefinition(dataSourceCamelName + Const.BEAN_SUFFIX.MyBatisConfiguration.val(), beanDefinition);

        });
    }


    /**
     * Bean初始化之前的拦截器，会遍历所有的Bean
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Configuration) {
            // 设置 Druid 名称
            Configuration configuration = (Configuration) bean;
            // 将 'spring.maxplus1.mybatis.data-sources.${name}' 的配置绑定到 Bean
            if (!Druid.isEmpty()) {
                Binder.get(environment).bind(Const.PROP_PREFIX.MyBatis.val() + "." + StringUtils.getFirstCamelName(beanName), Bindable.ofInstance(configuration));
            }
        }
        return bean;
    }


    /**
     * 构造 BeanDefinition，通过 ConfigurationWrapper 实现继承 'spring.maxplus1.mybatis' 的配置
     *
     * @return BeanDefinition
     */
    private static BeanDefinition genericMyBatisConfigurationBeanDefinition() {
        return BeanDefinitionBuilder.genericBeanDefinition(ConfigurationWrapper.class)
                .getBeanDefinition();
    }


}
