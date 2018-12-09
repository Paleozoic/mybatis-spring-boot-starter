package com.maxplus1.db.starter.config.mybatis;

import com.maxplus1.db.starter.config.Const;
import com.maxplus1.db.starter.config.utils.CharMatcher;
import com.maxplus1.db.starter.config.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
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
public class MapperScannerConfigurerConfiguration implements EnvironmentAware, ImportBeanDefinitionRegistrar, BeanPostProcessor {

    private Environment environment;
    /**
     * 多数据源属性配置
     */
    private Map<String, Object> MyBatis;
    private Map<String, Object> MyBatisCommon;
    private Map<String, Object> Druid;


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        this.MyBatis = Binder.get(environment)
                .bind(Const.PROP_PREFIX.MyBatis.val(), Bindable.mapOf(String.class, Object.class))
                .orElse(emptyMap());
        this.MyBatisCommon = Binder.get(environment)
                .bind(Const.PROP_PREFIX.MyBatisCommon.val(), Bindable.mapOf(String.class, Object.class))
                .orElse(emptyMap());
        this.Druid = Binder.get(environment)
                .bind(Const.PROP_PREFIX.Druid.val(), Bindable.mapOf(String.class, Object.class))
                .orElse(emptyMap());
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        this.MyBatis.keySet().forEach(dataSourceName -> {
            // 注册 BeanDefinition
            String camelName = CharMatcher.separatedToCamel().apply(dataSourceName);
            // 个性化配置
            String basePackage = ((Map) MyBatis.get(camelName)).get("base-package") + "";
            // 公共配置
            if (basePackage == null || basePackage.length() == 0) {
                basePackage = MyBatisCommon.get("base-package") + "";
            }
            BeanDefinition beanDefinition =
                    genericMapperScannerConfigurerBeanDefinition(camelName, basePackage);
            registry.registerBeanDefinition(camelName + Const.BEAN_SUFFIX.MapperScannerConfigurer.val(), beanDefinition);

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
        if (bean instanceof MapperScannerConfigurer) {
            // 设置 Druid 名称
            MapperScannerConfigurer mapperScannerConfigurer = (MapperScannerConfigurer) bean;
            // 将 'spring.maxplus1.mybatis.data-sources.${name}' 的配置绑定到 Bean
            if (!Druid.isEmpty()) {
                Binder.get(environment).bind(Const.PROP_PREFIX.MyBatis.val() + "." + StringUtils.getFirstCamelName(beanName), Bindable.ofInstance(mapperScannerConfigurer));
            }
        }
        return bean;
    }


    /**
     * 构造 BeanDefinition
     *
     * @return BeanDefinition
     */
    private static BeanDefinition genericMapperScannerConfigurerBeanDefinition(String dataSource, String basePackage) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurerWrapper.class);
        beanDefinitionBuilder.addPropertyValue("basePackage", basePackage);
        beanDefinitionBuilder.addPropertyValue("sqlSessionFactoryBeanName", dataSource + Const.BEAN_SUFFIX.SqlSessionFactory.val());
        /**
         * sqlSessionTemplateBeanName 和 sqlSessionFactoryBeanName只需要1个，sqlSessionTemplateBeanName优先级更高
         */
        return beanDefinitionBuilder.getBeanDefinition();
    }


}
