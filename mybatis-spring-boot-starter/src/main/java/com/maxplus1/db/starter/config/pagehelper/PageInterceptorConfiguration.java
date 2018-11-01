package com.maxplus1.db.starter.config.pagehelper;

import com.maxplus1.db.starter.config.BeanCustomizer;
import com.maxplus1.db.starter.config.Const;
import com.maxplus1.db.starter.config.utils.CharMatcher;
import com.maxplus1.db.starter.config.utils.StringUtils;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;

@Import(PageInterceptorConfiguration.PageInterceptorImportSelector.class)
@Slf4j
public class PageInterceptorConfiguration {

    /**
     * 多数据源分页拦截器PageInterceptor注册
     */
    static class DynamicPageInterceptorConfigurerRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

        /**
         * 多数据源属性配置
         */
        private Map<String, Object> dataSources;

        @Override
        public void setEnvironment(Environment environment) {
            this.dataSources = Binder.get(environment)
                    .bind(Const.PROP_PREFIX.MyBatisPageHelper.val(), Bindable.mapOf(String.class, Object.class))
                    .orElse(emptyMap());

        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
            this.dataSources.keySet().forEach(dataSourceName -> {
                // 注册 BeanDefinition
                String camelName = CharMatcher.separatedToCamel().apply(dataSourceName);

                BeanDefinition beanDefinition =
                        genericPageInterceptorBeanDefinition();
                registry.registerBeanDefinition(camelName+ Const.BEAN_SUFFIX.PageInterceptor.val(), beanDefinition);

            });
        }

    }

    /**
     * 构造 BeanDefinition
     *
     * @return BeanDefinition
     */
    private static BeanDefinition genericPageInterceptorBeanDefinition() {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(PageInterceptorWrapper.class);

        return   beanDefinitionBuilder
                .getBeanDefinition();
    }



    /**
     * Bean 处理器，将各数据源的自定义配置绑定到 Bean
     *
     */
    static class PageInterceptorBeanPostProcessor implements EnvironmentAware, BeanPostProcessor {

        private final List<BeanCustomizer<PageInterceptorWrapper>> customizers;
        private Environment environment;
        private Map<String, Object> dataSources;

        public PageInterceptorBeanPostProcessor(ObjectProvider<List<BeanCustomizer<PageInterceptorWrapper>>> customizers) {
            this.customizers = customizers.getIfAvailable(ArrayList::new);
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
            this.dataSources = Binder.get(environment)
                    .bind(Const.PROP_PREFIX.MyBatisPageHelper.val(), Bindable.mapOf(String.class, Object.class))
                    .orElse(emptyMap());
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
            if (bean instanceof PageInterceptorWrapper) {
                // 设置 Druid 名称
                PageInterceptorWrapper pageInterceptorWrapper = (PageInterceptorWrapper) bean;
                // 将 'spring.maxplus1.mybatis.data-sources.${name}' 的配置绑定到 Bean
                if (!dataSources.isEmpty()) {
                    Binder.get(environment).bind(Const.PROP_PREFIX.MyBatisPageHelper.val() + "." + StringUtils.getFirstCamelName(beanName), Bindable.ofInstance(pageInterceptorWrapper));
                }
                // 定制化配置，拥有最高优先级，会覆盖之前已有的配置
                customizers.forEach(customizer -> customizer.customize(pageInterceptorWrapper));
            }
            return bean;
        }

    }


    static class PageInterceptorImportSelector implements ImportSelector, EnvironmentAware {


        @Override
        public void setEnvironment(Environment environment) {

        }

        @Override
        public String[] selectImports(AnnotationMetadata metadata) {
            Stream.Builder<Class<?>> imposts = Stream.<Class<?>>builder().add(PageInterceptorConfiguration.PageInterceptorBeanPostProcessor.class);
            imposts.add(PageInterceptorConfiguration.DynamicPageInterceptorConfigurerRegistrar.class);
            return imposts.build().map(Class::getName).toArray(String[]::new);
        }


    }
}
