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
     * warmed up kind
     */
    public final int kind;
    /**
     * warmed up object
     */
    private final Object work;
    /**
     * warmed up info
     */
    public String info;

    public RnaWarmed(@NotNull String type, @NotNull String expr) {
        this(type, expr, null);
    }

    public RnaWarmed(@NotNull String type, @NotNull String expr, Object work) {
        this.type = type;
        this.expr = expr;
        this.work = work;
        this.kind = -1;
    }

    public RnaWarmed(@NotNull String type, @NotNull String expr, Object work, int kind) {
        this.type = type;
        this.expr = expr;
        this.work = work;
        this.kind = kind;
    }

    @SuppressWarnings("unchecked")
    public <T> T getTypedWork() {
        return (T) work;
    }

    public boolean hasInfo() {
        return info != null && !info.isEmpty();
    }

    @Override
    public String toString() {
        return "RnaWarmed{" +
               "type='" + type + '\'' +
               ", expr='" + expr + '\'' +
               ", kind=" + kind +
               ", info='" + info + '\'' +
               '}';
    }
}
