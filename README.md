# 写在前面

需了大量的BeanDefinition，可能有缺少的属性。

如果@Bean可以一次性返回多个Bean并且注册到Spring容器，
则可以作出大量简化，并复用大量的starter。
比如：
```java
@Beans
public Map<String,Bean> manyBeans(){
    Map<String,Bean> beans = new HashMap<>();
    // 分别设置BeanName和Bean
    return beans;
}
```

详情看我提的 :
[Spring Boot Issue](https://github.com/spring-projects/spring-boot/issues/14978)
[Spring Issue](https://jira.spring.io/browse/SPR-17441)


#  PS
- 基本遵循mybatis-spring-boot-starter和druid-spring-boot-starter的配置格式
- 如果存在不支持属性，欢迎提issue
