package pro.fessional.meepo.poof.impl.java;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author trydofor
 * @since 2020-11-04
 */
public interface JavaEval {

    /**
     * java函数
     *
     * @param ctx 上下文
     * @param obj 管道第一个参数
     * @param arg 管道参数
     * @return 结果
     */
    Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg);
}
