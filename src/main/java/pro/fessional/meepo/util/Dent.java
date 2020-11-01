package pro.fessional.meepo.util;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * @author trydofor
 * @since 2020-11-01
 */
public class Dent {

    public static int left(String text, int end) {
        if (end <= 0) return 0;
        int eg = Seek.seekPrevEdge(text, end);
        return eg >= 0 ? end - eg : 0;
    }

    public static void left(StringBuilder buf, int lft) {
        for (int i = 0; i < lft; i++) {
            buf.append(' ');
        }
    }

    public static void left(StringBuilder buf, int lft, Object val) {
        if (val == null) return;
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
    }
}
