package pro.fessional.meepo.bind.txt;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Clop;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.poof.RnaEngine;

import java.util.Map;

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
    public void merge(Map<String, Object> ctx, RnaEngine eng, StringBuilder buf) {
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
        return "TxtSimple{text='" +
                text.substring(edge.start, edge.until) +
                "'}";
    }
}
