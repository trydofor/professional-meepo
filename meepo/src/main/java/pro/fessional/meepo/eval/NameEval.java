package pro.fessional.meepo.eval;

import org.jetbrains.annotations.NotNull;

/**
 * @author trydofor
 * @since 2020-11-04
 */
public interface NameEval extends JavaEval {

    /**
     * name of eval, eg. fun:fmt
     *
     * @see FunEnv#KEY$PREFIX
     */
    @NotNull
    String[] name();

    /**
     * information about eval
     */
    @NotNull
    String info();
}
