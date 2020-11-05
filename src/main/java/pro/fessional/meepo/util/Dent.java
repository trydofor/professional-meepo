package pro.fessional.meepo.util;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2020-11-01
 */
public class Dent {

    private static final Map<Integer, String> padding = new HashMap<>();

    public static void pend(Appendable out, CharSequence str) {
        try {
            out.append(str);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void pend(Appendable out, CharSequence str, int off, int len) {
        try {
            out.append(str, off, len);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String dent(int level) {
        return padding.computeIfAbsent(level, s -> {
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
            return sb.toString();
        });
    }

    public static void line(Appendable buff, String str, int off, int end) {
        try {
            if (str == null) return;
            for (int i = off; i < end; i++) {
                char c = str.charAt(i);
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
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void line(Appendable buff, String str) {
        if (str == null) return;
        line(buff, str, 0, str.length());
    }

    public static int left(String text, int end) {
        if (end <= 0) return 0;
        int eg = Seek.seekPrevEdge(text, end);
        return eg >= 0 ? end - eg : 0;
    }

    public static void left(Appendable buf, int lft) {
        try {
            for (int i = 0; i < lft; i++) {
                buf.append(' ');
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void left(Appendable buf, int lft, Object val) {
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
