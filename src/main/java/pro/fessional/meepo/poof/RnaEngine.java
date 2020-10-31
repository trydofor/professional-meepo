package pro.fessional.meepo.poof;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * RNA 执行引擎接口，单线程内支线。
 *
 * @author trydofor
 * @since 2020-10-15
 */
public interface RnaEngine {

    /**
     * 对功能体求值，null以""代替
     *
     * @param type  引擎类型
     * @param expr  功能体
     * @param ctx   执行环境。
     * @param mute 是否忽略std out输出
     * @return 执行结果
     */
    @NotNull
    Object eval(@NotNull String type, @NotNull String expr, @NotNull Map<String, Object> ctx, boolean mute);

    /**
     * 可以被此引擎执行的类型。`*`表示any
     *
     * @return 类型
     */
    @NotNull
    String[] type();

    /**
     * 创建一个新的引擎。复制或共享父engine环境无要求。
     * 一般的使用场景等同于new，可是new是关键词，还是3字母
     *
     * @return 新引擎
     */
    @NotNull
    RnaEngine fork();
}
