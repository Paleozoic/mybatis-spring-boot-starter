package com.maxplus1.db.starter.config;


/**
 * Bean 的回调接口，可以在 Bean 初始化之前对其进行定制
 *
 */
public interface BeanCustomizer<T> {

    /**
     * 定制化
     *
     * @param
     */
    void customize(T bean);

}