package pro.fessional.meepo.poof.impl.map;

import org.jetbrains.annotations.Nullable;
import pro.fessional.meepo.bind.Const;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2020-11-09
 */
public class AttrGetter {

    private static final ConcurrentHashMap<Key, Member> MEMBER = new ConcurrentHashMap<>();

    public static String[] nav(String expr) {
        int p1 = expr.indexOf(Const.OBJ$NAVIGATOR);
        if (p1 > 0) {
            ArrayList<String> arr = new ArrayList<>();
            int p0 = 0;
            do {
                arr.add(expr.substring(p0, p1));
                p0 = p1 + 1;
                p1 = expr.indexOf(Const.OBJ$NAVIGATOR, p0);
                if (p1 < 0) {
                    arr.add(expr.substring(p0));
                }
            } while (p1 > 0);
            return arr.toArray(Const.ARR$EMPTY_STRING);
        } else {
            return Const.ARR$EMPTY_STRING;
        }
    }

    public static Object get(Object ctx, String expr, String[] part) {

        // 整取
        Object obj = byMap(ctx, expr);
        if (obj != null) return obj;

        String k2 = expr;
        if (part != null && part.length > 0) {
            int lst = part.length - 1;

            for (int i = 0; i < lst; i++) {
                String k1 = part[i];
                obj = byMap(ctx, k1);
                if (obj == null) {
                    obj = byBean(ctx, k1);
                }
                if (obj == null) {
                    return null;
                } else {
                    ctx = obj;
                }
            }

            k2 = part[lst];
        }

        obj = byMap(ctx, k2);
        if (obj == null) {
            obj = byBean(ctx, k2);
        }

        return obj;
    }

    @Nullable
    private static Object byMap(Object ctx, String key) {
        Object obj;
        if (ctx instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) ctx;
            obj = map.get(key);
        } else {
            obj = null;
        }
        return obj;
    }

    @Nullable
    private static Object byBean(Object ctx, String key) {
        Object obj;
        final Class<?> clz = ctx.getClass();
        Object ref = MEMBER.computeIfAbsent(new Key(clz, key), k -> {
            for (Method md : clz.getMethods()) {
                if (md.getParameterCount() != 0) continue;

                final String nm = md.getName();
                if (nm.equals(key)
                        || isGetter(nm, "get", key)
                        || isGetter(nm, "is", key)
                        || isGetter(nm, "has", key)
                ) {
                    return md;
                }
            }
            for (Field fd : clz.getFields()) {
                if (fd.getName().equals(key)) {
                    return fd;
                }
            }
            return null;
        });

        try {
            if (ref instanceof Method) {
                obj = ((Method) ref).invoke(ctx);
            } else if (ref instanceof Field) {
                obj = ((Field) ref).get(ctx);
            } else {
                obj = null;
            }
        } catch (Exception e) {
            throw new IllegalStateException("failed to reflect ok=" + key, e);
        }
        return obj;
    }

    private static boolean isGetter(String name, String pref, String prop) {
        final int of = pref.length(), pl = prop.length(), ln = name.length();
        return ln - of == pl
                && name.startsWith(pref)
                && name.charAt(of) == Character.toUpperCase(prop.charAt(0))
                && name.regionMatches(of + 1, prop, 1, pl - 1);
    }

    private static class Key {
        private final Class<?> claz;
        private final String attr;

        public Key(Class<?> claz, String attr) {
            this.claz = claz;
            this.attr = attr;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return Objects.equals(claz, key.claz) &&
                    Objects.equals(attr, key.attr);
        }

        @Override
        public int hashCode() {
            return Objects.hash(claz, attr);
        }
    }
}
