package pro.fessional.meepo.bind.txt;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.mark.Dyn;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.util.Dent;

import java.util.Map;
import java.util.Objects;

/**
 * 从环境中获得变量
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class TxtRnaUse extends Exon implements Dyn {
    @NotNull
    public final String para;
    public final int left;

    public TxtRnaUse(@NotNull String text, Clop edge, @NotNull String para, int left) {
        super(text, edge);
        this.para = para;
        this.left = left;
    }

    @Override
    public void merge(Map<String, Object> ctx, RnaEngine eng, StringBuilder buf) {
        Object o = ctx.get(para);
        if (o != null) {
            Dent.left(buf, left, o);
        }
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
        return "TxtRnaUse{" +
                "para='" + para + '\'' +
                '}';
    }
}
