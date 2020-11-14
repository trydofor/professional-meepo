package pro.fessional.meepo.poof.impl.map;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.bind.Const;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.poof.RnaWarmed;
import pro.fessional.meepo.poof.impl.java.JavaEval;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static pro.fessional.meepo.bind.Const.ENGINE$MAP;

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
        RnaWarmed navi = wit.next();
        Object obj = null;
        boolean ie = false;
        try {
            obj = MapHelper.get(ctx, navi.expr, navi.getTypedWork());

            if (obj == null) {
                obj = System.getProperty(navi.expr);
            }

            if (obj == null) {
                obj = System.getenv(navi.expr);
            }

            if (obj instanceof Supplier) {
                obj = ((Supplier<?>) obj).get();
            } else if (obj instanceof JavaEval) {
                obj = ((JavaEval) obj).eval(ctx, null, Const.ARR$EMPTY_STRING);
            } else if (obj instanceof Function) {
                @SuppressWarnings("unchecked")
                Function<Object, Object> fun = (Function<Object, Object>) obj;
                obj = fun.apply(null);
            }

            while (wit.hasNext()) {
                RnaWarmed pip = wit.next();
                Object cmd = ctx.get(pip.expr);
                String[] arg = pip.getTypedWork();
                if (cmd instanceof JavaEval) {
                    obj = ((JavaEval) cmd).eval(ctx, obj, arg);
                } else if (cmd instanceof Function) {
                    @SuppressWarnings("unchecked")
                    Function<Object, Object> fun = (Function<Object, Object>) cmd;
                    obj = fun.apply(obj);
                } else {
                    ie = true;
                    throw new IllegalStateException("failed to get cmd, expr=" + pip);
                }
            }
        } catch (Throwable t) {
            if (mute) {
                logger.warn("mute failed-eval " + expr, t);
            } else {
                if (ie) {
                    throw t;
                } else {
                    throw new IllegalStateException(expr.toString(), t);
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
