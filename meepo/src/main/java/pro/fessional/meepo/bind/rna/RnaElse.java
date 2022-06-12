package pro.fessional.meepo.bind.rna;

import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.kin.Rng;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Tock;
import pro.fessional.meepo.sack.Acid;
import pro.fessional.meepo.util.Dent;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Objects;

/**
 * <pre>
 * ` &lt;% RNA:ELSE user %&gt; \n`
 * </pre>
 *
 * @author trydofor
 * @since 2020-11-01
 */
public class RnaElse extends Tock implements Rng {

    public RnaElse(String text, Clop edge, String tock) {
        super(text, edge, tock);
    }

    @Override
    public void merge(Acid acid, Writer buff) {
        Tock tk = acid.execute.computeIfAbsent(tock, s -> this);
        if (tk == this) {
            logger.trace("[👹Merge:tock] deal RNA:ELSE tock={}", tock);
            for (Exon exon : gene) {
                exon.merge(acid, buff);
            }
        }
        else {
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
        StringWriter buff = new StringWriter();
        toString(buff);
        return buff.toString();
    }

    public void toString(Writer buff) {
        try {
            buff.append("RnaElse{");
            buff.append("tock='");
            Dent.lineIt(buff, tock);
            buff.append("'}");
            buff.append("; ");
            edge.toString(buff);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
