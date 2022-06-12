# meepo changelog

<https://keepachangelog.com>

## [1.4.1] - Unreleased

### Added

- 支持BigDecimal,Double,Float,Long的字面量
- EACH增加first,last属性
- 增加fun:abs
- 字面量支持 Boolean 型: TRUE:FALSE
- 字面量支持 Number 型: 整形，浮点，BigDecimal

## [1.4.0] - 2021-01-06

### Added

- 丰富内建函数，详细参考 function.md
- 支持`占位符模板`的解析，使用map引擎合并

### Changed

- 内建函数接口JavaEval，移动至eval包
- MapEngine支持简化版函数，即无前缀

## [1.0.0] - 2020-11-24

### Added

- 静态替换和动态构建，流程，循环
- 多段缩排功能
- Java,js,exec引擎
- 内存碎片，线程及性能优化

## [master]

[master]: https://github.com/trydofor/pro.fessional.meepo
[1.0.0]: https://github.com/trydofor/pro.fessional.meepo/releases/tag/1.0.0
[1.4.0]: https://github.com/trydofor/pro.fessional.meepo/releases/tag/1.4.0
