package pro.fessional.meepo.eval;

import org.jetbrains.annotations.NotNull;

/**
 * @author trydofor
 * @since 2020-11-04
 */
public interface NameEval extends JavaEval {

    /**
     * eval名字，如 fun:fmt
     *
     * @return 名字
     * @see FunEnv#KEY$PREFIX
     */
    @NotNull
    String name();

    /**
     * 信息
     *
     * @return 信息
     */
    @NotNull
    String info();
}
