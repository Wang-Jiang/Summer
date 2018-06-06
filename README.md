# Summer

[![JitPack](https://jitpack.io/v/Wang-Jiang/Summer.svg)](https://jitpack.io/#Wang-Jiang/Summer)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](http://www.opensource.org/licenses/mit-license.php)

## 简介
一个轻量级的JavaWeb框架，它简单易用、功能丰富，能让你专注于业务，用于快速构建网站。Summer基于约定优于配置(Convention Over Configuration)的理念，有着极低的学习成本

## 开发环境
需要Java版本 >= 8

## 安装
添加Maven依赖

Step 1. Add the JitPack repository to your build file
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Step 2. Add the dependency
```xml
<dependency>
    <groupId>com.github.Wang-Jiang</groupId>
    <artifactId>Summer</artifactId>
    <version>最新的Release版本</version>
</dependency>
```

## 功能列表
* 无需配置文件
* 约定优于配置的路由设计，轻松实现参数路由(类似于/user/{userId}/blog/{blogId})
* 简单易用的Model，轻松操作数据库，支持多种数据库，支持非Web环境使用
* 根据数据库结构自动生成Model代码
* 方便实用的AOP
* 功能丰富的表单验证
* 内置了轻量级控制台输出库EasyLogger，使用参见[EasyLogger文档](https://github.com/Wang-Jiang/EasyLogger)

## 使用
使用文档参见[DOC.md](DOC.md)

## TodoList
- [] 对SqlServer、Oracle、PostgreSQL等主流数据库的支持
- [] 对FreeMarker等模板引擎的支持
- [] 完善ModelGenerator对Sqlite数据库的支持
- [] 增加数据库迁移功能(migration)，由Model直接生成数据库表结构

## 更新日志
参见[CHANGELOG.md](CHANGELOG.md)

## 感谢
JFinal、Spring和Django给了我很大的启发，Summer的设计很多地方借鉴了这些优秀的框架