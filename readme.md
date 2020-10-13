# pro.fessional.meepo

米波，地卜师，主狗和分身狗具有同等的技能和待遇。  
一个基于注释和标记的不破坏母版语法的模板引擎翻译器。

![meepo](./meepo_full.png)

解决现代模板引擎语法，会破坏其模板文件本身的预览和编辑的特性。
米波只做中间层翻译，类似C的宏功能，支持任何有注释的语言做模板。

 * 从`java`生成`*.java`，模板和目标都是可编译
 * 从`sql`生成`*.sql`，模板和目标都可以执行
 * 从`htm`生成`*.htm`，模板和目标都可以预览

## 1.模板特性

在使用模板的场景中，特别希望模板的语法不被破坏，以便在预览和修改时有原生特性。
有自己语法特征的模板为`母版`，如java`母版`，直接用可编译的`Tmpl.java`。

`米波`在`母版`中通过`注释`和`注解`做语法标记，并转换成底层模板引擎的语法。  
当前使用pebble作为`底层引擎`，因其benchmark和语法较好。当然无缝支持其他引擎。

 * [pebble template](https://pebbletemplates.io/)
 * [template-benchmark](https://github.com/fizzed/template-benchmark)

## 2.语法详解

米波模板语法中，存在以下基础语素和约定。

 * `空白` - 一个`0x20`或`0x09`，即英文空格或`\t`
 * `母版注释` - 母版语言的注释的领起部分，如,`//`，`/*`
 * `指令` - 米波语法中特殊意义的特征标记，如前缀
 * `DNA` - define native annotation
 * `RNA` - run native annotation
 * `?+*` - 分表表示0或1个，至少一个，0或多个
 * 指令所在行，只被米波解析，对底层模板不可见

为了简化，后续举例中，省略领起`指令`的`母版注释`+`空白`

 * `DNA:MEEPO` 魔法注释，用来定义`母版注释`，以便后续解析
 * `DNA:SET` 设定变量，在一个范围内定义一个模板变量，后续替换。
 * `DNA:DEL` 删除变量，删除多个变量。
 * `DNA:VAR` 临时变量，简化版的SET加DEL。
 * `DNA:RAW` 原生模板，执行后面的原生模板语法。
 * `RNA:FUN` 执行函数，将行内字串解析成`Function`执行。
 * `RNA:EXE` 执行命令，将行内字串解析成当做系统命令执行。

### DNA:MEEPO 魔法注释

语法：`DNA:MEEPO` (`空白`+ `变量头` `空白`+ `变量尾`)?

定义`注释`，标识此文件为`米波`模板，并用其解析后续`母版`。

 * 变量头 - 底层模板变量的左边界符，默认空，如`${`,`{{`
 * 变量尾 - 底层模板变量的右边界符，默认空，如`}`,`}}`

比如在html中，tag部分用html注释，script部分，用js注释。
类似sql的`DELIMITER`定义结束符的用法和作用，举例如下，

 * java - `// MEEPO-DNA`，以`//`为注释
 * java - `/* MEEPO-DNA */`，又以`/*`为注释
 * sql -  `-- MEEPO-DNA`，以`--`为注释
 * bash -  `# MEEPO-DNA`，以`#`为注释
 * html = `<!-- MEEPO-DNA -->`

多次定义时，不出现`变量头`和`变量尾`时，表示沿用起始定义。
后续举例中，都以`// MEEPO-DNA {{ }}` 为例。

### DNA:SET 设定变量

语法：`DNA:SET` `空白`+ `变量` `空白`+ `符号` `=` `空白`? `特征`

定义一个在model中可以找到，在模板中要替换的占位符特征。其中，

 * 变量 - 非空白和等号(`[^= \t]+`)的至少一个字符，区分大小写。
 * 符号 - `*`表示正则。`1`数字，表示第1个重复项。
 * 特征 - 普通字符或正则（若有group，取第1个）
 * `=` - 其前的`空白`忽略，其后只忽略第1个`空白`。
 * 当`变量`在被重新定义时，覆盖旧值。值为空时，等同于`DEL`自身。

``` java
/* 
 * 只把此行的false当做变量，用useIdAsKey的值替换。底层模板输出为:
 * public static final boolean useIdAsKey = {{useIdAsKey}};
 */
// DNA:SET useIdAsKey = false
public static final boolean useIdAsKey = false;
// DNA:SET useIdAsKey =
```

### DNA:DEL 删除变量

语法：`DNA:DEL` (`空白`+ `变量`)+

删除多个通过`SET`定义的变量，以结束其作用域，取消后续的替换作用。

``` java
/* 
 * 分别定义，明文的id，正则group(1)的desc和第2个匹配的info,底层模板输出为:
 * SUPER({{id}}, "ConstantEnumTemplate", "{{desc}}", "{{info}}"),
 */
// DNA:SET id = 1010100
// DNA:SET desc *= "(性别)",
// DNA:SET info 2= 性别
SUPER(1010100, "ConstantEnumTemplate", "性别", "性别"),
// DNA:DEL id desc info
```

### DNA:VAR 临时变量

语法：同`SET`

`SET`和`DEL`的简化，只对其下的非`指令`行生效，以下效果等同于`SET`和`DEL`举例。

``` java
// DNA:VAR useIdAsKey = false
public static final boolean useIdAsKey = false;
// DNA:VAR id = 1010100
// DNA:VAR desc *= "(性别)",
// DNA:VAR info 2= 性别
SUPER(1010100, "ConstantEnumTemplate", "性别", "性别"),
```

### DNA:RAW 原生模板

语法：`DNA:RAW` `空白`+ `原生模板`

用注释的语法定义一个`模板`，用以弥补`母版`语法不支持的情况。
效果是，删除从`注释`到（`DNA:RAW`和`空白`）间的字符，输出原生模板。

``` java
/* 以下两行具有相同的输出效果，及删除了`// DNA:RAW ` */
SUPER(1010100, "ConstantEnumTemplate", "性别", "性别"),
// DNA:RAW SUPER(1010100, "ConstantEnumTemplate", "性别", "性别"),
```

### RNA:FUN 执行函数

语法：`DNA:FUN` `空白`+ `函数表达式`

其后所有的字符串都会被视为`函数表达式`，被具体实现引擎机制执行。
比如，在spring环境下，当做SpEL执行。
执行结果如果是`CharSequence`，会替换当前指令行`注释`之后。

### RNA:EXE 执行命令

语法：`DNA:FUN` `空白`+ `命令行`

其后所有的字符串都会被视为`命令行`，被当做os的命令执行。
如果stdout有输出，会替换当前指令行`注释`之后。
