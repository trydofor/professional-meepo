package pro.fessional.meepo.bind.wow;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private static final ConcurrentHashMap<String, Object> methods = new ConcurrentHashMap<>();

    public static Object naviGet(Object ctx, String key, char nav) {
        if (ctx == null || key == null) return null;

        int p0 = key.indexOf(nav);
        final String ok;
        if (p0 < 0 || p0 + 1 == key.length()) {
            ok = key;
            key = null;
        } else {
            ok = key.substring(0, p0);
            key = key.substring(p0 + 1);
        }

        final Object rt;
        if (ctx instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) ctx;
            rt = map.get(ok);
        } else {
            final Class<?> clz = ctx.getClass();
            Object ref = methods.computeIfAbsent(clz.getName() + nav + ok, k -> {
                for (Method md : clz.getMethods()) {
                    if (md.getParameterCount() != 0) continue;
                    String nm = md.getName();
                    if (nm.equals(ok)) {
                        return md;
                    } else if (nm.startsWith("get")
                            && nm.charAt(3) == Character.toUpperCase(ok.charAt(0))
                            && nm.regionMatches(4, ok, 1, ok.length() - 1)) {
                        return md;
                    } else if (nm.startsWith("is")
                            && nm.charAt(2) == Character.toUpperCase(ok.charAt(0))
                            && nm.regionMatches(3, ok, 1, ok.length() - 1)) {
                        return md;
                    }
                }
                for (Field fd : clz.getFields()) {
                    if (fd.getName().equals(ok)) {
                        return fd;
                    }
                }
                return null;
            });

            try {
                if (ref instanceof Method) {
                    rt = ((Method) ref).invoke(ctx);
                } else if (ref instanceof Field) {
                    rt = ((Field) ref).get(ctx);
                } else {
                    rt = null;
                }
            } catch (Exception e) {
                throw new IllegalStateException("failed to reflect ok=" + ok, e);
            }
        }


        if (key == null) {
            return rt;
        } else {
            return naviGet(rt, key, nav);
        }
    }
}
