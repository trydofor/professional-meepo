package pro.fessional.meepo.poof.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.poof.RnaEngine;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

import static pro.fessional.meepo.bind.Const.ENGINE$JS;
import static pro.fessional.meepo.bind.Const.TXT$EMPTY;

/**
 * ScriptEngineManager.getEngineByName("JavaScript");
 * 执行js
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class JsEngine implements RnaEngine {

    public static final String CTX_NAME = "ctx";
    private static final ScriptEngineManager MANAGER = new ScriptEngineManager();
    private final ScriptEngine engine = MANAGER.getEngineByName("JavaScript");

    private static final String[] TYPE = {ENGINE$JS};

    @Override
    public @NotNull String[] type() {
        return TYPE;
    }

    @Override
    public @NotNull Object eval(@NotNull String type, @NotNull String expr, @NotNull Map<String, Object> ctx, boolean mute) {
        try {
            engine.put(CTX_NAME, ctx);
            Object v = engine.eval(expr);
            return v == null || mute ? TXT$EMPTY : v;
        } catch (ScriptException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public @NotNull RnaEngine fork() {
        return new JsEngine();
    }
}
