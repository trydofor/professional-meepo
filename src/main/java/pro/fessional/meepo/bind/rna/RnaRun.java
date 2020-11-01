package pro.fessional.meepo.bind.rna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.kin.Bar;
import pro.fessional.meepo.bind.kin.Prc;
import pro.fessional.meepo.bind.txt.TxtRnaRun;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;
import pro.fessional.meepo.bind.wow.Tick;
import pro.fessional.meepo.util.Dent;

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
public class RnaRun extends Tick implements Bar, Prc {

    @NotNull
    public final Clop main;
    @NotNull
    public final String type;
    @NotNull
    public final Pattern find;
    @NotNull
    public final String expr;
    public final boolean mute;

    public RnaRun(String text, Clop edge, @NotNull Life life, @NotNull Clop main, @NotNull String type, @NotNull Pattern find, @NotNull String expr, boolean mute) {
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
    public void apply(List<Exon> gen, Clop pos, String txt, int bar) {
        gen.add(new TxtRnaRun(txt, pos, type, expr, mute, bar));
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
        StringBuilder buff = new StringBuilder("RnaRun{");
        buff.append("type='");
        Dent.line(buff, type);
        buff.append("', find='");
        Dent.line(buff, find.pattern());
        buff.append("', expr='");
        Dent.line(buff, expr);
        buff.append("', mute=");
        buff.append(mute);
        buff.append("}");
        buff.append("; ").append(edge);
        buff.append("; ").append(life);
        return buff.toString();
    }
}
