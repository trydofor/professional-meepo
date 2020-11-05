package pro.fessional.meepo.bind.txt;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.sack.Acid;
import pro.fessional.meepo.util.Dent;

import java.util.Objects;

/**
 * 静态替换
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class TxtDnaSet extends Exon {

    @NotNull
    public final String repl;

    public TxtDnaSet(@NotNull String text, Clop edge, @NotNull String repl) {
        super(text, edge);
        this.repl = repl;
    }

    @Override
    public void merge(Acid acid, Appendable buff) {
        Dent.pend(buff, repl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TxtDnaSet txtDnaSet = (TxtDnaSet) o;
        return repl.equals(txtDnaSet.repl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repl);
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder("TxtDnaSet{");
        buff.append("repl='");
        Dent.line(buff, repl);
        buff.append("'}");
        buff.append("; ").append(edge);
        return buff.toString();
    }
}
