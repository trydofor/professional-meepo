package pro.fessional.meepo.bind.txt;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.sack.Acid;
import pro.fessional.meepo.util.Dent;

/**
 * <pre>
 * ` plain text \n`
 * edge=` plain text \n`
 * main=` plain text \n`
 * </pre>
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class TxtSimple extends Exon {

    public TxtSimple(@NotNull String text, int start, int until) {
        super(text, new Clop(start, until));
    }

    @Override
    public void merge(Acid acid, StringBuilder buf) {
        buf.append(text, edge.start, edge.until);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TxtSimple txt = (TxtSimple) o;
        return text.regionMatches(edge.start, txt.text, txt.edge.start, edge.length);
    }

    @Override
    public int hashCode() {
        return text.substring(edge.start, edge.until).hashCode();
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder("TxtSimple{");
        buff.append("text='");
        Dent.line(buff, text, edge.start, edge.until);
        buff.append("'}");
        buff.append("; ").append(edge);
        return buff.toString();
    }
}
