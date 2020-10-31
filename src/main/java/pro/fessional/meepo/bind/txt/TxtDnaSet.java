package pro.fessional.meepo.bind.txt;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.poof.RnaEngine;

import java.util.Map;
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
    public void merge(Map<String, Object> ctx, RnaEngine eng, StringBuilder buf) {
        buf.append(repl);
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
        return "TxtDnaSet{" +
                "repl='" + repl + '\'' +
                '}';
    }
}
