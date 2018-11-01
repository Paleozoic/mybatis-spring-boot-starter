package com.maxplus1.db.starter.config.pagehelper;

import com.github.pagehelper.PageInterceptor;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@Data
@ConfigurationProperties("spring.maxplus1.pagehelper")
public class PageInterceptorWrapper extends PageInterceptor  implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setProperties(this.properties);
    }


    /**
     * 以下代码来源于 PageHelperProperties
     */

    private Properties properties = new Properties();


    public String getOffsetAsPageNum() {
        return properties.getProperty("offsetAsPageNum");
    }

    public void setOffsetAsPageNum(String offsetAsPageNum) {
        properties.setProperty("offsetAsPageNum", offsetAsPageNum);
    }

    public String getRowBoundsWithCount() {
        return properties.getProperty("rowBoundsWithCount");
    }

    public void setRowBoundsWithCount(String rowBoundsWithCount) {
        properties.setProperty("rowBoundsWithCount", rowBoundsWithCount);
    }

    public String getPageSizeZero() {
        return properties.getProperty("pageSizeZero");
    }

    public void setPageSizeZero(String pageSizeZero) {
        properties.setProperty("pageSizeZero", pageSizeZero);
    }

    public String getReasonable() {
        return properties.getProperty("reasonable");
    }

    public void setReasonable(String reasonable) {
        properties.setProperty("reasonable", reasonable);
    }

    public String getSupportMethodsArguments() {
        return properties.getProperty("supportMethodsArguments");
    }

    public void setSupportMethodsArguments(String supportMethodsArguments) {
        properties.setProperty("supportMethodsArguments", supportMethodsArguments);
    }

    public String getDialect() {
        return properties.getProperty("dialect");
    }

    public void setDialect(String dialect) {
        properties.setProperty("dialect", dialect);
    }

    public String getHelperDialect() {
        return properties.getProperty("helperDialect");
    }

    public void setHelperDialect(String helperDialect) {
        properties.setProperty("helperDialect", helperDialect);
    }

    public String getAutoRuntimeDialect() {
        return properties.getProperty("autoRuntimeDialect");
    }

    public void setAutoRuntimeDialect(String autoRuntimeDialect) {
        properties.setProperty("autoRuntimeDialect", autoRuntimeDialect);
    }

    public String getAutoDialect() {
        return properties.getProperty("autoDialect");
    }

    public void setAutoDialect(String autoDialect) {
        properties.setProperty("autoDialect", autoDialect);
    }

    public String getCloseConn() {
        return properties.getProperty("closeConn");
    }

    public void setCloseConn(String closeConn) {
        properties.setProperty("closeConn", closeConn);
    }

    public String getParams() {
        return properties.getProperty("params");
    }

    public void setParams(String params) {
        properties.setProperty("params", params);
    }

    public String getDefaultCount() {
        return properties.getProperty("defaultCount");
    }

    public void setDefaultCount(String defaultCount) {
        properties.setProperty("defaultCount", defaultCount);
    }

    public String getDialectAlias() {
        return properties.getProperty("dialectAlias");
    }

    public void setDialectAlias(String dialectAlias) {
        properties.setProperty("dialectAlias", dialectAlias);
    }
}
