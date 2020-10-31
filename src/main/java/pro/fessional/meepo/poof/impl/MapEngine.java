package pro.fessional.meepo.poof.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.poof.RnaEngine;

import java.util.Map;

import static pro.fessional.meepo.bind.Const.ENGINE_MAP;
import static pro.fessional.meepo.bind.Const.TXT_EMPTY;

/**
 * 依次从context，System.getProperty 和System.getenv 取值
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class MapEngine implements RnaEngine {

    private static final String[] TYPE = {ENGINE_MAP};

    @Override
    public @NotNull String[] type() {
        return TYPE;
    }

    @Override
    public @NotNull Object eval(@NotNull String type, @NotNull String expr, @NotNull Map<String, Object> ctx, boolean mute) {
        if (mute) return TXT_EMPTY;

        Object v = ctx.get(expr);
        if (v != null) {
            return v;
        }
        String s = System.getProperty(expr);
        if (s != null) {
            return s;
        }

        String e = System.getenv(expr);
        if (e != null) {
            return e;
        }

        return expr;
    }

    @Override
    public @NotNull RnaEngine fork() {
        return this;
    }
}
