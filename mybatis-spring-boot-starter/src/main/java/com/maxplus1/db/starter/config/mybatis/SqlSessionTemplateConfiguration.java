package com.maxplus1.db.starter.config.mybatis;

import com.maxplus1.db.starter.config.Const;
import com.maxplus1.db.starter.config.utils.CharMatcher;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
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

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;

@Import(SqlSessionTemplateConfiguration.SqlSessionTemplateImportSelector.class)
@Slf4j
public class SqlSessionTemplateConfiguration {



    /**
     * 多事务管理器注册
     */
    static class DynamicSqlSessionTemplateRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

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
            final AtomicBoolean primary= new AtomicBoolean(true);
            this.dataSources.keySet().forEach(dataSourceName -> {
                // 注册 BeanDefinition
                String camelName = CharMatcher.separatedToCamel().apply(dataSourceName);
                // 添加数据源
                BeanDefinition beanDefinition =
                        genericSqlSessionTemplateBeanDefinition(camelName+Const.BEAN_SUFFIX.SqlSessionFactory.val(),primary.getAndSet(false));
                registry.registerBeanDefinition(camelName+ Const.BEAN_SUFFIX.SqlSessionTemplate.val(), beanDefinition);

            });
        }


    }


    /**
     * 构造 BeanDefinition
     * 构造器方法注入
     *
     * @return BeanDefinition
     */
    private static BeanDefinition genericSqlSessionTemplateBeanDefinition(String sqlSessionFactoryBeanName,boolean primary) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionTemplate.class);
        beanDefinitionBuilder.addConstructorArgReference(sqlSessionFactoryBeanName);
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        beanDefinition.setPrimary(primary);
        return  beanDefinition;
    }



    /**
     * Bean 处理器，将各数据源的自定义配置绑定到 Bean
     *
     */
    static class SqlSessionTemplateBeanPostProcessor implements EnvironmentAware, BeanPostProcessor {




        @Override
        public void setEnvironment(Environment environment) {

        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof SqlSessionTemplate) {
                log.info("[INFO===>>>]inject the bean SqlSessionTemplate[{}]",beanName);
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

    }




    static class SqlSessionTemplateImportSelector implements ImportSelector, EnvironmentAware {


        @Override
        public void setEnvironment(Environment environment) {

        }

        @Override
        public String[] selectImports(AnnotationMetadata metadata) {
            Stream.Builder<Class<?>> imposts = Stream.<Class<?>>builder().add(SqlSessionTemplateConfiguration.SqlSessionTemplateBeanPostProcessor.class);
            imposts.add(SqlSessionTemplateConfiguration.DynamicSqlSessionTemplateRegistrar.class);
            return imposts.build().map(Class::getName).toArray(String[]::new);
        }

    }


}
