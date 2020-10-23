package pro.fessional.meepo.bind.dna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Clop;
import pro.fessional.meepo.bind.Const;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.Life;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
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
public class DnaSet extends Exon {

    @NotNull
    public final Pattern find;
    @NotNull
    public final String repl;

    public DnaSet(@NotNull String text, @NotNull Life life, Clop edge, Clop main, @NotNull Pattern find, @NotNull String repl) {
        super(text, life, edge, main);
        this.find = find;
        this.repl = repl;
    }

    @Override
    public List<N> match(String txt, int off, int end) {
        Matcher m = find.matcher(txt.substring(off, end));
        // 匹配 TODO
        return super.match(txt, off, end);
    }

    @Override
    public void apply(N mtc, String txt, StringBuilder buf) {
        // TODO
        super.apply(mtc, txt, buf);
    }

    @Override
    public void merge(Map<String, Object> ctx, StringBuilder buf) {
        // skip
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
