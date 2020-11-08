package pro.fessional.meepo.bind.dna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.kin.Prc;
import pro.fessional.meepo.bind.txt.TxtDnaSet;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;
import pro.fessional.meepo.bind.wow.Tick;
import pro.fessional.meepo.util.Dent;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * <pre>
 * ` <% DNA:SET /ConstantEnumTemplate/{{className}}/* %> \n`
 * edge=`<% DNA:SET /ConstantEnumTemplate/{{className}}/* %>`
 *
 * ` // DNA:SET /ConstantEnumTemplate/{{className}}/* \n`
 * edge=`// DNA:SET /ConstantEnumTemplate/{{className}}/* \n`
 *
 * main=`DNA:SET /ConstantEnumTemplate/{{className}}/*`
 * find=`ConstantEnumTemplate`
 * repl=`{{className}}`
 * life=`*`
 * </pre>
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class DnaSet extends Tick implements Prc {

    @NotNull
    public final Pattern find;
    @NotNull
    public final String repl;

    public DnaSet(@NotNull String text, Clop edge, @NotNull Life life, @NotNull Pattern find, @NotNull String repl) {
        super(text, edge, life);
        this.find = find;
        this.repl = repl;
    }

    @Override
    public Life.State match(List<N> lst, String txt) {
        return match(lst, txt, find);
    }

    @Override
    public @NotNull List<Exon> apply(Clop pos, String txt, int bar) {
        if (pos.length > 0) {
            return Collections.singletonList(new TxtDnaSet(txt, pos, repl));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DnaSet dnaSet = (DnaSet) o;
        return Objects.equals(find.pattern(), dnaSet.find.pattern()) &&
                Objects.equals(repl, dnaSet.repl) &&
                Objects.equals(life, dnaSet.life);
    }

    @Override
    public int hashCode() {
        return Objects.hash(find.pattern(), repl, life);
    }

    @Override
    public String toString() {
        StringWriter buff = new StringWriter();
        toString(buff);
        return buff.toString();
    }

    public void toString(Writer buff) {
        try {
            buff.append("DnaSet{");
            buff.append("find='");
            Dent.line(buff, find.pattern());
            buff.append("', repl='");
            Dent.line(buff, repl);
            buff.append("'}");
            buff.append("; ");
            edge.toString(buff);
            buff.append("; ");
            life.toString(buff);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
