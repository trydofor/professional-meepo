package pro.fessional.meepo.bind.rna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.kin.Bar;
import pro.fessional.meepo.bind.kin.Prc;
import pro.fessional.meepo.bind.txt.TxtRnaUse;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;
import pro.fessional.meepo.bind.wow.Tick;
import pro.fessional.meepo.util.Dent;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * <pre>
 * ` <% RNA:USE /meepo/who/1 %> \n`
 * edge=`<% RNA:USE /meepo/who/1 %>`
 *
 * ` // RNA:USE /meepo/who/1 \n`
 * edge=`// RNA:USE /meepo/who/1 \n`
 *
 * main=`RNA:USE /meepo/who/1`
 * find=`meepo`
 * para=`who`
 * life=1
 * </pre>
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class RnaUse extends Tick implements Bar, Prc {

    @NotNull
    public final Clop main;
    @NotNull
    public final Pattern find;
    @NotNull
    public final String para;

    public RnaUse(String text, Clop edge, @NotNull Life life, @NotNull Clop main, @NotNull Pattern find, @NotNull String para) {
        super(text, edge, life);
        this.main = main;
        this.find = find;
        this.para = para;
    }

    @Override
    public Life.State match(List<N> lst, String txt) {
        return match(lst, txt, find);
    }

    @Override
    public void apply(List<Exon> gen, Clop pos, String txt, int bar) {
        gen.add(new TxtRnaUse(txt, pos, para, bar));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RnaUse rnaUse = (RnaUse) o;
        return find.pattern().equals(rnaUse.find.pattern()) &&
                para.equals(rnaUse.para) &&
                life.equals(rnaUse.life);
    }

    @Override
    public int hashCode() {
        return Objects.hash(find.pattern(), para, life);
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder("RnaUse{");
        buff.append("find='");
        Dent.line(buff, find.pattern());
        buff.append("', para='");
        Dent.line(buff, para);
        buff.append("'}");
        buff.append("; ").append(edge);
        buff.append("; ").append(life);
        return buff.toString();
    }
}
