package pro.fessional.meepo.poof.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.poof.RnaEngine;

import java.util.Map;

import static pro.fessional.meepo.bind.Const.ENGINE$RAW;
import static pro.fessional.meepo.bind.Const.TXT$EMPTY;

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
    public @NotNull Object eval(@NotNull String type, @NotNull String expr, @NotNull Map<String, Object> ctx, boolean mute) {
        return mute ? TXT$EMPTY : expr;
    }

    @Override
    public @NotNull RnaEngine fork() {
        return this;
    }
}
