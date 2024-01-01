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
import static pro.fessional.meepo.eval.FunEnv.FUN$BIG_KEBAB_1;
import static pro.fessional.meepo.eval.FunEnv.FUN$BIG_SNAKE;
import static pro.fessional.meepo.eval.FunEnv.FUN$BIG_SNAKE_1;
import static pro.fessional.meepo.eval.FunEnv.FUN$CAMEL_CASE;
import static pro.fessional.meepo.eval.FunEnv.FUN$DOT_CASE;
import static pro.fessional.meepo.eval.FunEnv.FUN$KEBAB_CASE;
import static pro.fessional.meepo.eval.FunEnv.FUN$KEBAB_CASE_1;
import static pro.fessional.meepo.eval.FunEnv.FUN$LOWER_CASE;
import static pro.fessional.meepo.eval.FunEnv.FUN$PASCAL_CASE;
import static pro.fessional.meepo.eval.FunEnv.FUN$PASCAL_CASE_1;
import static pro.fessional.meepo.eval.FunEnv.FUN$SNAKE_CASE;
import static pro.fessional.meepo.eval.FunEnv.FUN$SNAKE_CASE_1;
import static pro.fessional.meepo.eval.FunEnv.FUN$UPPER_CASE;

/**
 * @author trydofor
 * @since 2021-01-04
 */
public class Case {

    public static final NameEval funCamelCase = new NameEval() {
        @Override
        public @NotNull String[] name() {
            return new String[]{FUN$CAMEL_CASE};
        }

        @Override
        public @NotNull String info() {
            return "Format String to camelCase";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            return camelCase(obj.toString());
        }
    };

    public static final NameEval funPascalCase = new NameEval() {
        @Override
        public @NotNull String[] name() {
            return new String[]{FUN$PASCAL_CASE, FUN$PASCAL_CASE_1};
        }

        @Override
        public @NotNull String info() {
            return "Format String to PascalCase";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            return pascalCase(obj.toString());
        }
    };

    public static final NameEval funSnakeCase = new NameEval() {
        @Override
        public @NotNull String[] name() {
            return new String[]{FUN$SNAKE_CASE, FUN$SNAKE_CASE_1};
        }

        @Override
        public @NotNull String info() {
            return "Format String to snake_case";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            return snakeCase(obj.toString(), caseType(arg));
        }
    };

    public static final NameEval funBigSnake = new NameEval() {
        @Override
        public @NotNull String[] name() {
            return new String[]{FUN$BIG_SNAKE, FUN$BIG_SNAKE_1};
        }

        @Override
        public @NotNull String info() {
            return "Format String to BIG_SNAKE";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            return snakeCase(obj.toString(), Type.Upper);
        }
    };

    public static final NameEval funKebabCase = new NameEval() {
        @Override
        public @NotNull String[] name() {
            return new String[]{FUN$KEBAB_CASE, FUN$KEBAB_CASE_1};
        }

        @Override
        public @NotNull String info() {
            return "Format String to kebab-case";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            return kebabCase(obj.toString(), caseType(arg));
        }
    };

    public static final NameEval funBigKebab = new NameEval() {
        @Override
        public @NotNull String[] name() {
            return new String[]{FUN$BIG_KEBAB, FUN$BIG_KEBAB_1};
        }

        @Override
        public @NotNull String info() {
            return "Format String to BIG-KEBAB";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            return kebabCase(obj.toString(), Type.Upper);
        }
    };

    public static final NameEval funDotCase = new NameEval() {
        @Override
        public @NotNull String[] name() {
            return new String[]{FUN$DOT_CASE};
        }

        @Override
        public @NotNull String info() {
            return "Format String to dot.case";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            return dotCase(obj.toString(), caseType(arg));
        }
    };

    // http://unicode.org/faq/casemap_charprop.html
    public static final NameEval funUpperCase = new NameEval() {
        @Override
        public @NotNull String[] name() {
            return new String[]{FUN$UPPER_CASE};
        }

        @Override
        public @NotNull String info() {
            return "Format String to UPPERCASE";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            String txt = obj.toString();
            if (arg.length > 1) {
                Locale loc = Locale.forLanguageTag(arg[0].toString());
                return txt.toUpperCase(loc);
            }
            else {
                return txt.toUpperCase();
            }
        }
    };

    public static final NameEval funLowerCase = new NameEval() {
        @Override
        public @NotNull String[] name() {
            return new String[]{FUN$LOWER_CASE};
        }

        @Override
        public @NotNull String info() {
            return "Format String to lowercase";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) return "";
            String txt = obj.toString();
            if (arg.length > 1) {
                Locale loc = Locale.forLanguageTag(arg[0].toString());
                return txt.toLowerCase(loc);
            }
            else {
                return txt.toLowerCase();
            }
        }
    };

    // /////////

    /**
     * lowercase 1st char, camelCase
     */
    public static String camelCase(String str) {
        return wordCase(str, false);
    }

    /**
     * Uppercase 1st char, PascalCase
     */
    public static String pascalCase(String str) {
        return wordCase(str, true);
    }

    /**
     * underscore(_) delimited.
     */
    private static String snakeCase(String str, Type type) {
        return wordCase(str, type, '_');
    }

    /**
     * minus sign(-) delimited.
     */
    private static String kebabCase(String str, Type type) {
        return wordCase(str, type, '-');
    }

    /**
     * dot sign(.) delimited.
     */
    private static String dotCase(String str, Type type) {
        return wordCase(str, type, '.');
    }

    public enum Type {
        /**
         * All Uppercase
         */
        Upper,
        /**
         * All Lowercase
         */
        Lower,
        /**
         * Keep the original shape
         */
        Keep
    }

    /**
     * parse string to words and use the given delimiter to format
     *
     * @param str   The string to format
     * @param type  Case type
     * @param split the delimiter to split the word
     * @return formatted string
     */
    public static String wordCase(String str, Type type, char split) {
        StringBuilder sb = new StringBuilder(str.length() + 16);
        int flag = 0;
        char last = 0;
        for (int i = 0, len = str.length(); i < len; i++) {
            char c = str.charAt(i);
            if (isJavaIdentifierPart(c)) {
                if (c == '_' || c == '-') {
                    flag = 1;
                }
                else {
                    final char c1;
                    if (type == Type.Upper) {
                        c1 = toUpperCase(c);
                    }
                    else if (type == Type.Lower) {
                        c1 = toLowerCase(c);
                    }
                    else {
                        c1 = c;
                    }
                    if (flag == 0) {
                        sb.append(c1);
                    }
                    else if (flag == 1) {
                        sb.append(split);
                        sb.append(c1);
                    }
                    else {
                        if (isUpperCase(c) && isLowerCase(last)) {
                            sb.append(split);
                        }
                        sb.append(c1);
                    }
                    flag = -1;
                }
            }
            else {
                if (flag != 0) flag = 1;
            }
            last = c;
        }

        return sb.toString();
    }

    /**
     * convert to camelCase or PascalCase
     *
     * @param str    The string to format
     * @param pascal Whether 1st char Uppercase
     * @return formatted string
     */
    public static String wordCase(String str, boolean pascal) {
        StringBuilder sb = new StringBuilder(str.length() + 16);
        int flag = 0;
        char last = 0;
        for (int i = 0, len = str.length(); i < len; i++) {
            char c = str.charAt(i);
            if (isJavaIdentifierPart(c)) {
                if (c == '_' || c == '-') {
                    flag = 1;
                }
                else {
                    if (flag == 0) {
                        sb.append(pascal ? toUpperCase(c) : toLowerCase(c));
                    }
                    else if (flag == 1) {
                        sb.append(toUpperCase(c));
                    }
                    else {
                        if (isUpperCase(last)) {
                            sb.append(toLowerCase(c));
                        }
                        else {
                            sb.append(c);
                        }
                    }
                    flag = -1;
                }
            }
            else {
                if (flag != 0) flag = 1;
            }
            last = c;
        }

        return sb.toString();
    }

    ///////
    private static Type caseType(Object[] arg) {
        if (arg != null && arg.length > 0 && arg[0] != null) {
            String str = arg[0].toString();
            if ("upper".equalsIgnoreCase(str)) {
                return Type.Upper;
            }
            else if ("keep".equalsIgnoreCase(str)) {
                return Type.Keep;
            }
        }
        return Type.Lower;
    }
}
