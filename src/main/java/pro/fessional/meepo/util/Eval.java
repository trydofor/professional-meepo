package pro.fessional.meepo.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2020-11-03
 */
public class Eval {

    /**
     * boolean的`false`
     * 对象 `null`
     * Number的double值是`NaN`或在正负`0.0000001`
     * `empty`  空字符串，空数组，空集合
     *
     * @param obj 对象
     * @return 当做false
     */
    public static boolean asFalse(Object obj) {
        final boolean f;
        if (obj == null) {
            f = true;
        } else if (obj instanceof Boolean) {
            f = !((Boolean) obj);
        } else if (obj instanceof CharSequence) {
            f = ((CharSequence) obj).length() == 0;
        } else if (obj instanceof Number) {
            double dv = ((Number) obj).doubleValue();
            f = Double.isNaN(dv) || (dv > -0.000000001D && dv < 0.000000001D);
        } else if (obj instanceof Map) {
            f = ((Map<?, ?>) obj).isEmpty();
        } else if (obj instanceof Collection) {
            f = ((Collection<?>) obj).isEmpty();
        } else if (obj.getClass().isArray()) {
            f = Array.getLength(obj) == 0;
        } else {
            f = false;
        }
        return f;
    }


    public abstract static class ArgType<T> {
        public static final ArgType<Object> Obj = new ArgType<Object>() {
            @Override
            public Object transform(String str) {
                StringBuilder num = new StringBuilder(str.length());
                boolean hasDot = false;
                int p1 = str.length() - 1;
                char lt = str.charAt(p1);
                for (int i = 0, len = str.length(); i < len; i++) {
                    char c = str.charAt(i);
                    if (c == '.') {
                        hasDot = true;
                        num.append(c);
                    } else if (c == '-' || c >= '0' && c <= '9') {
                        num.append(c);
                    } else if (c == '+') {
                        // skip
                    } else {
                        if (p1 == i && c != 'D' && c != 'd' && c != 'L' && c != 'l') {
                            num.setLength(0);
                        }
                        break;
                    }
                }

                if (num.length() > 0) {
                    if (lt == 'D' || lt == 'd') {
                        return Double.valueOf(num.toString());
                    } else if (lt == 'L' || lt == 'l') {
                        return Long.valueOf(num.toString());
                    } else {
                        if (hasDot) {
                            return Float.valueOf(num.toString());
                        } else {
                            return Integer.valueOf(num.toString());
                        }
                    }
                }

                return str;
            }

            @Override
            public ArgType<? extends String> forceStr() {
                return Str;
            }
        };

        public static final ArgType<String> Str = new ArgType<String>() {
            @Override
            public String transform(String str) {
                return str;
            }

            @Override
            public ArgType<? extends String> forceStr() {
                return this;
            }
        };

        private ArgType() {
        }

        public abstract T transform(String str);

        public abstract ArgType<? extends T> forceStr();
    }

    /**
     * 按空白解析命令行，支持引号块和转义 "one\" arg"和数字解析。
     * 支持，String，Long，Integer，Double，Float类型。
     *
     * @param line 参数行
     * @param type 解析类型
     * @return 解析后命令行
     */
    @NotNull
    public static <T> List<T> parseArgs(CharSequence line, ArgType<T> type) {
        if (line == null || line.length() == 0) return Collections.emptyList();
        List<T> args = new ArrayList<>();
        int len = line.length();
        StringBuilder buff = new StringBuilder(len);
        char qto = 0;
        boolean esc = false;

        for (int i = 0; i < len; i++) {
            char c = line.charAt(i);
            if (c == '\\') {
                if (esc) {
                    buff.append(c);
                    esc = false;
                } else {
                    esc = true;
                }
            } else if (c == '"' || c == '\'') {
                if (esc) {
                    buff.append(c);
                    esc = false;
                } else {
                    if (qto == 0) {
                        qto = c;
                    } else {
                        if (qto == c) {
                            typedAdd(args, buff, type.forceStr());
                            buff.setLength(0);
                            qto = 0;
                        } else {
                            buff.append(c);
                        }
                    }
                }
            } else if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                if (qto > 0) {
                    buff.append(c);
                } else {
                    if (buff.length() > 0) {
                        typedAdd(args, buff, type);
                        buff.setLength(0);
                    }
                }
            } else {
                if (esc) {
                    buff.append('\\');
                    esc = false;
                }
                buff.append(c);
            }
        }

        if (buff.length() > 0) {
            if (esc) {
                buff.append('\\');
            }
            typedAdd(args, buff, type);
        }

        return args;
    }

    public static ArrayList<String> split(CharSequence text, char spt) {
        return split(text, spt, '\0');
    }

    public static ArrayList<String> split(CharSequence text, char spt, char esc) {
        ArrayList<String> arr = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        int cnt = 1;
        ArgType<String> typ = ArgType.Str;
        for (int i = 0, len = text.length(); i < len; i++) {
            char c = text.charAt(i);
            if (c == esc) {
                cnt++;
                buf.append(c);
            } else {
                if (c == spt) {
                    if (cnt % 2 == 0) {
                        buf.setCharAt(buf.length() - 1, c);
                    } else {
                        typedAdd(arr, buf, typ);
                        buf.setLength(0);
                        cnt = 1;
                    }
                } else {
                    cnt = 1;
                    buf.append(c);
                }
            }
        }
        if (buf.length() > 0) {
            typedAdd(arr, buf, typ);
        }
        return arr;
    }

    private static <T> void typedAdd(List<? super T> list, StringBuilder buff, ArgType<T> type) {
        int[] ps = Seek.trimBlank(buff, 0, buff.length());
        if (ps[1] <= ps[0]) return;
        String str = buff.substring(ps[0], ps[1]);
        list.add(type.transform(str));
    }
}
