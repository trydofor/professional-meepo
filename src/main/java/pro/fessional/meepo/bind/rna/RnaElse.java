package pro.fessional.meepo.bind.rna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.kin.Dyn;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Tock;
import pro.fessional.meepo.sack.Acid;
import pro.fessional.meepo.util.Dent;

import java.util.Objects;

/**
 * <pre>
 * ` <% RNA:ELSE user %> \n`
 * </pre>
 *
 * @author trydofor
 * @since 2020-11-01
 */
public class RnaElse extends Tock implements Dyn {

    @NotNull
    public final Clop main;

    public RnaElse(String text, Clop edge, @NotNull Clop main, String tock) {
        super(text, edge, tock);
        this.main = main;
    }

    @Override
    public void merge(Acid acid, Appendable buff) {
        Tock tk = acid.execute.computeIfAbsent(tock, s -> this);
        if (tk == this) {
            logger.trace("[👹Merge:tock] deal RNA:ELSE tock={}", tock);
            for (Exon exon : gene) {
                exon.merge(acid, buff);
            }
        } else {
            logger.trace("[👹Merge:tock] skip RNA:ELSE tock={}", tock);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RnaElse tock1 = (RnaElse) o;
        return tock.equals(tock1.tock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tock);
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder("RnaElse{");
        buff.append("tock='");
        Dent.line(buff, tock);
        buff.append("'}");
        buff.append("; ").append(edge);
        return buff.toString();
    }
}
