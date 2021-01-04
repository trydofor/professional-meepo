package pro.fessional.meepo.eval.fmt;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.eval.NameEval;

import java.util.Locale;
import java.util.Map;

import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;
import static pro.fessional.meepo.eval.FunEnv.FUN$BIG_KEBAB;
import static pro.fessional.meepo.eval.FunEnv.FUN$BIG_SNAKE;
import static pro.fessional.meepo.eval.FunEnv.FUN$CAMEL_CASE;
import static pro.fessional.meepo.eval.FunEnv.FUN$DOT_CASE;
import static pro.fessional.meepo.eval.FunEnv.FUN$KEBAB_CASE;
import static pro.fessional.meepo.eval.FunEnv.FUN$PASCAL_CASE;
import static pro.fessional.meepo.eval.FunEnv.FUN$SNAKE_CASE;
import static pro.fessional.meepo.eval.FunEnv.FUN$UPPER_CASE;

/**
 * @author trydofor
 * @since 2021-01-04
 */
public class Case {

    public static final NameEval funCamelCase = new NameEval() {
        @Override
        public @NotNull String name() {
            return FUN$CAMEL_CASE;
        }

        @Override
        public @NotNull String info() {
            return "字符串变camelCase";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            StringBuilder sb = wordCase(obj.toString(), false);
            return sb.toString();
        }
    };

    public static final NameEval funPascalCase = new NameEval() {
        @Override
        public @NotNull String name() {
            return FUN$PASCAL_CASE;
        }

        @Override
        public @NotNull String info() {
            return "字符串变PascalCase";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            StringBuilder sb = wordCase(obj.toString(), true);
            return sb.toString();
        }
    };

    public static final NameEval funSnakeCase = new NameEval() {
        @Override
        public @NotNull String name() {
            return FUN$SNAKE_CASE;
        }

        @Override
        public @NotNull String info() {
            return "字符串变snake_case";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            StringBuilder sb = wordCase(obj.toString(), false, '_');
            return sb.toString();
        }
    };

    public static final NameEval funBigSnake = new NameEval() {
        @Override
        public @NotNull String name() {
            return FUN$BIG_SNAKE;
        }

        @Override
        public @NotNull String info() {
            return "字符串变BIG_SNAKE";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            StringBuilder sb = wordCase(obj.toString(), true, '_');
            return sb.toString();
        }
    };

    public static final NameEval funKebabCase = new NameEval() {
        @Override
        public @NotNull String name() {
            return FUN$KEBAB_CASE;
        }

        @Override
        public @NotNull String info() {
            return "字符串变kebab-case";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            StringBuilder sb = wordCase(obj.toString(), false, '-');
            return sb.toString();
        }
    };

    public static final NameEval funBigKebab = new NameEval() {
        @Override
        public @NotNull String name() {
            return FUN$BIG_KEBAB;
        }

        @Override
        public @NotNull String info() {
            return "字符串变BIG-KEBAB";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            StringBuilder sb = wordCase(obj.toString(), true, '-');
            return sb.toString();
        }
    };

    public static final NameEval funDotCase = new NameEval() {
        @Override
        public @NotNull String name() {
            return FUN$DOT_CASE;
        }

        @Override
        public @NotNull String info() {
            return "字符串变dot.case";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            StringBuilder sb = wordCase(obj.toString(), false, '.');
            return sb.toString();
        }
    };

    // http://unicode.org/faq/casemap_charprop.html
    public static final NameEval funUpperCase = new NameEval() {
        @Override
        public @NotNull String name() {
            return FUN$UPPER_CASE;
        }

        @Override
        public @NotNull String info() {
            return "字符串变UPPERCASE";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            String txt = obj.toString();
            if (arg.length > 1) {
                Locale loc = Locale.forLanguageTag((String) arg[0]);
                return txt.toUpperCase(loc);
            } else {
                return txt.toUpperCase();
            }
        }
    };

    public static final NameEval funLowerCase = new NameEval() {
        @Override
        public @NotNull String name() {
            return FUN$UPPER_CASE;
        }

        @Override
        public @NotNull String info() {
            return "字符串变lowercase";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            String txt = obj.toString();
            if (arg.length > 1) {
                Locale loc = Locale.forLanguageTag((String) arg[0]);
                return txt.toLowerCase(loc);
            } else {
                return txt.toLowerCase();
            }
        }
    };

    //
    private static StringBuilder wordCase(String str, boolean isUpper, char split) {
        StringBuilder sb = new StringBuilder(str.length() + 16);
        int flag = 0;
        char last = 0;
        for (int i = 0, len = str.length(); i < len; i++) {
            char c = str.charAt(i);
            if (isJavaIdentifierPart(c)) {
                if (c == '_' || c == '-') {
                    flag = 1;
                } else {
                    final char c1 = isUpper ? toUpperCase(c) : toLowerCase(c);
                    if (flag == 0) {
                        sb.append(c1);
                    } else if (flag == 1) {
                        sb.append(split);
                        sb.append(c1);
                    } else {
                        if (isUpperCase(c) && isLowerCase(last)) {
                            sb.append(split);
                        }
                        sb.append(c1);
                    }
                    flag = -1;
                }
            } else {
                if (flag != 0) flag = 1;
            }
            last = c;
        }

        return sb;
    }

    private static StringBuilder wordCase(String str, boolean up1st) {
        StringBuilder sb = new StringBuilder(str.length() + 16);
        int flag = 0;
        char last = 0;
        for (int i = 0, len = str.length(); i < len; i++) {
            char c = str.charAt(i);
            if (isJavaIdentifierPart(c)) {
                if (c == '_' || c == '-') {
                    flag = 1;
                } else {
                    if (flag == 0) {
                        sb.append(up1st ? toUpperCase(c) : toLowerCase(c));
                    } else if (flag == 1) {
                        sb.append(toUpperCase(c));
                    } else {
                        if (isUpperCase(last)) {
                            sb.append(toLowerCase(c));
                        } else {
                            sb.append(c);
                        }
                    }
                    flag = -1;
                }
            } else {
                if (flag != 0) flag = 1;
            }
            last = c;
        }

        return sb;
    }
}
