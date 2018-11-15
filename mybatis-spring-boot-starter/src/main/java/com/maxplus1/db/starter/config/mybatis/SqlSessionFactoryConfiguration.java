package com.maxplus1.db.starter.config.mybatis;

import com.maxplus1.db.starter.config.BeanCustomizer;
import com.maxplus1.db.starter.config.Const;
import com.maxplus1.db.starter.config.utils.CharMatcher;
import com.maxplus1.db.starter.config.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
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
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;

@Import(SqlSessionFactoryConfiguration.SqlSessionFactoryImportSelector.class)
@Slf4j
public class SqlSessionFactoryConfiguration {


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
            final AtomicBoolean primary= new AtomicBoolean(true);
            this.dataSources.keySet().forEach(dataSourceName -> {
                // 注册 BeanDefinition
                String camelName = CharMatcher.separatedToCamel().apply(dataSourceName);

                // 声明SqlSessionFactoryBean
                BeanDefinition factoryBeanBeanDefinition =
                        genericSqlSessionFactoryBeanBeanDefinition(camelName,primary.getAndSet(false));
                registry.registerBeanDefinition(camelName+ Const.BEAN_SUFFIX.SqlSessionFactoryBean.val(),factoryBeanBeanDefinition);
                // 声明SqlSessionFactory，通过SqlSessionFactoryBean的实例工厂创建
                BeanDefinition beanDefinition =
                        genericSqlSessionFactoryBeanDefinition(camelName);
                registry.registerBeanDefinition(camelName+ Const.BEAN_SUFFIX.SqlSessionFactory.val(), beanDefinition);

            });
        }


    }

    /**
     * SqlSessionFactoryBean  beanFactory.getBean("工厂方法BeanId")会得到工厂生成的真正Bean，而不是工厂本身
     * 如果需要获取工厂本身，则需要使用：FACTORY_BEAN_PREFIX = "&"
     * factory.getBean("&工厂方法BeanId")会得到工厂Bean
     * 在org.springframework.beans.factory.support.ConstructorResolver#instantiateUsingFactoryMethod  工厂方法注入
     * 获取的是产品Bean，而不是工厂Bean本身，注意！
     * String factoryBeanName = mbd.getFactoryBeanName();
         if (factoryBeanName != null) {
         if (factoryBeanName.equals(beanName)) {
         throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName,
         "factory-bean reference points back to the same bean definition");
         }
         factoryBean = this.beanFactory.getBean(factoryBeanName);
         if (mbd.isSingleton() && this.beanFactory.containsSingleton(beanName)) {
         throw new ImplicitlyAppearedSingletonException();
         }
         factoryClass = factoryBean.getClass();
         isStatic = false;
         }
     *
     * @param camelName
     * @return
     */
    private static BeanDefinition genericSqlSessionFactoryBeanBeanDefinition(String camelName,boolean primary ) {
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionFactoryBeanWrapper.class)
                .addPropertyReference("dataSource", camelName + Const.BEAN_SUFFIX.DataSource.val())
                .addPropertyReference("configuration", camelName + Const.BEAN_SUFFIX.MyBatisConfiguration.val())
                //MyBatis拦截器
                .addPropertyReference("plugins",camelName + Const.BEAN_SUFFIX.PageInterceptor.val())
                .getBeanDefinition();
        beanDefinition.setPrimary(primary);
        return beanDefinition;
    }


    /**
     * 构造 BeanDefinition，通过工厂实例生成Bean  SqlSessionFactory
     *
     * @return BeanDefinition
     */
    private static BeanDefinition genericSqlSessionFactoryBeanDefinition(String camelName ) {
        return BeanDefinitionBuilder.genericBeanDefinition(DefaultSqlSessionFactory.class)
                .setFactoryMethodOnBean("getObject","&"+camelName+Const.BEAN_SUFFIX.SqlSessionFactoryBean.val())
                .getBeanDefinition();
    }





    /**
     * Bean 处理器，将各数据源的自定义配置绑定到 Bean
     *
     */
    static class SqlSessionFactoryBeanPostProcessor implements EnvironmentAware, BeanPostProcessor, PriorityOrdered {

        private  List<BeanCustomizer<SqlSessionFactoryBean>> customizers;
        private Environment environment;
        private Map<String, Object> dataSources;

        public SqlSessionFactoryBeanPostProcessor() {
            this.customizers = new ArrayList<>();
        }

        public SqlSessionFactoryBeanPostProcessor(ObjectProvider<List<BeanCustomizer<SqlSessionFactoryBean>>> customizers) {
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
            if (bean instanceof SqlSessionFactoryBeanWrapper) {
                // 获取SqlSessionFactoryBean
                SqlSessionFactoryBeanWrapper sqlSessionFactoryBean = (SqlSessionFactoryBeanWrapper) bean;
                // 将 'spring.maxplus1.druid.data-sources.${name}' 的配置绑定到 Bean
                if (!dataSources.isEmpty()) {
                    Binder.get(environment).bind(Const.PROP_PREFIX.MyBatis.val() + "." + StringUtils.getFirstCamelName(beanName), Bindable.ofInstance(sqlSessionFactoryBean));
                }
                // 定制化配置，拥有最高优先级，会覆盖之前已有的配置
                customizers.forEach(customizer -> customizer.customize(sqlSessionFactoryBean));
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }


        @Override
        public int getOrder() {
            return 1;
        }
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
