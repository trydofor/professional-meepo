# meepo changelog

<https://keepachangelog.com>

## [1.5.0] - 2024-01-01

bump version, ready for stable channel.

### Changed

- deprecated JsEngine.
- RnaManager NOT register JsEngine

## [1.4.1] - 1.4.14 snapshot

### Added

- Support literal number `BigDecimal`, `Double`, `Float`, `Long`, `Integer`
- Added `first` and `last` properties to `EACH`
- Added `fun:abs` function
- Support Boolean type `TRUE`/`FALSE`

## [1.4.0] - 2021-01-06

### Added

- Rich built-in functions, see docs for details
- Support for `placeholder template`, merging with map engine

### Changed

- Moved `JavaEval` to eval package
- MapEngine supports functions without a prefix

## [1.0.0] - 2020-11-24

### Added

- Static replacement and dynamic construction, flow, loops
- Multi-paragraph indentation function
- `java`, `js`, `exec` engines
- Memory, Thread and Performance Optimization

## [master]

[master]: https://github.com/trydofor/professional-meepo
[1.0.0]: https://github.com/trydofor/professional-meepo/releases/tag/1.0.0
[1.4.0]: https://github.com/trydofor/professional-meepo/releases/tag/1.4.0
[1.5.0]: https://github.com/trydofor/professional-meepo/releases/tag/1.5.0
