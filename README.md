# 写在前面

- pagination &  druid & mybatis for multi datasources(event different dialects),all in one strarter~
- 此工具通过一个starter直接开启多数据源（支持不同方言）的druid+mybatis+分页的工程，非常方便于复用~
- 写了大量的BeanDefinition，可能有缺少的属性。
>   如果@Bean可以一次性返回多个Bean并且注册到Spring容器，
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

- [Spring Boot Issue](https://github.com/spring-projects/spring-boot/issues/14978)
- [Spring Issue](https://jira.spring.io/browse/SPR-17441)

# 注意
- 所有的bean，比如`PageInterceptor`  、`DruidDataSource` 、 `SqlSessionFactory` 、 `TransactionManager`等，所有注入的BeanName都是：数据源+类名。例如：`xxxTransactionManager`，`xxx`表示数据源的名字
- 但是`xxxTransactionManager`起了别名`xxx`，方便处理事务。用于`@Transactional('xxx')`

#  PS
- 基本遵循mybatis-spring-boot-starter和druid-spring-boot-starter的配置格式
- 如果存在不支持属性，欢迎提issue
