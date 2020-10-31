package pro.fessional.meepo.bind.txt;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Dyn;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.poof.RnaEngine;

import java.util.Map;
import java.util.Objects;

/**
 * 通过引擎执行获得变量
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class TxtRnaRun extends Exon implements Dyn {

    @NotNull
    public final String type;
    @NotNull
    public final String expr;
    public final boolean mute;

    public TxtRnaRun(@NotNull String text, Clop edge, @NotNull String type, @NotNull String expr, boolean mute) {
        super(text, edge);
        this.type = type;
        this.expr = expr;
        this.mute = mute;
    }

    @Override
    public void merge(Map<String, Object> ctx, RnaEngine eng, StringBuilder buf) {
        if (eng != null) {
            Object s = eng.eval(type, expr, ctx, mute);
            if (edge.length > 0) {
                buf.append(s);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TxtRnaRun txtRnaRun = (TxtRnaRun) o;
        return mute == txtRnaRun.mute &&
                type.equals(txtRnaRun.type) &&
                expr.equals(txtRnaRun.expr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, expr, mute);
    }

    @Override
    public String toString() {
        return "TxtRnaRun{" +
                "type='" + type + '\'' +
                ", expr='" + expr + '\'' +
                ", mute=" + mute +
                '}';
    }
}
