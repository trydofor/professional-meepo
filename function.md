# 内嵌函数列表

以`|`可构成管道处理，多个处理函数的链式调用，第一个为key，其后的都是`函数`，格式下，  
`key` `|` `funA` `|` `funB arg1 "arg 2"`
以上等同于函数调用链`funB(ctx, funA(ctx.get("key")), "arg1", "arg 2")`

函数必须是以下类型之一，通过注册才可以被Meepo使用。

* java.util.function.Function
* pro.fessional.meepo.eval.JavaEval

函数，均以`fun:`前缀作为全函数名，和变量不冲突时，可以省略前缀。

## 当前日时 now

`fun:now pattern?` - String:javaEval, 动态计算，`pattern`格式化

   - obj，若是`java.util.Date`或`TemporalAccessor`，则格式化
   - 若是null或其他，则为`LocalDateTime.now()`
   - pattern，为`DateTimeFormatter`格式，
   - 若是null，则为`yyyy-MM-dd HH:mm:ss`

## 数字取余 mod

`fun:mod arg...` -String:javaEval，根据数字对args取余获得args值

   - obj, 需要是Number，取intValue，对arg.length取余
   - arg，必须有值，可为字符串或数字

## 字符格式化 fmt

`fun:fmt pattern` - String:javaEval, printf格式化对象

   - obj，为任意对象
   - pattern，为java格式化，，调用String.format(pattern,obj)

