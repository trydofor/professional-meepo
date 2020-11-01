package pro.fessional.meepo.bind.txt;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.kin.Dyn;
import pro.fessional.meepo.bind.kin.Ngx;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.sack.Acid;
import pro.fessional.meepo.util.Dent;

import java.util.Objects;

/**
 * 通过引擎执行获得变量
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class TxtRnaRun extends Exon implements Dyn, Ngx {

    @NotNull
    public final String type;
    @NotNull
    public final String expr;
    public final boolean mute;
    public final int left;


    public TxtRnaRun(@NotNull String text, Clop edge, @NotNull String type, @NotNull String expr, boolean mute, int left) {
        super(text, edge);
        this.type = type;
        this.expr = expr;
        this.mute = mute;
        this.left = left;
    }

    @Override
    public @NotNull String getType() {
        return type;
    }

    @Override
    public void merge(Acid acid, StringBuilder buf) {
        RnaEngine eng = acid.getEngine(this);
        if (eng == null) return;

        Object s = eng.eval(type, expr, acid.context, mute);
        if (edge.length > 0) {
            Dent.left(buf, left, s);
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
        StringBuilder buff = new StringBuilder("TxtRnaRun{");
        buff.append("type='");
        Dent.line(buff, type);
        buff.append("', expr='");
        Dent.line(buff, expr);
        buff.append("', mute=");
        buff.append(mute);
        buff.append('}');
        buff.append("; ").append(edge);
        return buff.toString();
    }
}
