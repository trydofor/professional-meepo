package pro.fessional.meepo.poof.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.poof.RnaWarmed;

import java.util.Map;

import static pro.fessional.meepo.bind.Const.ENGINE$RAW;

/**
 * 依次从context，System.getProperty 和System.getenv 取值
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class RawEngine implements RnaEngine {

    private static final String[] TYPE = {ENGINE$RAW};

    @Override
    public @NotNull String[] type() {
        return TYPE;
    }

    @Override
    public Object eval(@NotNull Map<String, Object> ctx, @NotNull RnaWarmed expr, boolean mute) {
        return expr.expr;
    }

    @Override
    public @NotNull RnaEngine fork() {
        return this;
    }
}
