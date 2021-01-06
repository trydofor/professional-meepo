package pro.fessional.meepo.poof.impl.map;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.eval.JavaEval;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.poof.RnaWarmed;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static pro.fessional.meepo.bind.Const.ARR$EMPTY_OBJECT;
import static pro.fessional.meepo.bind.Const.ENGINE$MAP;
import static pro.fessional.meepo.eval.FunEnv.KEY$PREFIX;
import static pro.fessional.meepo.poof.impl.map.MapHelper.KIND_FUNC;

/**
 * 依次从context，System.getProperty 和System.getenv 取值
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class MapEngine implements RnaEngine {

    private static final Logger logger = LoggerFactory.getLogger(MapEngine.class);

    private static final String[] TYPE = {ENGINE$MAP};

    @Override
    public @NotNull String[] type() {
        return TYPE;
    }

    @Override
    public @NotNull RnaWarmed warm(@NotNull String type, @NotNull String expr) {
        return MapHelper.warm(type, expr);
    }

    @Override
    public Object eval(@NotNull Map<String, Object> ctx, @NotNull RnaWarmed expr, boolean mute) {
        final ArrayList<RnaWarmed> work = expr.getTypedWork();
        Iterator<RnaWarmed> wit = work.iterator();
        RnaWarmed curr = expr;
        RnaWarmed navi = wit.next();
        Object obj = null;
        boolean ie = false;
        try {
            curr = navi;
            obj = MapHelper.get(ctx, navi.expr, navi.getTypedWork());

            if (navi.kind == KIND_FUNC) {
                if (!(obj instanceof JavaEval || obj instanceof Function) && !navi.expr.startsWith(KEY$PREFIX)) {
                    obj = MapHelper.get(ctx, KEY$PREFIX + navi.expr, navi.getTypedWork());
                }
            } else {
                if (obj == null) obj = System.getProperty(navi.expr);
                if (obj == null) obj = System.getenv(navi.expr);
            }

            if (obj instanceof Supplier) {
                obj = ((Supplier<?>) obj).get();
            } else if (obj instanceof JavaEval) {
                Object[] args = navi.kind == KIND_FUNC ? navi.getTypedWork() : ARR$EMPTY_OBJECT;
                obj = ((JavaEval) obj).eval(ctx, null, args);
            } else if (obj instanceof Function) {
                @SuppressWarnings("unchecked")
                Function<Object, Object> fun = (Function<Object, Object>) obj;
                Object arg = navi.kind == KIND_FUNC ? navi.getTypedWork() : null;
                obj = fun.apply(arg);
            }

            while (wit.hasNext()) { // function
                RnaWarmed pip = wit.next();
                curr = pip;
                Object[] arg = pip.getTypedWork();
                String key = pip.expr;
                // 自定义或简化模式，function 可能不带前缀'fun:'
                while (true) {
                    Object cmd = ctx.get(key);
                    if (cmd instanceof JavaEval) {
                        obj = ((JavaEval) cmd).eval(ctx, obj, arg);
                    } else if (cmd instanceof Function) {
                        @SuppressWarnings("unchecked")
                        Function<Object, Object> fun = (Function<Object, Object>) cmd;
                        obj = fun.apply(obj);
                    } else {
                        if (key.startsWith(KEY$PREFIX)) {
                            ie = true;
                            throw new IllegalStateException("failed to get cmd, expr=" + pip);
                        } else {
                            key = KEY$PREFIX + key;
                            continue;
                        }
                    }
                    break;
                }
            }
        } catch (Throwable t) {
            if (mute) {
                logger.warn("mute failed-eval " + expr, t);
            } else {
                if (ie) {
                    throw t;
                } else {
                    throw new IllegalStateException(expr + ", current=" + curr, t);
                }
            }
        }

        return obj;
    }

    @Override
    public @NotNull RnaEngine fork() {
        return this;
    }
}
