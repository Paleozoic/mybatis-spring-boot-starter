package com.maxplus1.db.starter.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

import java.util.HashMap;
import java.util.Map;

public class BeanUtils {

    /**
     * 构造 BeanDefinition，通过 clzz 实现继承 'spring.maxplus1.xxx' 的配置
     *
     * @return BeanDefinition
     */
    public static BeanDefinition genericBeanDefinition(Class clzz) {
        return BeanDefinitionBuilder.genericBeanDefinition(clzz)
                .setInitMethodName("init")
                .setDestroyMethodName("close")
                .getBeanDefinition();
    }

    public static BeanDefinition genericBeanDefinition(Class clzz,Map<String,String> propRefMap) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clzz);

        propRefMap.forEach((name,beanName)->{
            beanDefinitionBuilder.addPropertyReference(name,beanName);
        });

        return   beanDefinitionBuilder.setInitMethodName("init")
                .setDestroyMethodName("close")
                .getBeanDefinition();
    }

    public static BeanDefinition genericBeanDefinition(Class clzz,String name,String beanName) {
        Map<String,String> propRefMap = new HashMap<>();
        propRefMap.put(name,beanName);
        return BeanUtils.genericBeanDefinition(clzz,propRefMap);
    }



    public static BeanDefinition genericBeanDefinitionPropVal(Class clzz,Map<String,Object> propRefMap) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clzz);

        propRefMap.forEach((name,beanName)->{
            beanDefinitionBuilder.addPropertyValue(name,beanName);
        });

        return   beanDefinitionBuilder.setInitMethodName("init")
                .setDestroyMethodName("close")
                .getBeanDefinition();
    }
}
