package pro.fessional.meepo.bind.dna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.util.Dent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * <pre>
 * ` <% DNA:END id 黑皇杖 %> \n`
 * edge=`<% DNA:END id 黑皇杖 %>`
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
    public final Clop main;
    @NotNull
    public final Set<String> name;

    public DnaEnd(String text, Clop edge, @NotNull Clop main, Collection<String> name) {
        super(text, edge);
        this.main = main;
        if (name instanceof Set) {
            this.name = Collections.unmodifiableSet((Set<String>) name);
        } else {
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
        StringBuilder buff = new StringBuilder("DnaEnd{");
        buff.append("name=[");
        Dent.line(buff, String.join(",", name));
        buff.append("]}");
        buff.append("; ").append(edge);
        return buff.toString();
    }
}
