package pro.fessional.meepo.bind.rna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Clop;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.Life;
import pro.fessional.meepo.bind.Live;
import pro.fessional.meepo.bind.txt.TxtRnaRun;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * <pre>
 * ` <% RNA:RUN os/who/basename $(pwd)/1 %> \n`
 * edge=`<% RNA:RUN os/who/basename $(pwd)/1 %>`
 *
 * ` // RNA:RUN os/who/basename $(pwd)/1 \n`
 * edge=`// RNA:RUN os/who/basename $(pwd)/1 \n`
 *
 * main=`RNA:RUN os/who/basename $(pwd)/1`
 * type=`os`
 * name=`who`
 * expr=`basename $(pwd)`
 * life=1
 * </pre>
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class RnaRun extends Live {

    @NotNull
    public final Clop main;
    @NotNull
    public final String type;
    @NotNull
    public final Pattern find;
    @NotNull
    public final String expr;
    public final boolean mute;

    public RnaRun(String text, @NotNull Life life, Clop edge, @NotNull Clop main, @NotNull String type, @NotNull Pattern find, @NotNull String expr, boolean mute) {
        super(text, edge, life);
        this.main = main;
        this.type = type;
        this.find = find;
        this.expr = expr;
        this.mute = mute;
    }

    @Override
    public Life.State match(List<Exon.N> lst, String txt) {
        return match(lst, txt, find);
    }

    @Override
    public void apply(List<Exon> gen, Clop pos, String txt) {
        gen.add(new TxtRnaRun(txt, pos, type, expr, mute));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RnaRun rnaRun = (RnaRun) o;
        return type.equals(rnaRun.type) &&
                find.pattern().equals(rnaRun.find.pattern()) &&
                expr.equals(rnaRun.expr) &&
                mute == rnaRun.mute &&
                life.equals(rnaRun.life);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, find.pattern(), expr, mute, life);
    }

    @Override
    public String toString() {
        return "RnaRun{" +
                "type='" + type + '\'' +
                ", find='" + find.pattern() + '\'' +
                ", expr='" + expr + '\'' +
                ", quiet=" + mute +
                ", life=" + life +
                '}';
    }
}
