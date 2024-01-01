package pro.fessional.meepo.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static pro.fessional.meepo.bind.Const.ARG$BOOL_FALSE;
import static pro.fessional.meepo.bind.Const.ARG$BOOL_TRUE;
import static pro.fessional.meepo.bind.Const.ARG$NUMBER_DSUF;
import static pro.fessional.meepo.bind.Const.ARG$NUMBER_FSUF;
import static pro.fessional.meepo.bind.Const.ARG$NUMBER_LSUF;
import static pro.fessional.meepo.bind.Const.ARG$NUMBER_NSUF;
import static pro.fessional.meepo.bind.Const.ARG$NUMBER_REGEX;

/**
 * @author trydofor
 * @since 2020-11-03
 */
public class Eval {

    /**
     * <pre>
     * Any of the following is as false
     * - `false` of boolean
     * - `null` of Object
     * - `NaN` or abs in `0.0000001` of Number as double
     * - `empty` of String, Array, Collection
     * </pre>
     *
     * @param obj Object
     * @return as false
     */
    public static boolean asFalse(Object obj) {
        final boolean f;
        if (obj == null) {
            f = true;
        }
        else if (obj instanceof Boolean) {
            f = !((Boolean) obj);
        }
        else if (obj instanceof CharSequence) {
            f = ((CharSequence) obj).length() == 0;
        }
        else if (obj instanceof Number) {
            double dv = ((Number) obj).doubleValue();
            f = Double.isNaN(dv) || (dv > -0.000000001D && dv < 0.000000001D);
        }
        else if (obj instanceof Map) {
            f = ((Map<?, ?>) obj).isEmpty();
        }
        else if (obj instanceof Collection) {
            f = ((Collection<?>) obj).isEmpty();
        }
        else if (obj.getClass().isArray()) {
            f = Array.getLength(obj) == 0;
        }
        else {
            f = false;
        }
        return f;
    }

    public static class RefStr implements CharSequence {

        private final String string;

        public RefStr(String string) {
            this.string = string;
        }

        @Override public int length() {
            return string.length();
        }

        @Override public char charAt(int index) {
            return string.charAt(index);
        }

        @Override public CharSequence subSequence(int start, int end) {
            return string.subSequence(start, end);
        }

        @NotNull
        @Override public String toString() {
            return string;
        }
    }

    public abstract static class ArgType<T> {

        /**
         * Auto parsed into supported objects: Number, Boolean, RefStr
         */
        public static final ArgType<Object> Obj = new ArgType<Object>() {
            @Override
            public Object transform(String str) {
                // boolean
                Boolean bol = parseBoolean(str);
                if (bol != null) return bol;

                Number num = parseNumber(str);
                if (num != null) return num;

                //
                return Ref.transform(str);
            }

            @Override
            public ArgType<? extends CharSequence> forceStr() {
                return Str;
            }

            private Boolean parseBoolean(String str) {
                if (ARG$BOOL_TRUE.equals(str)) return Boolean.TRUE;
                if (ARG$BOOL_FALSE.equals(str)) return Boolean.FALSE;
                return null;
            }

            private Number parseNumber(String str) {
                final Matcher mtc = ARG$NUMBER_REGEX.matcher(str);
                if (!mtc.find()) return null;

                final String bd = mtc.group(2);
                final StringBuilder sb = new StringBuilder(bd.length() + 1);
                if ("-".equals(mtc.group(1))) sb.append("-");
                for (int i = 0, len = bd.length(); i < len; i++) {
                    char c = bd.charAt(i);
                    if (c != ',' && c != '_') {
                        sb.append(c);
                    }
                }

                final String body = sb.toString();
                final String type = mtc.group(3);

                try {
                    if (ARG$NUMBER_FSUF.equalsIgnoreCase(type)) {
                        return Float.valueOf(body);
                    }
                    if (ARG$NUMBER_DSUF.equalsIgnoreCase(type)) {
                        return Double.valueOf(body);
                    }
                    if (ARG$NUMBER_LSUF.equalsIgnoreCase(type)) {
                        return Long.valueOf(body);
                    }
                    if (ARG$NUMBER_NSUF.equalsIgnoreCase(type)) {
                        return new BigDecimal(body);
                    }

                    if (body.contains(".")) {
                        return Float.valueOf(body);
                    }
                    else {
                        return Integer.valueOf(body);
                    }
                }
                catch (NumberFormatException e) {
                    return null;
                }
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

        public static final ArgType<RefStr> Ref = new ArgType<RefStr>() {
            @Override
            public RefStr transform(String str) {
                return new RefStr(str);
            }

            @Override
            public ArgType<? extends RefStr> forceStr() {
                return this;
            }
        };

        private ArgType() {
        }

        public abstract T transform(String str);

        public abstract ArgType<? extends T> forceStr();
    }

    /**
     * Parses command line by whitespace, supports quote blocks, escaping "one\" arg" and number parsing.
     * Supports: String, Long, Integer, Double, Float, Boolean.
     *
     * @param line command line
     * @param type type to parse
     * @param <T>  type of command
     * @return command
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
                }
                else {
                    esc = true;
                }
            }
            else if (c == '"' || c == '\'') {
                if (esc) {
                    buff.append(c);
                    esc = false;
                }
                else {
                    if (qto == 0) {
                        qto = c;
                    }
                    else {
                        if (qto == c) {
                            typedAdd(args, buff, type.forceStr(), false);
                            buff.setLength(0);
                            qto = 0;
                        }
                        else {
                            buff.append(c);
                        }
                    }
                }
            }
            else if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                if (qto > 0) {
                    buff.append(c);
                }
                else {
                    if (buff.length() > 0) {
                        typedAdd(args, buff, type);
                        buff.setLength(0);
                    }
                }
            }
            else {
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
            }
            else {
                if (c == spt) {
                    if (cnt % 2 == 0) {
                        buf.setCharAt(buf.length() - 1, c);
                    }
                    else {
                        typedAdd(arr, buf, typ);
                        buf.setLength(0);
                        cnt = 1;
                    }
                }
                else {
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
        typedAdd(list, buff, type, true);
    }

    private static <T> void typedAdd(List<? super T> list, StringBuilder buff, ArgType<T> type, boolean plain) {
        int[] ps = Seek.trimBlank(buff, 0, buff.length());
        if (plain && ps[1] <= ps[0]) return;
        String str = buff.substring(ps[0], ps[1]);
        list.add(type.transform(str));
    }
}
