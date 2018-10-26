package com.maxplus1.db.starter.config.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.maxplus1.db.starter.config.Const;
import com.maxplus1.db.starter.config.druid.utils.CharMatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import static java.util.Collections.emptyMap;

/**
 * Druid 数据源配置
 *
 * @author trang
 */
@Import(DruidDataSourceConfiguration.DruidDataSourceImportSelector.class)
@Slf4j
public class DruidDataSourceConfiguration {


    /**
     * 多数据源注册
     * 加载顺序：EnvironmentAware=>ImportBeanDefinitionRegistrar=>ApplicationContextAware
     * 读取环境配置=》注册Bean=》生成上下文
     *
     * @author trang
     */
    static class DynamicDataSourceRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

        private Map<String, Object> dataSources;

        @Override
        public void setEnvironment(Environment environment) {
            this.dataSources = Binder.get(environment)
                    .bind(Const.PROP_PREFIX.Druid.val(), Bindable.mapOf(String.class, Object.class))
                    .orElse(emptyMap());
        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
            this.dataSources.keySet().forEach(dataSourceName -> {
                // 注册 BeanDefinition
                String camelName = CharMatcher.separatedToCamel().apply(dataSourceName);
                registry.registerBeanDefinition(camelName, genericDruidBeanDefinition());
                // 注册以 DataSource 为后缀的别名
                if (!StringUtils.endsWithIgnoreCase(camelName, Const.BEAN_SUFFIX.DataSource.val())) {
                    registry.registerAlias(camelName, camelName + Const.BEAN_SUFFIX.DataSource.val());
                }
            });
        }

    }

    /**
     * 构造 BeanDefinition，通过 DruidDataSourceWrapper 实现继承 'spring.maxplus1.druid' 的配置
     *
     * @return BeanDefinition druidBeanDefinition
     */
    private static BeanDefinition genericDruidBeanDefinition() {
        return BeanDefinitionBuilder.genericBeanDefinition(DruidDataSourceWrapper.class)
                .setInitMethodName("init")
                .setDestroyMethodName("close")
                .getBeanDefinition();
    }


    /**
     * DruidDataSource 的 Bean 处理器，将各数据源的自定义配置绑定到 Bean
     *
     * @author trang
     */
    static class DruidDataSourceBeanPostProcessor implements EnvironmentAware, BeanPostProcessor {

        private final List<DruidDataSourceCustomizer> customizers;
        private Environment environment;
        private Map<String, Object> dataSources;

        public DruidDataSourceBeanPostProcessor(ObjectProvider<List<DruidDataSourceCustomizer>> customizers) {
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
            if (bean instanceof DruidDataSource) {
                // 设置 Druid 名称
                DruidDataSource druidDataSource = (DruidDataSource) bean;
                druidDataSource.setName(beanName);
                // 将 'spring.maxplus1.druid.data-sources.${name}' 的配置绑定到 Bean
                if (!dataSources.isEmpty()) {
                    Binder.get(environment).bind(Const.PROP_PREFIX.Druid.val() + "." + beanName, Bindable.ofInstance(druidDataSource));
                }
                // 定制化配置，拥有最高优先级，会覆盖之前已有的配置
                customizers.forEach(customizer -> customizer.customize(druidDataSource));
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

    }

    /**
     * 数据源选择器
     * 当配置文件中存在 spring.datasource.druid.data-sources 属性时为多数据源
     * 不存在则为单数据源
     *
     * @author trang
     */
    static class DruidDataSourceImportSelector implements ImportSelector, EnvironmentAware {


        @Override
        public void setEnvironment(Environment environment) {

        }

        @Override
        public String[] selectImports(AnnotationMetadata metadata) {
            Builder<Class<?>> imposts = Stream.<Class<?>>builder().add(DruidDataSourceBeanPostProcessor.class);
            imposts.add(DynamicDataSourceRegistrar.class);
            return imposts.build().map(Class::getName).toArray(String[]::new);
        }

    }

}