# pro.fessional.meepo

米波，地卜师，主身和分身具有同等的技能，一荣俱荣，一损俱损的待遇。  
一个基于`母版`语法注释和标记的不破坏`母版`语法的非专业模板引擎。

![meepo](./meepo_full.png)

解决现代模板引擎自身语法，会破坏其目标文件的语法，预览和编辑的特性的干扰。  
米波仅做静态翻译和有限的动态控制（动态编译java），类似C的宏功能，性能高效。

 * 从`java`生成`*.java`，模板和目标文件都是可编译
 * 从`sql`生成`*.sql`，模板和目标文件都可以执行
 * 从`htm`生成`*.htm`，模板和目标文件都可以预览

## 1.模板特性

在使用模板的场景中，特别希望`模板语法`不要破坏`目标文件`的语法，两者无入侵的共存，  
如用velocity生成java时，希望模板，同时受velocity和java语法检查和加持。  
如生成html时，也希望模板能够不破坏html语法，能够直接在浏览器中预览。

 * `模板语法` - 底层模板的语法，如 FreeMarker, Velocity
 * `目标语法` - `母版语法`，有自己语法的目标文件，如java，html
 * `米波语法` - 利用母版语法的注释，做的简单标记指令，完成翻译

在`母版`中通过`注释`做语法标记，逐行处理规则替换，以输出`底层模板(backend)`。  
母版的处理分为`解析parse`和`合并merge`两个过程，解析时的查找依赖正则表达式。  
合并时，除了部分`RNA`外，都是直接输出，效能等于`StringBuilder.append`。

在RNA中没有复杂的`流程控制`及`执行函数`的功能，所以一次`解析`后，后续`合并`非常高效，

 * 没有`RNA`时，相当于幂等操作的静态字符串，仅merge一次，后续直接使用。
 * `RNA`依赖于`执行引擎`，除动态语言外，相当于`Map`+`StringBuilder`
 * `java`执行引擎，动态编译成字节码，首次编译后等于原生java class
 * `StringBuilder`预先计算长度，以避免过程中扩容复制。

## 2.应用举例

米波进行模板翻译，不依赖任何模板，测试和演示时使用pebble，因其benchmark和语法较好。

Pebble，FreeMarker和Velocity此类模板有自己的语法特性，在行业内大量使用。  
有些IDE有插件支持，但都是模板语言，而非目标文件的语言的支持，包括语法高亮，纠错等加持。

Thymeleaf(近期停止更新了)类的模板不会破坏目标文件语法，并且应用领域和具体语言特性绑定。

 * [thymeleaf template](https://www.thymeleaf.org/)
 * [pebble template](https://pebbletemplates.io/)
 * [template-benchmark](https://github.com/trydofor/template-benchmark)

虽然，米波本身的动态控制能力有限，但使用`执行引擎`（如js和java）可以做很复杂的功能操作。

### 2.1.忽略指令行空白，可读性优先

底层模板的[blog-trim.peb](src/test/resources/template/blog/blog-trim.peb)，
不能有效的被html和js语法加持，IDE插件能够识别pebble语法。
``` pebble
<body>
  {% for article in articles %}
  <h3>{{ article.title }}</h3>
  <p>{{ article.content }}</p>
  {% endfor %}
  <script type="text/javascript">
    var machineId = {{machineId}}
  </script>
</body>
```

同等输出的[blog-trim.htm](src/test/resources/template/blog/blog-trim.htm)，
保留原本的html和js特性，可以以html编辑。也可以使用pebble插件编辑。  
没有使用`!`，如果指令行独占一行，且行内全为空白，则不输出此行。第一行故意有个空格。
``` html
 <!-- HI-MEEPO -->
<body>
  <!-- DNA:RAW {% for article in articles %} -->
  <h3>{{ article.title }}</h3>
  <p>{{ article.content }}</p>
  <!-- DNA:RAW {% endfor %} -->
  <script type="text/javascript">
    // HI-MEEPO
    // DNA:SET /"machine-id"/{{machineId}}/
    var machineId = "machine-id"
  </script>
</body>
```

### 2.2.保留指令行空白，一致性优先

底层模板的[blog-pure.peb](src/test/resources/template/blog/blog-pure.peb)，
注意`<body>`上有一空行，`var`前共有有4x3=12个空格。
``` pebble

<body>
  {% for article in articles %}
  <h3>{{ article.title }}</h3>
  <p>{{ article.content }}</p>
  {% endfor %}
  <script type="text/javascript">
            var machineId = {{machineId}}
  </script>
</body>
```

同等输出的[blog-pure.htm](src/test/resources/template/blog/blog-pure.htm)，
使用了`!`，使得米波只处理注释首尾间的内容，保留之外的换行和空白（var前2行各4个空格）。
``` html
<!-- HI-MEEPO! -->
<body>
  <!-- DNA:RAW {% for article in articles %} -->
  <h3>{{ article.title }}</h3>
  <p>{{ article.content }}</p>
  <!-- DNA:RAW {% endfor %} -->
  <script type="text/javascript">
    // HI-MEEPO!
    // DNA:SET /"machine-id"/{{machineId}}/
    var machineId = "machine-id"
  </script>
</body>
```

### 2.3.全部替换，使用匿名全局

输出结果的[replace-all-o.htm](src/test/resources/template/repl/replace-all-o.htm)
```html
<div>
use anonymous all-life to replace div to div
</div>
```

米波模板的[replace-all-i.htm](src/test/resources/template/repl/replace-all-i.htm)，使用`*`为匿名全局替换。
```html
<!-- HI-MEEPO -->
<!-- DNA:SET /body/div/* -->
<body>
use anonymous all-life to replace body to div
</body>
```

### 2.4.间隔替换，使用指定范围

输出结果的[replace-1a3-o.htm](src/test/resources/template/repl/replace-1a3-o.htm)
```html
<div>
use ranged-life to replace 1st and 3rd body to div
</div>
```

米波模板的[replace-1a3-i.htm](src/test/resources/template/repl/replace-1a3-i.htm)，使用`1,3`逗号分隔，确认次数。
```html
<!-- HI-MEEPO -->
<!-- DNA:SET /body/div/1,3 -->
<body>
use ranged-life to replace 1st and 3rd body to div
</body>
```

### 2.5.范围替换，使用命名全局

输出结果的[replace-end-o.htm](src/test/resources/template/repl/replace-end-o.htm)
```html
<body>
use named-life to replace scoped div to div
</body>
```

米波模板的[replace-end-i.htm](src/test/resources/template/repl/replace-end-i.htm)，使用`end`和命名生命周期。
```html
<!-- HI-MEEPO -->
<body>
<!-- DNA:SET /body/div/body -->
use named-life to replace scoped body to div
<!-- DNA:END body -->
</body>
```

### 2.6.保留原样，使用魔免黑皇杖

输出结果的[black-king-bar-o.htm](src/test/resources/template/bkb/black-king-bar-o.htm)
```html
<!-- DNA:SET /body/div/* -->
<body>
in bkb, all are plain text, including DNA:SET
</body>
```

米波模板的[black-king-bar-i.htm](src/test/resources/template/bkb/black-king-bar-i.htm)，使用`end`和命名生命周期。
```html
<!-- HI-MEEPO -->
<!-- DNA:BKB bkb -->
<!-- DNA:SET /body/div/* -->
<body>
in bkb, all are plain text, including DNA:SET
</body>
<!-- DNA:END bkb -->
```

### 2.7.删除行块，实际是替换为空

输出结果的[delete-1a3-o.htm](src/test/resources/template/del/delete-1a3-o.htm)
```html
delete all, but this line

```

米波模板的[delete-1a3-i.htm](src/test/resources/template/del/delete-1a3-i.htm)，删除（替换为空）第1和3匹配行。
```html
<!-- HI-MEEPO -->
<!-- DNA:SET /^.*\n?//1,3 -->
<body>
delete all, but this line
</body>
```

米波模板的[delete-all-i.htm](src/test/resources/template/del/delete-all-i.htm)，删除body及期间所有。
```html
<!-- HI-MEEPO -->
<!-- DNA:SET :<body>[\s\S]*</body>:: -->
<body>
delete all, but this line
</body>
```

### 2.8.单次执行，使用变量，以便后续读取

输出结果的[put-use-o.htm](src/test/resources/template/rna/put-use-o.htm)
```html
<body>
1009+10+7=1026
1009+10+7=1026
</body>
```

米波模板的[put-use-i.htm](src/test/resources/template/rna/put-use-i.htm)，用PUT和USE做单次执行，到处使用。
```html
<!-- HI-MEEPO -->
<!-- RNA:PUT js/calc/1009+10+7/ -->
<body>
<!-- RNA:USE /result/calc/* -->
1009+10+7=result
1009+10+7=result
</body>
```

### 2.9.每次执行，一个js版的计数器

输出结果的[run-any-o.htm](src/test/resources/template/rna/run-any-o.htm)
```html
<body>
i=1009+10+7=1026
i++ == 1027
i++ == 1028
</body>
```

米波模板的[run-any-i.htm](src/test/resources/template/rna/run-any-i.htm)，用PUT和USE做单次执行，到处使用。
```html
<!-- HI-MEEPO -->
<!-- RNA:RUN js//i=1009+10+7/ -->
<!-- RNA:RUN js/counter/i++;i.toFixed()/* -->
<body>
i=1009+10+7=1026
i++ == counter
i++ == counter
</body>
```

### 2.10.导入外部模板，import uri

通过uri引擎，可以读取 `file://`,`classpath:`和`http://`等外部资源

输出结果的[import-o.htm](src/test/resources/template/imp/import-o.htm)
```html
imported text
```

米波模板的[import-i.htm](src/test/resources/template/imp/import-i.htm)，读入[import-f.htm](src/test/resources/template/imp/import-f.htm)。
```html
<!-- HI-MEEPO -->
<!-- RNA:RUN uri#import-here#classpath:/template/imp/import-f.htm# -->
import-here
```

### 2.11.执行java代码，java引擎

输出结果的[compile-java-o.htm](src/test/resources/template/java/compile-java-o.htm)
```html
2020-07-09 00:00:00
```

米波模板的[compile-java-i.htm](src/test/resources/template/java/compile-java-i.htm)
```html
<!-- HI-MEEPO -->
<!-- RNA:RUN java#java-output#
import java.time.LocalTime, java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

LocalDate date = LocalDate.parse("2020-07-09");
LocalDateTime ldt = LocalDateTime.of(date, LocalTime.of(0, 0, 0));
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
return ldt.format(fmt);
# -->
java-output
```

### 2.12.替换的界定符，不想用`/`

`界定符`是第1个非(`空白`,`!`,`英数`)1-2字节char，常用的如`/`，汉字。  
所以只要避免和指令中内容重复即可，但是，像👹这种的3,4字节不可以，getChar会分裂。

``` js
// RNA:RUN js/counter/i++;i.toFixed()/
// RNA:RUN js:counter:i++;i.toFixed():
// RNA:RUN js|counter|i++;i.toFixed()|
// RNA:RUN js汉counter汉i++;i.toFixed()汉
```

### 2.13.自动缩排多行代码，美观易读

参加测试[JavaTest.java](src/test/java/pro/fessional/meepo/tmpl/JavaTest.java)，`变量`为集合类，其元素自带换行

``` txt
c.put("method", Arrays.asList("LocalDate date = LocalDate.parse(\"2020-07-09\");\n",
                "LocalDateTime ldt = LocalDateTime.of(date, LocalTime.of(0, 0, 0));\n",
                "DateTimeFormatter fmt = DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss\");\n",
                "return ldt.format(fmt);"));
```
会输出有了缩排的[JavaOut.java](src/test/resources/pro/fessional/meepo/poof/impl/java/JavaOut.java)

``` java
    public Object eval(@NotNull Map<String, Object> ctx) {
        LocalDate date = LocalDate.parse("2020-07-09");
        LocalDateTime ldt = LocalDateTime.of(date, LocalTime.of(0, 0, 0));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ldt.format(fmt);
    }
```
如果，`变量`不是集合类，而是带有`\n`的字符串，那么会这样的呲牙效果

``` java
    public Object eval(@NotNull Map<String, Object> ctx) {
        LocalDate date = LocalDate.parse("2020-07-09");
LocalDateTime ldt = LocalDateTime.of(date, LocalTime.of(0, 0, 0));
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
return ldt.format(fmt);
    }
```


## 3.框架集成

## 3.1.与Spring体系集成

在SpringMvc和SpringBoot体系中，View层的输出，有以下约定，
TODO

## 3.2.与底层模板引擎集成

TODO

## 4.语法概要

米波模板语法中，存在以下基础语素和约定。

 * `空白` - 一个`0x20`或`0x09`，即英文空格或`\t`
 * `英数` - `[a-zA-z0-9]`，英文字母和数字，区分大小写
 * `母版注释` - 母版语言的注释，如,`//`，`/*`和`*/`
 * `指令` - 米波语法中特殊意义的特征标记，如前缀
 * `DNA` - Defined Native Annotation
 * `RNA` - Runnable Native Annotation
 * `?+*` - 分别为`[0,1]`，`[1,∞)`，`[0,∞)`
 * `指令行` - 米波指令所在行，只被`米波`解析，merge后不可见
 * `行` - 指`[^\n]\n`或`[^\n]$`两者格式。

为了简化，后续举例中，省略领起`指令`的`母版注释`+`空白`

 * `HI-MEEPO` 嗨！米波，用来定义`母版注释`，以便后续解析
 * `DNA:SET` 设定替换，在一个范围内定义一个模板替换。
 * `DNA:END` 结束作用，结束多个`作用`的作用域。
 * `DNA:BKB` 免疫区域，被`END`之前的区域不进行解析。
 * `DNA:RAW` 原生模板，执行后面的原生模板语法。
 * `RNA:PUT` 存入变量，使用引擎运行`执行体`，结果存入环境。
 * `RNA:USE` 使用变量，使用环境变量，内置或`PUT`的变量。
 * `RNA:RUN` 每次执行，每次都会执行功能体，比如计数器。
 * `RNA:WHEN` 条件执行，组合成if-elseif-else逻辑块。
 * `RNA:EACH` 循环执行，应用于数组或集合，循环输出。
 * `RNA:ELSE` 否则条件，对`WHEN`和`EACH`执行否则分支。
 * `RNA:DONE` 结束执行，结束`WHEN`和`EACH`的作用域。

在处理`行符`时，以`\n`断行，window的`\r\n`也做`\n`处理。  
单行注释型，若结尾有`\n`，会作为语法的结束符，即合并时不会输出。

因为解析时使用了java的RegExp作为底层实现，所以需要一定的基础。

 * 查找中，常误用字符有`.^$?+*{}()[]\|`。`[]`内字符有些不用转义。
 * 替换中，`\`主要用作`反向引用`，部分`()`组合有特殊含义。

正则的compile选项是`UNIX_LINES`和`MULTILINE`，通过`embedded flag`设定

 * `(?idmsuxU-idmsuxU)` Nothing, but turns match `flags` on - off
 * `(?idmsux-idmsux:X)` X, as a non-capturing group with the given `flags` on - off
 * i Pattern.CASE_INSENSITIVE 不区分大小写，默认关闭
 * d Pattern.UNIX_LINES 只有`\n`作为`行符`，默认开启
 * m Pattern.MULTILINE `^`和`$`会匹配`行符`，默认开启
 * s Pattern.DOTTAL `.`匹配`行符`，默认关闭
 * u Pattern.UNICODE_CASE 如全角字母的大小写，默认关闭
 * x Pattern.COMMENTS 忽略空白，支持行注释，默认关闭
 * U Pattern.UNICODE_CHARACTER_CLASS，默认关闭

### 4.1.HI-MEEPO 嗨！米波

语法：`注释头` `空白`+ `HI-MEEPO` `!`? `空白`+ `注释尾`?

定义`注释`并标识此文件为`米波`模板，以解析其后续`母版`。

 * `注释头` - 单行注释或多行注释的首部，前一个非`空白`字符串
 * `HI-MEEPO` - 这么怪的名字(hi meepo)，主要是避免重复和转义。
 * `!` - 如果存在`!`，表示保留指令前后的`空白`，后详述。
 * `注释尾` - 多行注释的结尾，后一个非`空白`字符串

`嗨！米波`必须独占一行，最好有`空白`分割，以便阅读时清晰。  
类似sql的`DELIMITER`定义结束符的用法和作用，举例如下，

 * java - `// HI-MEEPO`，以`//`为注释
 * java - `/* HI-MEEPO */`，又以`/*`和`*/`为注释
 * sql -  `-- HI-MEEPO`，以`--`为注释
 * bash -  `# HI-MEEPO`，以`#`为注释
 * html = `<!-- HI-MEEPO -->`

注意，`注释头`存在尾字符叠字的情况，米波只处理同字符的叠字，举例如下，

 * `/*` - `/***** DNA:RAW`，无效，不处理
 * `//` - `////// DNA:RAW`，处理叠字
 * `#` - `##### DNA:RAW`，处理叠字

对于后续文本（DNA和RNA）的解析，存在行解析和块解析2种，规则如下

 * `HI-MEEPO` 始终是行解析，必须独占一行。
 * `单行注释`型米波，会按行解析，按行解析。
 * `多行注释`型米波，会跨行读取，按块解析。
 * 因非按行解析，故正则匹配时`^`和`$`不定未行首和行尾。

关于`HI-MEEPO!`和`HI-MEEPO`处理指令行的首位`空白`存在以下规则。

 * 无`!`，并且指令独占一行，输出时忽略本行，即指令行后的第一个`\n`。
 * 有`!`时，只处理米波头尾直接的指令，前后空白保留。
 * `DNA:RAW`比较特殊，无视`!`设置，保留指令外，移除指令内的首位空白。
 * `@<!--_DNA:RAW_SUPER_-->@`中`@`和`_`分别标识保留和移除的空白。

后续举例中，都以`// HI-MEEPO` 为例，但省略书写。

## 5.厂长DNA

DNA好比一个厂长，定义替换指令，在parse时，进行高效的静态文本替换。

### 5.1.DNA:SET 设定替换

语法：`DNA:SET` `空白`+ `界定` `查找` `界定` `替换` `界定` `作用`?

在一定作用域内，把符合特征的字符串替换成底层模板的字符串。其中，

 * `界定` - 第1个非(`空白`,`!`,`英数`)1-2字节的char，常用的如`/`，汉字。
 * `查找` - 不含`界定`的正则特征，存在分组时参考`分组引用`。
 * `查找`为空时，忽略此`SET`。
 * `替换` - 不含`界定`的正则特征，存在引用时参考`分组引用`。
 * `替换`为空时，表示删除，即替换成空。
 * `作用` - 生效的作用次数，即到何时结束作用，非`空白`。

`分组引用`指查找时有`()`的group或替换时使用`\1`的反向引用的情况。  
这会对特征字符串的边界有影响，也要避开书写复杂的表达式，约定规则如下，

 * 如果`查找`中无group，在使用group(0)，即全部匹配。
 * 如果`查找`有group时，取第一个`(`，即group(1)内容。
 * 如`((A)(B(C)))`，按`(`从左到右出现的顺序计数。
    - group(1) - ((A)(B(C)))
    - group(2) - (A)
    - group(3) - (B(C))
    - group(4) - (C)

`作用`即`作用次数`或`作用域`，默认作用`1`次，`*`表示`匿名`的无限次。

 * `次数`，以`,`分隔的单次或`-`连接的闭区间，如`1-3,15`。
 * `命名`的无限次作用，可被`END`结束。

``` js
// DNA:SET /false/{{user.male}}/
var isMale = false;
/* 只把此行的false替换为user.male模板变量。底层模板输出为:
var isMale = {{user.male}};
*/
```

### 5.2.DNA:END 结束作用

语法：`DNA:END` (`空白`+ `作用`)+

结束多个指令产生的`作用`的作用域，如`SET`的命名作用域。

``` js
// DNA:SET /1010100/{{id}}/id
// DNA:SET /"(性别)"/{{desc}}/1
// DNA:SET /性别/{{info}}/2
SUPER(1010100, "ConstantEnumTemplate", "性别", "性别")
// DNA:END id
/* 分别定义，命名的id；group(1)的desc；第2次匹配的info。底层模板输出为:
SUPER({{id}}, "ConstantEnumTemplate", "{{desc}}", "{{info}}")
*/
```

### 5.3.DNA:BKB 免疫区域

语法：`DNA:BKB` `空白`+ `作用`

定义一个`命名`的全局免疫作用，可以被`END`结束，之间的文本和指令不会被处理。

 * 文本 - 任何非米波指令格式的文本
 * 指令 - 除了当前生效的BKB对应的END外，都视为文本处理。
 * 当前只能有一个生效的BKB

``` js
// DNA:BKB 黑皇杖
// DNA:SET /"(性别)"/desc/1
SUPER(1010100, "ConstantEnumTemplate", "性别", "性别")
// DNA:END 黑皇杖
/* 无视了SET指令，底层模板输出为:
// DNA:SET /"(性别)"/desc/1
SUPER(1010100, "ConstantEnumTemplate", "性别", "性别")
*/
```

### 5.4.DNA:RAW 原生模板

语法：`DNA:RAW` `空白`+ `原生模板`

用注释的语法定义一个`模板`，用以弥补`母版`语法不支持的情况。  
使用单行注释表意清晰，多行注释时，只保留头尾直接的内容。  
效果是，删除`注释头`,`DNA:RAW` 和`注释尾`及之间的`空白`。

``` js
/* 以下两行具有相同的输出效果，即删除了`// DNA:RAW ` */
SUPER(1010100, "ConstantEnumTemplate", "性别", "性别")
// DNA:RAW SUPER(1010100, "ConstantEnumTemplate", "性别", "性别")
```

## 6.主任RNA

RNA好比车间主任，定义执行指令，在merge时调用`执行引擎`，用其结果做替换。

 * 一个`执行引擎`可以执行多种`类型`的`功能体`，一种类型简称一个`引擎`。
 * `引擎`的命名，必须为`英数`，区分大小写，如`js`。
 * 命名可以用`!`结尾，如`js!`，表示仅执行，但忽略输出（用空字符串代替）
 * 执行结果为`null`时，使用空字符串代替。

RNA中默认的`引擎`默认为`map`。用户可以通过RnaManager注册引擎，后详述。

 * `map` - `session`级，以`功能体`为key，到`环境`中取值，没有则输出key。
 * `raw` - `nothing`级，直接把`功能体`当字符串返回，不会展开转义字符。

米波在多行注释时，使用多行的块解析，所以`功能体`天然支持多行，提高可读性。

### 6.1.RNA:USE 使用变量

语法：`RNA:USE` `空白`+ `界定` `查找` `界定` `变量` `界定` `作用`?

`SET`的`RNA`版本，区别在于从`map`引擎中取得`变量`值，而非底层模板的字面量替换。  
变量获取规则，详见map引擎说明。

在`变量`合并时，会根据`变量值`的类型进行自动`多段缩排`支持，同时满足，

 * `被查找`的字符串前有缩排的空白。
 * `变量值`是Array和Collection时，其内条目数大于1个。

对2个起的元素进行缩进，和第1个元素列对齐。缩排后会出现不智能的情况，影响了美观。

 * 缩排的对象，没有`\n`结尾，不换行，出现斑马线效果。
 * 未缩排对象，包含`\n`，换行了，出现呲牙的效果。

``` js
// DNA:USE /meepo/user.home/
var userHome = "meepo";
/* 读取System.getProperty("user.home")。底层模板输出为:
var userHome = "/home/trydofor"; 
*/
```

### 6.2.RNA:PUT 存入变量

语法：`RNA:PUT` `空白`+ `引擎`? `界定` `变量` `界定` `功能体` `界定`

指定`引擎`执行`功能体`，把执行结果存入`环境`，以便其他`RNA`取值。

 * `环境`指米波context和部分脚本引擎上下文。
 * `引擎`，参考引擎说明。
 * `界定`同`SET`。
 * `变量`指存入上下文的变量，非母版字面量。
 * `功能体`由具体的执行引擎执行，如spring，则可当做SpEL执行。

``` js
// DNA:PUT os/who/basename $(pwd)/
/* 把结果`pro.fessional.meepo`放入米波的执行环境 */
```

### 6.3.RNA:RUN 每次执行

语法：`RNA:RUN` `空白`+ `引擎`? `界定` `查找` `界定` `功能体` `界定` `作用`?

`PUT`和`USE`的结合体，同样支持缩排，区别在于，

 * `查找`为空时，表示仅执行，不替换
 * `功能体`执行结果立即使用，不存入`变量`
 * 每次都执行，类似计数器功能，每次调用都会自增，无缓存。

``` js
// DNA:RUN os/rand/echo $RANDOM/1-3
var userName = "meepo-rand";
var userPass = "rand-rand";
/* 每次都获得随机数，输出3次。底层模板输出为:
var userName = "meepo-12599";
var userPass = "16345-31415";
*/
```

### 6.4.RNA:WHEN 条件执行

语法：`RNA:WHEN` `空白`+ `引擎`? `界定` `真假` `界定` `功能体` `界定` `归组`

可以使用多个`WHEN`组合成`if`-`else if`-`else`逻辑块。

 * `真假` - 必须是`y|yes|n|no|not`，表示求值的`取真`或`取假`。
 * `功能体` - 引擎执行结果，并对结果求值。
 * `归组` - 必须是`英数`，可别`ELSE`和`DONE`归组。

求值时，以下情况为`false`，对`false`执行`n`则为`true`

  * boolean的`false`
  * 对象 `null`
  * Number的double值是`NaN`或在正负`0.000000001`间（9位）
  * `empty`  空字符串，空数组，空Collection，空Map

``` html
<!-- RNA:WHEN /yes/it:rem0/bg -->
<li value="code">rem0-name</li>
<!-- RNA:WHEN /not/it:rem1/bg -->
<li value="code">rem2-name</li>
<!-- RNA:ELSE bg -->
<li value="code">rem1-name</li>
<!-- RNA:DONE bg -->
```

等同于以下js的伪代码的`if(a){}else if(!b){}else{}` 分支逻辑
``` js
if (it.rem0){
    console.log('<li value="code">rem0-name</li>')
} else if (!it.rem1){
    console.log('<li value="code">rem2-name</li>')
} else {
    console.log('<li value="code">rem1-name</li>')
}
```

### 6.5.RNA:EACH 循环执行

语法：`RNA:EACH` `空白`+ `引擎`? `界定` `步长` `界定` `功能体` `界定` `归组`

通过`归组`做为元素引用的循环体。若`归组`名为`it`，则`it.x`表示当前元素的`x`属性。

 * `步长` - 必须`-`和`数字`，表示循环顺序和步长，负数表示`倒序`
 * `功能体` - 引擎执行结果，需要是数组或集合，否则等同于`RNA:PUT`效果。
 * `归组` - 必须是`英数`，可别`ELSE`和`DONE`归组，引用当前元素和内置状态属性。

根据不同的数据类型，执行不同的循环处理，空或null跳过，可被`ELSE`执行。

 * Array - Class.isArray()
 * Collection<E> -  instance of Collection
 * 其他类型，不做任何循环
 * 倒序循环时，非RandomAccess和ReverseIterator，会toArray

循环体中，存在以下内置属性，用来表示循环的状态，若`归组`名为`it`，则，

 * 引用当前元素的`x`属性时，其格式为`it:x`，注意不是`it.x`
 * `it` - 当前循环的元素，避免同名，而产生环境污染
 * `it.count` - 内置变量，当前循环计数，1-base，未循环时为0
 * `it.total` - 内置变量，`归组`内所有元素的数量
 * 内置变量在循环结束后不移除，可以在循环外部使用。

因为米波是`专业`的`非专业`模板引擎，所以此`for-each`十分低级，

 * 支持有限的对象导航，使用`:`分隔对象，详见`map`引擎。
 * 集合内元素仅支持Map<String,?>和JavaBean的Getter取值。
 * 没有作用域隔离，同名`归组`，会造成context内变量覆盖。

``` html
<!-- RNA:EACH map/2/items/it -->
<!-- RNA:USE /name/it:name/* -->
<li value="code">rem0-name</li>
<!-- RNA:ELSE it -->
<li>no item</li>
<!-- RNA:DONE it -->
<!-- RNA:USE /total/it.total/ -->
<!-- RNA:USE /count/it.count/ -->
<div>result=count/total</div>
```

等同于以下js的伪代码的`for(;;)`或`for-in`循环逻辑，依`集合`类型和`步长`正负而定
``` js
let step=2 // 循环步长，负数为倒序，不可为0
let index=0 // 过程量
let it = null, count=0, total=items.length; // 内置变量
for(it in items){
    if(index++ % step !== 0) continue // 控制步长
    count++
    console.log('<li value="code">rem0-'+it.name+'</li>')
}
if(count === 0){
    console.log('<li>no item</li>')
}
console.log('<div>result='+count+'/'+total+'</div>')
```

### 6.6.RNA:ELSE 否则条件

语法：`RNA:ELSE` `空白`+  `归组`

通过`作用`归组，对同组的`WHEN`或`EACH`执行否则分支，情况如何。

 * `WHEN`时，表示没有任何一个`WHEN`被执行。
 * `EACH`时，表示循环体从未执行（如集合无元素）
 * `EACH-ELSE`和`pebble`语义相同，和python的`for-else`不同。

### 6.7.RNA:DONE 结束执行

语法：`RNA:DONE` (`空白`+  `归组`)+

通过`归组`归组，结束一个或多个`WHEN`和`EACH`的`归组`。


## 7.执行引擎

其中各`引擎`的实现和执行上下文是不一样的，即变量作用域不一样，存在以下2个级别，

 * `session`级，一次merge内的多次eval，同一context，eval间有影响，如`js`。
 * `nothing`级，每次eval的上下文无关联，eval间无影响，如`sh`依赖于bash设置。

执行中的引擎环境，在每次eval时，可以被context覆盖，也可以不覆盖，依赖于引擎实现。

### 7.1.字典引擎(map)

`session`级，每次eval共享context，context不覆盖引擎环境。

 * 如果mute，直接返回`字符串空`
 * 以`功能体`为`key`，依次查找，找到`非null`即返回
 * 顺序为context,System.property,System.env,Builtin
 * 没有找到`非null`值，直接返回`字符串空`

支持简单的对象导航，以`:`分隔对象，而非`.`，主要是避免以下情况，

 * java的System中有大量`.`型变量，如`os.name`，`user.home`
 * 如果用户存有`os`或`user`，使用`.`导航，则会发生混乱
 * 因为有`.`分隔的字符串变量存在，无法正确识别导航对象

以`:`分隔的对象导航，采用以下查找顺序和规则，以key为`out:it:name`为例，

 * 以key直接查找，有`非null`值，则return
 * 如key中存在`:`，以`:`分隔成`out`，`it`和`name`
 * 依次以`out`和`it`为对象key递归查找对象。
    - 若为任意递归中返回null，则return`字符串空`。
    - 若为Map类型，则以getKey的方式取值。
    - 其他类型，通过反射取值，以Getter命名规则和Field查找。
 * 递归中的最终对象，以`name`为key取值（map或反射）

内置以下`变量`，有些是动态计算，每次获得不同，有些是静态变量。

 * `now` - String, 动态计算，当前日期时间 `yyyy-MM-dd HH:mm:ss`
 * `now.date` - String, 动态计算，系统日期 `yyyy-MM-dd`
 * `now.time` - String, 动态计算，系统时间 `HH:mm:ss`
 * `user.name` - String, 当前系统用户，java内置
 * `user.dir` - String, 当前的工作目录，java内置

### 7.2.来啥回啥(raw)

`nothing`级，直接把`功能体`当字符串返回，但mute时返回`字符串空`。

### 7.3.内容引入(uri)

`nothing`级，把uri的内容以UTF8输出为字符串。首次读入，后续缓存。

 * `http://`,`https://`时，以GET读取
 * `file://`,`/`或`.`时，从file system读取
 * `classpath:`时，从classloader读入，注意没有`//`
 * 其他，以URLConnection读取，超时为3秒

### 7.4.直接执行(exe)

`nothing`级，直接执行命令，解析引号块和转义，捕获std_out输出。  
注意的是，每次eval时，engine会用context覆盖环境变量。

### 7.5.win下命令(cmd)

在window系，以`cmd /c`执行的exe。

### 7.6.unx下命令(sh)

在bash系，以`bash -c`执行的exe。

### 7.7.执行js脚本(js)

`session`级，以java的ScriptEngine执行js脚本，捕获最后一个求值。  
执行context，以`ctx`对象存在于js环境，可以通过`ctx.xxx`获得环境变量。

### 7.8.执行java代码(java)

`session`级，通过米波模板动态编译java代码，并以context为参加执行。

 * 头部`import java.util.*,java.util.Map;`，可以`,`分隔多个
 * 简单方法体单行（java不能简单），复杂的多行，以增加可读性。
 * 尾部以`return obj`返回，`;`可以省略。
 * 通过[模板](src/main/resources/pro/fessional/meepo/poof/impl/java/JavaName.java)动态编译java。
 * 编译的java实现了`JavaEval`接口，位于`pro.fessional.meepo.poof.impl.java`
 * 传入`Map<String, Object> ctx`，可读取context
 * 已经import的class有，
    - org.jetbrains.annotations.NotNull;
    - pro.fessional.meepo.poof.impl.JavaEngine;
    - java.util.Map;

## 9.常见问题

### 01.如何调试，debug解析

调试主要集中在Parse和RnaEngine执行上，因此logger只在此2处存在。
米波工程本身的test中，slf4j的日志基本是trace，因此在其他工程引入时，
需要把设置`pro.fessional.meepo`的级别为`trace`。

如果通过日志，不能调试到位，可以通过继承Parser，调用protected方法。

### 02.有关性能和线程安全

模板引擎都是，一次解析，多次使用的，并增加了预编译或缓存。

米波解析时，Parse本身基于字符串分析，仅在有`查找`的指令中使用正则，  
通常建议，解析的过程需要在单线程内进行，多次解析或竞争毫无意义。

合并使用时，如果不存在`Dyn`类指令，是静态字符串拼接，首次拼接，后续缓存。  
拼接过程中，预分配刚好够的buff，避免扩容。性能高于多次的原生String拼接。

无`Dyn`指令时，线程安全且碎片极少，可以放心使用。当存在`Dyn`指令时，
性能和线程安全，取决于执行引擎和传入的context。
