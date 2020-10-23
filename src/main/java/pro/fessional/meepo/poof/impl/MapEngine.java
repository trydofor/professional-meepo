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
    public @NotNull String eval(@NotNull String type, @NotNull String expr, @NotNull Map<String, Object> ctx, boolean quiet) {
        Object v = ctx.get(expr);
        if (v != null) {
            return quiet ? TXT_EMPTY : v.toString();
        }
        String s = System.getProperty(expr);
        if (s != null) {
            return quiet ? TXT_EMPTY : s;
        }

        String e = System.getenv(expr);
        if (e != null) {
            return quiet ? TXT_EMPTY : e;
        }

        return quiet ? TXT_EMPTY : expr;
    }

    @Override
    public @NotNull RnaEngine fork() {
        return this;
    }
}
