package pro.fessional.meepo.eval;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author trydofor
 * @since 2020-11-04
 */
public interface JavaEval {

    /**
     * java function
     *
     * @param ctx the context
     * @param obj The 1st arg of the pipeline
     * @param arg other args of pipeline
     * @return the result
     */
    Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg);
}
