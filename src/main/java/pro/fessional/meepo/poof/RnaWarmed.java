package pro.fessional.meepo.poof;

import org.jetbrains.annotations.NotNull;

/**
 * @author trydofor
 * @since 2020-11-09
 */
public class RnaWarmed {

    @NotNull
    public final String type;
    @NotNull
    public final String expr;

    /**
     * warm的预热对象
     */
    public final Object work;
    /**
     * warm后type下的子类别
     */
    public final int kind;
    /**
     * warm后的信息
     */
    public String info;

    public RnaWarmed(@NotNull String type, @NotNull String expr) {
        this(type, expr, null, -1);
    }

    public RnaWarmed(@NotNull String type, @NotNull String expr, Object work) {
        this(type, expr, work, -1);
    }

    public RnaWarmed(@NotNull String type, @NotNull String expr, Object work, int kind) {
        this.kind = kind;
        this.type = type;
        this.expr = expr;
        this.work = work;
    }

    @SuppressWarnings("unchecked")
    public <T> T getTypedWork() {
        return (T) work;
    }

    public <T> T getTypedWork(Class<T> clz) {
        return clz.cast(work);
    }

    public boolean hasInfo() {
        return info != null && info.length() > 0;
    }
}
