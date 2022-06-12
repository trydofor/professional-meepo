package pro.fessional.meepo.poof.impl.map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.meepo.bind.Const;
import pro.fessional.meepo.poof.RnaWarmed;
import pro.fessional.meepo.util.Eval;
import pro.fessional.meepo.util.Eval.ArgType;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static pro.fessional.meepo.bind.Const.ARR$EMPTY_OBJECT;
import static pro.fessional.meepo.bind.Const.ARR$EMPTY_STRING;

/**
 * @author trydofor
 * @since 2020-11-09
 */
public class MapHelper {

    private static final ConcurrentHashMap<Key, Member> MEMBER = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String[]> NAVEXP = new ConcurrentHashMap<>();

    public static final int KIND_PURE = 1;
    public static final int KIND_NAVI = 2;
    public static final int KIND_FUNC = 3;

    public static RnaWarmed warm(@NotNull String type, @NotNull String expr) {
        ArrayList<String> pipes = Eval.split(expr, Const.OBJ$PIPE_BAR, Const.OBJ$CHAR_ESC);
        if (pipes.isEmpty()) {
            return RnaWarmed.EMPTY;
        }
        final ArrayList<RnaWarmed> work = new ArrayList<>(pipes.size());

        Iterator<String> it = pipes.iterator();
        String key = it.next();
        ArrayList<String> navi = Eval.split(key, Const.OBJ$NAVI_DOT);
        if (navi.size() > 1) {
            work.add(new RnaWarmed(type, key, navi.toArray(ARR$EMPTY_STRING), KIND_NAVI));
        }
        else {
            List<String> args = Eval.parseArgs(key, ArgType.Str);
            if (args.size() > 1) {
                String cmd = args.get(0);
                String[] arg = args.subList(1, args.size()).toArray(ARR$EMPTY_STRING);
                work.add(new RnaWarmed(type, cmd, arg, KIND_FUNC));
            }
            else {
                work.add(new RnaWarmed(type, key, ARR$EMPTY_STRING, KIND_PURE));
            }
        }

        while (it.hasNext()) {
            List<Object> pipe = Eval.parseArgs(it.next(), ArgType.Obj);
            String cmd = pipe.get(0).toString();
            Object[] arg = ARR$EMPTY_OBJECT;
            if (pipe.size() > 1) {
                arg = pipe.subList(1, pipe.size()).toArray();
            }
            work.add(new RnaWarmed(type, cmd, arg, KIND_FUNC));
        }

        return new RnaWarmed(type, expr, work);
    }

    public static Object arg(Object ctx, CharSequence expr) {
        if (expr instanceof String) {
            return expr;
        }

        final String refs = expr.toString();
        String[] pts = ARR$EMPTY_STRING;
        if (refs.indexOf(Const.OBJ$NAVI_DOT) > 0) {
            pts = NAVEXP.computeIfAbsent(refs, k -> Eval.split(refs, Const.OBJ$NAVI_DOT).toArray(ARR$EMPTY_STRING));
        }
        final Object rt = get(ctx, refs, pts);
        return rt == null ? refs : rt;
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
                }
                else {
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
        }
        else {
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
            }
            else if (ref instanceof Field) {
                obj = ((Field) ref).get(ctx);
            }
            else {
                obj = null;
            }
        }
        catch (Exception e) {
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
        private final int hash;

        public Key(Class<?> claz, String attr) {
            this.claz = claz;
            this.attr = attr;
            this.hash = claz.hashCode() + 31 * attr.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return claz.equals(key.claz) && attr.equals(key.attr);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
