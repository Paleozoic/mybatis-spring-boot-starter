package com.maxplus1.db.starter.config.mybatis;

import lombok.Data;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class SqlSessionFactoryBeanWrapper extends SqlSessionFactoryBean {

    private String[] mapperLocationsStr;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        super.setMapperLocations(resolveMapperLocations());
    }

    @Override
    public SqlSessionFactory getObject() throws Exception {
        return super.getObject();
    }

    public Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = new ArrayList<Resource>();
        if (this.mapperLocationsStr != null) {
            for (String mapperLocation : this.mapperLocationsStr) {
                try {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation);
                    resources.addAll(Arrays.asList(mappers));
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }
}
