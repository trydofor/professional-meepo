package pro.fessional.meepo.util;

import pro.fessional.meepo.bind.wow.Clop;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2020-11-01
 */
public class Dent {

    private static final Map<Integer, char[]> PADDING = new HashMap<>();

    public static char[] chars(String text, Clop clop) {
        char[] chs = new char[clop.length];
        text.getChars(clop.start, clop.until, chs, 0);
        return chs;
    }

    public static char[] chars(String text, int start, int end) {
        char[] chs = new char[end - start];
        text.getChars(start, end, chs, 0);
        return chs;
    }

    public static void pend(Writer out, char[] str) {
        try {
            out.write(str);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void dent(Writer out, int level) {
        char[] chr = PADDING.computeIfAbsent(level, s -> {
            boolean ng = false;
            if (s < 0) {
                s = -s;
                ng = true;
            }
            StringBuilder sb = new StringBuilder(2 * s);
            for (int i = 0; i < s; i++) {
                if (ng) {
                    sb.append("#-");
                } else {
                    sb.append("|-");
                }
            }
            char[] cs = new char[sb.length()];
            sb.getChars(0, cs.length, cs, 0);
            return cs;
        });

        pend(out, chr);
    }

    public static void line(Writer buff, char[] str) {
        try {
            if (str == null) return;
            for (char c : str) {
                line(buff, c);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void line(Writer buff, CharSequence str, Clop edg) {
        line(buff, str, edg.start, edg.until);
    }

    public static void line(Writer buff, CharSequence str, int off, int end) {
        try {
            if (str == null) return;
            for (int i = off; i < end; i++) {
                line(buff, str.charAt(i));
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void line(Writer buff, char c) throws IOException {
        if (c == '\\') {
            buff.append("\\\\");
        } else if (c == '\r') {
            buff.append("\\r");
        } else if (c == '\n') {
            buff.append("\\n");
        } else if (c == '\t') {
            buff.append("\\t");
        } else {
            buff.append(c);
        }
    }

    public static void line(Writer buff, CharSequence str) {
        if (str == null) return;
        line(buff, str, 0, str.length());
    }

    public static int left(String text, int end) {
        if (end <= 0) return 0;
        int eg = Seek.seekPrevEdge(text, end);
        return eg >= 0 ? end - eg : 0;
    }

    public static void left(Writer buf, int lft) {
        try {
            for (int i = 0; i < lft; i++) {
                buf.append(' ');
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void left(Writer buf, int lft, Object val) {
        if (val == null) return;

        try {
            Class<?> clz = val.getClass();
            if (Collection.class.isAssignableFrom(clz)) {
                boolean f = false;
                for (Object o : ((Collection<?>) val)) {
                    if (o == null) continue;
                    if (f) {
                        left(buf, lft);
                    } else {
                        f = true;
                    }
                    buf.append(o.toString());
                }
            } else if (clz.isArray()) {
                boolean f = false;
                for (int i = 0, len = Array.getLength(val); i < len; i++) {
                    Object o = Array.get(val, i);
                    if (o == null) continue;

                    if (f) {
                        left(buf, lft);
                    } else {
                        f = true;
                    }
                    buf.append(o.toString());
                }
            } else {
                buf.append(val.toString());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
