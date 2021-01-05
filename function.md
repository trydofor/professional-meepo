# 函数列表

函数的用法，详情参看[字典引擎引擎 map](./readme.md#7.1.字典引擎-map)的管道约定和函数规则。  
函数全名和别名均以`fun:`为前缀。在不冲突时使用，前缀可省略。

示例说明，在函数描述中，存在以下约定，
* `obj`，特指管道输出，没有时为null
* `arg...`表示，arg为可变参数。
* `arg?`表示，arg可以null，
* `&opt`表示 opt选项是默认值
* `String:javaEval`返回类型String，函数类型是javaEval

## 当前日时 now

类型用途：String:javaEval，输出指定格式的当前日期时间  
用法说明：fun:now ptn?
   - obj，若是`java.util.Date`或`TemporalAccessor`，则格式化
   - 若是null或其他，则为`LocalDateTime.now()`
   - ptn，为`DateTimeFormatter`格式，
   - 若是null，则为`yyyy-MM-dd HH:mm:ss`

```
# ptn含空格，用引号包围
{{ now 'yyyy-MM-dd HH:mm:ss' }}
# 输出 2021-01-05 10:01:31
```

## 数字取余 mod

类型用途：String:javaEval，根据数字对args取余，获得args余数位置的值  
用法说明：fun:mod arg... 
   - obj, 需要是Number，取intValue，对arg.length取余
   - arg，必须有值，可为字符串或数字

```
# index = 3; count = 4;
{{ index | mod even odd }}
{{ count | mod even odd }}
# 输出 odd even
```

## 字符格式化 fmt

类型用途：String:javaEval，调用String.printf格式化对象  
用法说明：fun:fmt ptn 
   - obj，为任意对象
   - ptn，为java格式化，调用String.format(ptn,obj)

```
# amount = 1000
{{ amount | fmt '$%,d' }}
# 输出 $1,000
```

## 大小写变换系列

类型用途：String:javaEval，调用调整obj大小写格式  
用法说明：fun:### arg?，其中 ### 为以下函数名和别名

 * upperCase 全部大写，支持 locale 参数
 * lowerCase 全部小写，支持 locale 参数
 * dotCase 逗点分隔，可定制大小写，如 try.do.for
 * kebabCase, kebab-case 减号分隔，可定制大小写，如 try-do-for
 * bigKebab, BIG-KEBAB 减号分隔，全大写，如 TRY-DO-FOR
 * camelCase 驼峰首字小写，如 tryDoFor
 * pascalCase, PascalCase 驼峰首字大写，如 TryDoFor
 * snakeCase, snake_case 下划线分隔，可定制大小写，如 try_do_for
 * bigSnake, BIG_SNAKE 下划线分隔，全大写，如 TRY_DO_FOR

参数说明
  * obj 否则toString，null返回空串
  * arg在upperCase,lowerCase中为locale格式
  * arg在dotCase,snakeCase,kebabCase中为&lower,upper,keep
  * 其余函数无arg，不支持定制大小写
  
 ```
 # author = try&DO&for
 {{ author | upperCase zh-cn }} #输出 TRY&DO&FOR
 {{ author | lowerCase zh-cn }} #输出 try&do&for
 {{ author | dotCase }}         #输出 try.do.for
 {{ author | dotCase lower }}   #输出 try.do.for
 {{ author | dotCase upper }}   #输出 TRY.DO.FOR
 {{ author | dotCase keep }}    #输出 try.DO.for
 {{ author | kebab-case }}      #输出 try-do-for
 {{ author | BIG-KEBAB }}       #输出 TRY-DO-FOR
 {{ author | camelCase }}       #输出 tryDoFor
 {{ author | PascalCase }}      #输出 TryDoFor
 {{ author | snake_case }}      #输出 try_do_for
 {{ author | BIG_SNAKE }}       #输出 TRY_DO_FOR
 ```

