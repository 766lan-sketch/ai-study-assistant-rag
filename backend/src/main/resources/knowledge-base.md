# Controller：接收请求的入口
关键词：controller,控制器,请求,http,接口,参数
Controller 是 Java Web 后端接收 HTTP 请求的入口。它负责读取请求参数、进行基础校验，然后调用 Service 处理业务，不应该堆放大量业务逻辑。

---
# Service：处理业务逻辑
关键词：service,业务,业务逻辑,分层,调用
Service 负责组织业务规则和处理流程。Controller 接到请求后调用 Service，Service 再调用 Repository 操作数据，这样代码更容易维护和测试。

---
# Repository：操作数据库
关键词：repository,数据库,jpa,查询,保存,持久化
Repository 是数据访问层，负责查询、保存、修改和删除数据库记录。Spring Data JPA 可以根据接口方法名自动生成常用查询。

---
# Spring Boot：快速创建 Java Web 项目
关键词：spring boot,springboot,自动配置,java web,服务器
Spring Boot 用约定和自动配置简化 Java Web 项目搭建。引入 Web 依赖后，可以直接启动内置服务器，并通过注解创建 REST 接口。

---
# Java 类与对象
关键词：类,对象,class,new,实例,属性,方法
类可以理解为设计图，规定对象拥有哪些属性和方法；对象是根据类创建出来的具体实例。Java 使用 new 关键字创建对象，同一个类可以创建多个对象。

---
# RESTful API 与常用请求方法
关键词：rest,restful,get,post,put,delete,crud,接口
RESTful API 使用 HTTP 方法表达操作：GET 查询数据，POST 新增数据，PUT 修改数据，DELETE 删除数据。前端通过这些接口与 Java 后端交换 JSON 数据。

---
# Maven：Java 项目构建工具
关键词：maven,pom,依赖,构建,打包
Maven 根据 pom.xml 管理项目依赖和构建流程。常用命令包括 mvn test 运行测试、mvn package 打包项目和 mvn spring-boot:run 启动 Spring Boot。
