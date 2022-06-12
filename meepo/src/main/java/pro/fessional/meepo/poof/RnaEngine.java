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
     * 对功能体求值
     *
     * @param ctx  执行环境。
     * @param expr 功能体
     * @param mute 是否忽略错误
     * @return 执行结果
     */
    Object eval(@NotNull Map<String, Object> ctx, @NotNull RnaWarmed expr, boolean mute);

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

    /**
     * 解析阶段，方法体预热，如语法检查，预编译等，一个expr应该只warm一次
     *
     * @param type 引擎类型
     * @param expr 功能体
     * @return 警告信息，无则返回null
     */
    @NotNull
    default RnaWarmed warm(@NotNull String type, @NotNull String expr) {
        return new RnaWarmed(type, expr);
    }
}
