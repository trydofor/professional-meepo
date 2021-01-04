package pro.fessional.meepo.eval;

/**
 * 函数按目的和参数类型分包
 *
 * @author trydofor
 * @since 2021-01-02
 */
public class FunEnv {
    // map 变量
    public static final String KEY$PREFIX = "fun:";

    // time
    public static final String ENV$NOW_DATE = "now.date";
    public static final String ENV$NOW_TIME = "now.time";
    public static final String FUN$NOW = KEY$PREFIX + "now";

    // num
    public static final String FUN$MOD = KEY$PREFIX + "mod";
    public static final String FUN$ABS = KEY$PREFIX + "abc";

    // fmt
    public static final String FUN$FMT = KEY$PREFIX + "fmt";

    // str
    public static final String FUN$UPPER_CASE = KEY$PREFIX + "upperCase";
    public static final String FUN$LOWER_CASE = KEY$PREFIX + "lowerCase";
    public static final String FUN$DOT_CASE = KEY$PREFIX + "dotCase";
    public static final String FUN$KEBAB_CASE = KEY$PREFIX + "kebab-case";
    public static final String FUN$BIG_KEBAB = KEY$PREFIX + "BIG-KEBAB";
    public static final String FUN$CAMEL_CASE = KEY$PREFIX + "camelCase";
    public static final String FUN$PASCAL_CASE = KEY$PREFIX + "PascalCase";
    public static final String FUN$BIG_SNAKE = KEY$PREFIX + "BIG_SNAKE";
    public static final String FUN$SNAKE_CASE = KEY$PREFIX + "snake_case";
}
