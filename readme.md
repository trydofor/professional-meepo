# Meepo Hero Template (pro.fessional.meepo)

![Maven Central](https://img.shields.io/maven-central/v/pro.fessional/meepo?color=00DD00)
![Sonatype Snapshots](https://img.shields.io/nexus/s/pro.fessional/meepo?server=https%3A%2F%2Foss.sonatype.org)
[![Coverage Status](https://coveralls.io/repos/github/trydofor/professional-meepo/badge.svg)](https://coveralls.io/github/trydofor/professional-meepo)

> English 🇺🇸 | [中文 🇨🇳](readme-zh.md)

Meepo, Five are stronger than one. Divided they stand, united they fall.

a non-professional template engine that does not break the `master` syntax,
and is based on the comments and markup of the `master` syntax.

![meepo](meepo_full.png)

The syntax of most modern template engines will break the syntax 
of the target file when previewing and editing.

Meepo solves this problem with high performance like C's macro by 
using only static transformations and limited dynamic control.

* Generate `*.java` from `java`, both are compilable.
* Generate `*.sql` from `sql`, both are executable.
* Generate `*.htm` from `htm`, both are previewable.
* `placeholder` supports customizing variable's boundaries and escaping
* flexible and efficient customizing (dynamic, static, runtime) function using java

## How to use

① DIY `clone` and `install` is powerful.

② Using Maven Central is stable.

```xml
<dependency>
    <groupId>pro.fessional</groupId>
    <artifactId>meepo</artifactId>
    <version>${meepo.version}</version>
</dependency>
```

③ Using SNAPSHOT is the latest.

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

## Standalone use

`Meepo` provides common methods and caching 
that can handle the most common scenarios.

* File template - use `Meepo` as entry, `#parse`, `#merge`
* Placeholder template - use `Holder` as entry, `#piece`
* String Parsing - use `Parser` to construct `Gene`, `#parse`

you can customize the use of `Parser` and `Gene` to combine the tool as you need.

## Mvc Integration

`spring-mvc` module integrates with `SpringMvc`, 
can be used independently or pre-processed with other templates.

```xml
<dependency>
    <groupId>pro.fessional.meepo</groupId>
    <artifactId>spring-mvc</artifactId>
    <version>${meepo-spring.version}</version>
</dependency>
```

## Spel Integration

`spring-spel` module registers the `spel` engine, provide spring `Bean` and `SpEL`.

```xml
<dependency>
    <groupId>pro.fessional.meepo</groupId>
    <artifactId>spring-spel</artifactId>
    <version>${meepo-spring.version}</version>
</dependency>
```

## Detailed Documents

* <https://wings.fessional.pro/b-meepo/>
* <https://github.com/fessionalpro/wings-doc/tree/main/src/b-meepo/>
* <https://gitee.com/fessionalpro/wings-doc/tree/main/src/b-meepo/>
