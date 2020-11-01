package pro.fessional.meepo.poof.impl.java;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author trydofor
 * @since 2020-11-04
 */
public interface JavaEval {
    Object eval(@NotNull Map<String, Object> ctx);
}
