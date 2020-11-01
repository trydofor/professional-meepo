package pro.fessional.meepo.bind.dna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.txt.TxtDnaSet;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;
import pro.fessional.meepo.bind.wow.Live;

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
public class DnaSet extends Live {

    @NotNull
    public final Clop main;
    @NotNull
    public final Pattern find;
    @NotNull
    public final String repl;

    public DnaSet(@NotNull String text, @NotNull Life life, Clop edge, @NotNull Clop main, @NotNull Pattern find, @NotNull String repl) {
        super(text, edge, life);
        this.main = main;
        this.find = find;
        this.repl = repl;
    }

    @Override
    public Life.State match(List<N> lst, String txt) {
        return match(lst, txt, find);
    }

    @Override
    public void apply(List<Exon> gen, Clop pos, String txt, int bar) {
        if (pos.length > 0) {
            gen.add(new TxtDnaSet(txt, pos, repl));
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
        return "DnaSet{" +
                "find='" + find.pattern() +
                "', repl='" + repl +
                "', life=" + life +
                '}';
    }
}
