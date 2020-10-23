package pro.fessional.meepo.bind.txt;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Clop;
import pro.fessional.meepo.bind.Const;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.Life;

import java.util.Objects;

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
public class TxtPlain extends Exon {

    @NotNull
    public final String note;

    public TxtPlain(@NotNull String text, int start, int until) {
        super(text, Life.nobodyOne(), new Clop(start, until));
        this.note = Const.TXT_EMPTY;
    }

    public TxtPlain(@NotNull String text, int start, int until, String note) {
        super(text, Life.nobodyOne(), new Clop(start, until));
        this.note = note == null ? Const.TXT_EMPTY : note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TxtPlain txtPlain = (TxtPlain) o;
        return text.regionMatches(edge.start, txtPlain.text, txtPlain.edge.start, edge.length);
    }

    @Override
    public int hashCode() {
        return text.substring(edge.start, edge.until).hashCode();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(edge.length + 20);
        buf.append("TxtPlain{text='");
        buf.append(text, edge.start, edge.until);
        if (!note.isEmpty()) {
            buf.append("', note='");
            buf.append(note);
        }
        buf.append("'}");
        return buf.toString();
    }
}
