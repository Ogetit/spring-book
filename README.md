# spring-book

Spring相关技术框架整合和研究，一系列项目模板，用于后续快速开发和速查～:-D

## SpringFramework

- Spring 核心容器：核心容器提供 Spring 框架的基本功能，管理着Spring应用中bean的创建、配置和管理。核心容器的主要组件是 BeanFactory，它是工厂模式的实现。BeanFactory 使用DI将应用程序的配置和依赖性规范与实际的应用程序代码分开。
- Spring 上下文：Spring 上下文是一个配置文件，向 Spring 框架提供上下文信息。提供了一种框架式的对象访问方法，有些象JNDI注册器。Context封装包的特性得自于Beans封装包，并添加了对国际化（I18N）的支持（例如资源绑定），事件传播，资源装载的方式和Context的透明创建，比如说通过Servlet容器。Spring 上下文和Bean工厂都是 bean 容器 的实现。
- Spring AOP：通过配置管理特性，Spring AOP 模块直接将面向方面的编程功能集成到了 Spring 框架中。所以，可以很容易地使 Spring 框架管理的任何对象支持 AOP。Spring AOP 模块为基于 Spring 的应用程序中的对象提供了事务管理服务。
- Spring DAO：JDBC DAO 抽象层提供了有意义的异常层次结构，可用该结构来管理异常处理和不同数据库供应商抛出的错误消息。异常层次结构简化了错误处理，并且极大地降低了需要编写的异常代码数量（例如打开和关闭连接）。Spring DAO 的面向 JDBC 的异常遵从通用的 DAO 异常层次结构。
- Spring ORM：Spring 框架插入了若干个 ORM 框架，从而提供了 ORM 的对象关系工具，其中包括 JDO、Hibernate 和 iBatis SQL Map。所有这些都遵从 Spring 的通用事务和 DAO 异常层次结构。
- Spring Web 模块：Web 上下文模块建立在应用程序上下文模块之上，为基于 Web 的应用程序提供了上下文。
- Spring MVC 框架：MVC 框架是一个全功能的构建 Web 应用程序的 MVC 实现。通过策略接口，MVC 框架变成为高度可配置的，MVC 容纳了大量视图技术，其中包括 JSP、Velocity、Tiles、iText 和 POI。

## SpringWebFlow

Spring Web Flow是Spring MVC的一个扩展， 它为基于流程的会话式Web应用（购物车或者向导功能）提供了支持。简言之，它是一个流程框架，能够引导用户执行一系列向导步骤。

在Spring Web Flow中，流程是由三个主要元素定义的：状态、转移和流程数据。

## SpringSecurity

Spring Security就是通过AOP和Filter来为应用程序实现安全性的。

使用Servlet规范中的Filter保护Web请求并限制URL级别的访问。Spring Security还能够使用Spring AOP保护方法调用——借助于对象代理和使用通知，能够确保只有具备适当权限的用户才能访问安全保护的方法。

Spring Security非常灵活，能够基于各种数据存储来认证用户。它内置了多种常见的用户存储场景，如内存、关系型数据库以及LDAP。但我们也可以编写并插入自定义的用户存储实现。

## SpringData

- Spring Data Commons
- Spring Data JPA
- Spring Data KeyValue
- Spring Data LDAP
- Spring Data MongoDB
- Spring Data Gemfire
- Spring Data REST
- Spring Data Redis
- Spring Data for Apache Cassandra
- Spring Data for Apache Solr
- Spring Data Couchbase (community module)
- Spring Data Elasticsearch (community module)
- Spring Data Neo4j (community module)

## SpringBoot

- Spring Boot Starter：它将常用的依赖分组进行了整合，将其合并到一个依赖中，这样就可以一次性添加到项目的Maven或Gradle构建中，这里可以找到目前所有的starter项目。
- 自动配置：Spring Boot的自动配置特性利用了Spring 4对条件化配置的支持，合理地推测应用所需的bean并自动化配置它们，减少了你自己需要配置的数量。
- 命令行接口（Command-line interface，CLI）：Spring Boot的CLI发挥了Groovy编程语言的优势，并结合自动配置进一步简化Spring应用的开发。
- Actuator：它为Spring Boot应用添加了一定的管理特性。

## SpringCloud

- 分布式/版本化配置：Spring Cloud Config
- 服务注册和发现：Netflix Eureka 或者 Spring Cloud Eureka（对前者的二次封装）
- 路由：Spring Cloud Zuul 基于 Netflix Zuul
- service - to - service调用：Spring Cloud Feign
- 负载均衡：Spring Cloud Ribbon 基于 Netflix Ribbon 实现
- 断路器：Spring Cloud Hystrix
- 分布式消息传递：Spring Cloud Bus
