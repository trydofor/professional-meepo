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

import static pro.fessional.meepo.bind.Const.ENGINE$MAP;

/**
 * 从环境中获得变量
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class TxtRnaUse extends Exon implements Dyn, Ngx {
    @NotNull
    public final String para;
    public final int left;

    public TxtRnaUse(@NotNull String text, Clop edge, @NotNull String para, int left) {
        super(text, edge);
        this.para = para;
        this.left = left;
    }

    @Override
    public @NotNull String getType() {
        return ENGINE$MAP;
    }

    @Override
    public void merge(Acid acid, StringBuilder buf) {
        RnaEngine eng = acid.getEngine(this);
        if (eng == null) return;

        Object o = eng.eval(ENGINE$MAP, para, acid.context, false);
        Dent.left(buf, left, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TxtRnaUse txtRnaUse = (TxtRnaUse) o;
        return para.equals(txtRnaUse.para);
    }

    @Override
    public int hashCode() {
        return Objects.hash(para);
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder("TxtRnaUse{");
        buff.append("para='");
        Dent.line(buff, para);
        buff.append("'}");
        buff.append("; ").append(edge);
        return buff.toString();
    }
}
