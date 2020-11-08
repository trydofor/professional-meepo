package pro.fessional.meepo.bind.wow;

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

    /**
     * 按空白解析命令行，支持引号块和转义 "one\" arg"
     *
     * @param line 参数行
     * @return 解析后命令行
     */
    @NotNull
    public static List<String> parseArgs(String line) {
        if (line == null || line.isEmpty()) return Collections.emptyList();
        List<String> args = new ArrayList<>();
        int len = line.length();
        StringBuilder buf = new StringBuilder(len);
        char qto = 0;
        boolean esc = false;
        for (int i = 0; i < len; i++) {
            char c = line.charAt(i);
            if (c == '\\') {
                if (esc) {
                    buf.append(c);
                    esc = false;
                } else {
                    esc = true;
                }
            } else if (c == '"' || c == '\'') {
                if (esc) {
                    buf.append(c);
                    esc = false;
                } else {
                    if (qto == 0) {
                        qto = c;
                    } else {
                        if (qto == c) {
                            args.add(buf.toString());
                            buf.setLength(0);
                            qto = 0;
                        } else {
                            buf.append(c);
                        }
                    }
                }
            } else if (c == ' ' || c == '\t') {
                if (qto > 0) {
                    buf.append(c);
                } else {
                    if (buf.length() > 0) {
                        args.add(buf.toString());
                        buf.setLength(0);
                    }
                }
            } else {
                if (esc) {
                    buf.append('\\');
                    esc = false;
                }
                buf.append(c);
            }
        }

        if (buf.length() > 0) {
            if (esc) {
                buf.append('\\');
            }
            args.add(buf.toString());
        }

        return args;
    }
}
