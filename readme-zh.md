# 米波英雄模板 (pro.fessional.meepo)

![Maven Central](https://img.shields.io/maven-central/v/pro.fessional/meepo?color=00DD00)
![Sonatype Snapshots](https://img.shields.io/nexus/s/pro.fessional/meepo?server=https%3A%2F%2Foss.sonatype.org)
[![Coverage Status](https://coveralls.io/repos/github/trydofor/professional-meepo/badge.svg)](https://coveralls.io/github/trydofor/professional-meepo)

> 中文 🇨🇳 | [English 🇺🇸](readme.md)

米波，地卜师，主身和分身具有同等的技能，一荣俱荣，一损俱损的待遇。
一个基于`母版`语法注释和标记的不破坏`母版`语法的非专业模板引擎。

![meepo](meepo_full.png)

现代模板引擎自身的语法，会破坏目标文件的语法，会干扰目标文件的预览和编辑。
米波解决以上问题，仅做静态翻译和有限的动态控制，类似C的宏功能，性能高效。

* 从`java`生成`*.java`，模板和目标文件都是可编译
* 从`sql`生成`*.sql`，模板和目标文件都可以执行
* 从`htm`生成`*.htm`，模板和目标文件都可以预览
* 占位符模板，支持自定义变量边界和变量转义(spring痛点)
* java体系内，灵活高效的自定义函数（动态，静态，运行时）

## 如何使用

① 自己`clone`和`install`最豪横。

② 使用 maven central 比较稳妥。

```xml
<dependency>
    <groupId>pro.fessional</groupId>
    <artifactId>meepo</artifactId>
    <version>${meepo.version}</version>
</dependency>
```

③ 使用 SNAPSHOT 与时俱进。

```xml
<repository>
    <id>oss-sonatype</id>
    <name>oss-sonatype</name>
    <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>
```

## 独立使用

`Meepo`封装了常用方法和缓存机制。能够满足一般的场景需求。

* 文件模板 - 以`Meepo`为入口, `#parse`, `#merge`
* 占位模板 - 以`Holder`为入口，`#piece`
* 解析字串 - 以`Parser`构造`Gene`, `#parse`

如果有定制需要，可以自定义使用`Parser`和`Gene`来组合出需要的工具类。

## 集成Mvc

`spring-mvc`子工程，集成`SpringMvc`，可独立使用，也可预处理其他模板

```xml
<dependency>
    <groupId>pro.fessional.meepo</groupId>
    <artifactId>spring-mvc</artifactId>
    <version>${meepo-spring.version}</version>
</dependency>
```

## 集成Spel

`spring-spel`子工程，注册`spel`引擎，提供`Bean`和`SpEL`能力

```xml
<dependency>
    <groupId>pro.fessional.meepo</groupId>
    <artifactId>spring-spel</artifactId>
    <version>${meepo-spring.version}</version>
</dependency>
```

## 详细文档

* <https://wings.fessional.pro/zh/b-meepo/>
* <https://github.com/fessionalpro/wings-doc/tree/main/src/zh/b-meepo/>
* <https://gitee.com/fessionalpro/wings-doc/tree/main/src/zh/b-meepo/>
