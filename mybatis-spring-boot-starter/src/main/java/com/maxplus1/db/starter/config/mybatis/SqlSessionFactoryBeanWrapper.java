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

    private String[] mapperLocations;

    @Override
    public void afterPropertiesSet() throws Exception {
//        super.afterPropertiesSet(); // 和下面getObject()重复执行了
        super.setMapperLocations(resolveMapperLocations());
    }

    @Override
    public SqlSessionFactory getObject() throws Exception {
        /**
         * super.afterPropertiesSet(); 必须加 否则不生成MapperStatements  坑啊！！！
         */
        super.afterPropertiesSet();
        return super.getObject();
    }

    public Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = new ArrayList<Resource>();
        if (this.mapperLocations != null) {
            for (String mapperLocation : this.mapperLocations) {
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
