package pro.fessional.meepo.poof;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * RNA, the execution engine should be used in single thread.
 *
 * @author trydofor
 * @since 2020-10-15
 */
public interface RnaEngine {

    /**
     * Evaluate the function body
     *
     * @param ctx  the execution context
     * @param expr expression of the function body
     * @param mute whether to ignore error
     * @return the result
     */
    Object eval(@NotNull Map<String, Object> ctx, @NotNull RnaWarmed expr, boolean mute);

    /**
     * Types can be executed by this engine. `*` means any
     */
    @NotNull
    String[] type();

    /**
     * Create a new engine. The context of the parent engine can be copied or shared.
     */
    @NotNull
    RnaEngine fork();

    /**
     * During the parsing phase, warm up the function body, such as syntax checking, pre-compilation, etc.
     * An `expr` should only be warmed up once
     *
     * @param type engine type
     * @param expr function body
     * @return warmed engine with info of waring or null
     */
    @NotNull
    default RnaWarmed warm(@NotNull String type, @NotNull String expr) {
        return new RnaWarmed(type, expr);
    }
}
