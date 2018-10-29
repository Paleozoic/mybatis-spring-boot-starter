package com.maxplus1.db.starter.config.mybatis;

import com.maxplus1.db.starter.config.BeanCustomizer;
import com.maxplus1.db.starter.config.Const;
import com.maxplus1.db.starter.config.druid.utils.CharMatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.Configuration;
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

@Import(MyBatisConfigurationConfiguration.MyBatisConfigurationImportSelector.class)
@Slf4j
public class MyBatisConfigurationConfiguration  {


    /**
     * 多数据源的MyBatis配置注册
     * 加载顺序：EnvironmentAware=>ImportBeanDefinitionRegistrar=>ApplicationContextAware
     * 读取环境配置=》注册Bean=》生成上下文
     */
    static class DynamicMyBatisConfigurationRegistrar implements EnvironmentAware,ImportBeanDefinitionRegistrar {

        /**
         * 多数据源属性配置 <dataSource,props>
         * props是个性化配置
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
                        genericMyBatisConfigurationBeanDefinition();

                registry.registerBeanDefinition(dataSourceCamelName + Const.BEAN_SUFFIX.MyBatisConfiguration.val(), beanDefinition);

            });
        }


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


    /**
     * Bean 处理器，将各数据源的自定义配置绑定到 Bean
     *
     */
    static class MyBatisConfigurationBeanPostProcessor implements EnvironmentAware, BeanPostProcessor {

        private final List<BeanCustomizer<Configuration>> customizers;
        private Environment environment;
        private Map<String, Object> dataSources;

        public MyBatisConfigurationBeanPostProcessor(ObjectProvider<List<BeanCustomizer<Configuration>>> customizers) {
            this.customizers = customizers.getIfAvailable(ArrayList::new);
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
            this.dataSources = Binder.get(environment)
                    .bind(Const.PROP_PREFIX.Druid.val(), Bindable.mapOf(String.class, Object.class))
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
            if (bean instanceof Configuration) {
                // 设置 Druid 名称
                Configuration configuration = (Configuration) bean;
                // 将 'spring.maxplus1.mybatis.data-sources.${name}' 的配置绑定到 Bean
                if (!dataSources.isEmpty()) {
                    Binder.get(environment).bind(Const.PROP_PREFIX.MyBatis.val() + "." + beanName, Bindable.ofInstance(configuration));
                }
                // 定制化配置，拥有最高优先级，会覆盖之前已有的配置
                customizers.forEach(customizer -> customizer.customize(configuration));
            }
            return bean;
        }

    }





    static class MyBatisConfigurationImportSelector implements ImportSelector, EnvironmentAware {


        @Override
        public void setEnvironment(Environment environment) {

        }

        @Override
        public String[] selectImports(AnnotationMetadata metadata) {
            Stream.Builder<Class<?>> imposts = Stream.<Class<?>>builder().add(MyBatisConfigurationConfiguration.MyBatisConfigurationBeanPostProcessor.class);
            imposts.add(DynamicMyBatisConfigurationRegistrar.class);
            return imposts.build().map(Class::getName).toArray(String[]::new);
        }


    }


}
