package pro.fessional.meepo.poof.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.util.Read;

import java.util.HashMap;
import java.util.Map;

import static pro.fessional.meepo.bind.Const.ENGINE_URI;
import static pro.fessional.meepo.bind.Const.TXT_EMPTY;

/**
 * 以UTF8输出URI内容
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class UriEngine implements RnaEngine {

    private static final String[] TYPE = {ENGINE_URI};

    private final Map<String, String> cache = new HashMap<>();

    @Override
    public @NotNull String[] type() {
        return TYPE;
    }

    @Override
    public @NotNull Object eval(@NotNull String type, @NotNull String expr, @NotNull Map<String, Object> ctx, boolean mute) {
        String s = cache.get(expr);
        if (s != null) return s;

        String str = Read.read(expr);
        cache.put(expr, str);

        return mute ? TXT_EMPTY : str;
    }

    @Override
    public @NotNull RnaEngine fork() {
        return this;
    }
}
