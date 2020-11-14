package pro.fessional.meepo.poof;

import org.jetbrains.annotations.NotNull;

import static pro.fessional.meepo.bind.Const.TXT$EMPTY;

/**
 * @author trydofor
 * @since 2020-11-09
 */
public class RnaWarmed {

    public static final RnaWarmed EMPTY = new RnaWarmed(TXT$EMPTY, TXT$EMPTY);

    @NotNull
    public final String type;
    @NotNull
    public final String expr;

    /**
     * warm的预热对象
     */
    private final Object work;
    /**
     * warm后的信息
     */
    public String info;

    public RnaWarmed(@NotNull String type, @NotNull String expr) {
        this(type, expr, null);
    }

    public RnaWarmed(@NotNull String type, @NotNull String expr, Object work) {
        this.type = type;
        this.expr = expr;
        this.work = work;
    }

    @SuppressWarnings("unchecked")
    public <T> T getTypedWork() {
        return (T) work;
    }

    public boolean hasInfo() {
        return info != null && info.length() > 0;
    }
}
