package com.maxplus1.db.starter.config.mybatis;

import com.maxplus1.db.starter.config.BeanCustomizer;
import com.maxplus1.db.starter.config.Const;
import com.maxplus1.db.starter.config.utils.CharMatcher;
import com.maxplus1.db.starter.config.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
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

@Import(MapperScannerConfigurerConfiguration.MapperScannerConfigurerImportSelector.class)
@Slf4j
public class MapperScannerConfigurerConfiguration {



    /**
     * 多MapperScannerConfigurer注册
     */
    static class DynamicMapperScannerConfigurerRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

        /**
         * 多数据源属性配置
         */
        private Map<String, Object> dataSources;
        private Map<String, Object> commonMap;

        @Override
        public void setEnvironment(Environment environment) {
            this.dataSources = Binder.get(environment)
                    .bind(Const.PROP_PREFIX.MyBatis.val(), Bindable.mapOf(String.class, Object.class))
                    .orElse(emptyMap());
            this.commonMap = Binder.get(environment)
                    .bind(Const.PROP_PREFIX.MyBatisCommon.val(), Bindable.mapOf(String.class, Object.class))
                    .orElse(emptyMap());
        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
            this.dataSources.keySet().forEach(dataSourceName -> {
                // 注册 BeanDefinition
                String camelName = CharMatcher.separatedToCamel().apply(dataSourceName);
                // 个性化配置
                String basePackage = ((Map)dataSources.get(camelName)).get("base-package")+"";
                // 公共配置
                if(basePackage==null||basePackage.length()==0){
                     basePackage = commonMap.get("base-package")+"";
                }
                BeanDefinition beanDefinition =
                        genericMapperScannerConfigurerBeanDefinition(camelName,basePackage);
                registry.registerBeanDefinition(camelName+ Const.BEAN_SUFFIX.MapperScannerConfigurer.val(), beanDefinition);

            });
        }


    }


    /**
     * 构造 BeanDefinition
     *
     * @return BeanDefinition
     */
    private static BeanDefinition genericMapperScannerConfigurerBeanDefinition(String dataSource,String basePackage) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurerWrapper.class);
        beanDefinitionBuilder.addPropertyValue("basePackage",basePackage);
        beanDefinitionBuilder.addPropertyValue("sqlSessionFactoryBeanName",dataSource+Const.BEAN_SUFFIX.SqlSessionFactory.val());
        /**
         * sqlSessionTemplateBeanName 和 sqlSessionFactoryBeanName只需要1个，sqlSessionTemplateBeanName优先级更高
         */
//        beanDefinitionBuilder.addPropertyValue("sqlSessionTemplateBeanName",dataSource+Const.BEAN_SUFFIX.SqlSessionTemplate.val());
        return   beanDefinitionBuilder
                .getBeanDefinition();
    }



    /**
     * Bean 处理器，将各数据源的自定义配置绑定到 Bean
     *
     */
    static class MapperScannerConfigurerBeanPostProcessor implements EnvironmentAware, BeanPostProcessor {


        private final List<BeanCustomizer<MapperScannerConfigurer>> customizers;
        private Environment environment;
        private Map<String, Object> dataSources;

        public MapperScannerConfigurerBeanPostProcessor(ObjectProvider<List<BeanCustomizer<MapperScannerConfigurer>>> customizers) {
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
            if (bean instanceof MapperScannerConfigurer) {
                // 设置 Druid 名称
                MapperScannerConfigurer mapperScannerConfigurer = (MapperScannerConfigurer) bean;
                // 将 'spring.maxplus1.mybatis.data-sources.${name}' 的配置绑定到 Bean
                if (!dataSources.isEmpty()) {
                    Binder.get(environment).bind(Const.PROP_PREFIX.MyBatis.val() + "." + StringUtils.getFirstCamelName(beanName), Bindable.ofInstance(mapperScannerConfigurer));
                }
                // 定制化配置，拥有最高优先级，会覆盖之前已有的配置
                customizers.forEach(customizer -> customizer.customize(mapperScannerConfigurer));
            }
            return bean;
        }

    }




    static class MapperScannerConfigurerImportSelector implements ImportSelector, EnvironmentAware {


        @Override
        public void setEnvironment(Environment environment) {

        }

        @Override
        public String[] selectImports(AnnotationMetadata metadata) {
            Stream.Builder<Class<?>> imposts = Stream.<Class<?>>builder().add(MapperScannerConfigurerConfiguration.MapperScannerConfigurerBeanPostProcessor.class);
            imposts.add(MapperScannerConfigurerConfiguration.DynamicMapperScannerConfigurerRegistrar.class);
            return imposts.build().map(Class::getName).toArray(String[]::new);
        }

    }


}
