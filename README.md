# 写在前面
此项目暂时放弃
多个数据源的MyBatis注入需要写大量的BeanDefinition，
实在太累。
暂且搁置。

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

详情看我提的 [Issue](https://github.com/spring-projects/spring-boot/issues/14978)
https://jira.spring.io/browse/SPR-17441
# PS
多数据源的注入已经实现