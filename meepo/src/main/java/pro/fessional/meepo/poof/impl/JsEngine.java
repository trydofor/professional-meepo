package pro.fessional.meepo.poof.impl;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.poof.RnaWarmed;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;

import static pro.fessional.meepo.bind.Const.ENGINE$JS;

/**
 * ScriptEngineManager.getEngineByName("JavaScript") to run js (java 11+ removed)
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class JsEngine implements RnaEngine {

    private static final Logger logger = LoggerFactory.getLogger(JsEngine.class);
    public static final String CTX_NAME = "ctx";
    private static final ScriptEngineManager MANAGER = new ScriptEngineManager();
    private final ScriptEngine engine = MANAGER.getEngineByName("JavaScript");

    private static final String[] TYPE = {ENGINE$JS};

    @Override
    public @NotNull String[] type() {
        return TYPE;
    }

    @Override
    public Object eval(@NotNull Map<String, Object> ctx, @NotNull RnaWarmed expr, boolean mute) {
        Object obj = null;
        try {
            engine.put(CTX_NAME, ctx);
            obj = engine.eval(expr.expr);
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
        return new JsEngine();
    }
}
