package pro.fessional.meepo.bind.dna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.util.Dent;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * <pre>
 * ` &lt;% DNA:END id 黑皇杖 %&gt; \n`
 * edge=`&lt;% DNA:END id 黑皇杖 %&gt;`
 *
 * ` // DNA:END id 黑皇杖 \n`
 * edge=`// DNA:END id 黑皇杖 \n`
 *
 * main=`DNA:END id 黑皇杖`
 * name={`id`,`黑皇杖`}
 * </pre>
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class DnaEnd extends Exon {

    @NotNull
    public final Set<String> name;

    public DnaEnd(String text, Clop edge, Collection<String> name) {
        super(text, edge);
        if (name instanceof Set) {
            this.name = Collections.unmodifiableSet((Set<String>) name);
        }
        else {
            Set<String> set = new HashSet<>(name);
            this.name = Collections.unmodifiableSet(set);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DnaEnd dnaEnd = (DnaEnd) o;
        return name.equals(dnaEnd.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        StringWriter buff = new StringWriter();
        toString(buff);
        return buff.toString();
    }

    public void toString(Writer buff) {
        try {
            buff.append("DnaEnd{");
            buff.append("name=[");
            Dent.lineIt(buff, String.join(",", name));
            buff.append("]}");
            buff.append("; ");
            edge.toString(buff);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
