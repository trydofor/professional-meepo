package pro.fessional.meepo.poof.impl;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.poof.RnaWarmed;
import pro.fessional.meepo.util.Read;

import java.util.Map;

import static pro.fessional.meepo.bind.Const.ENGINE$URI;

/**
 * Output the URI in UTF8
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class UriEngine implements RnaEngine {

    private static final Logger logger = LoggerFactory.getLogger(UriEngine.class);

    private static final String[] TYPE = {ENGINE$URI};

    @Override
    public @NotNull String[] type() {
        return TYPE;
    }

    @Override
    public Object eval(@NotNull Map<String, Object> ctx, @NotNull RnaWarmed expr, boolean mute) {
        Object obj = null;
        try {
            // Static String. Be aware of the security when execute it
            obj = ctx.computeIfAbsent(expr.expr, Read::read);
        }
        catch (Throwable t) {
            if (mute) {
                logger.warn("mute failed-eval " + expr, t);
            }
            else {
                throw new IllegalStateException(expr.toString(), t);
            }
        }
        return obj;
    }

    @Override
    public @NotNull RnaEngine fork() {
        return this;
    }
}
