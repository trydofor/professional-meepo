package pro.fessional.meepo.bind.rna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Const;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Tock;
import pro.fessional.meepo.sack.Acid;
import pro.fessional.meepo.util.Dent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * <pre>
 * ` <% RNA:DONE user pwd %> \n`
 * </pre>
 *
 * @author trydofor
 * @since 2020-11-01
 */
public class RnaDone extends Tock {

    @NotNull
    public final Clop main;
    @NotNull
    public final Set<String> name;

    public RnaDone(String text, Clop edge, @NotNull String tock, @NotNull Clop main, Collection<String> name) {
        super(text, edge, tock);
        this.main = main;
        if (name instanceof Set) {
            this.name = Collections.unmodifiableSet((Set<String>) name);
        } else {
            Set<String> set = new HashSet<>(name);
            this.name = Collections.unmodifiableSet(set);
        }
    }

    public RnaDone(String text, Clop edge, @NotNull Clop main, Collection<String> name) {
        this(text, edge, Const.TXT$EMPTY, main, name);
    }

    public RnaDone copy(String tock) {
        return new RnaDone(text, edge, tock, main, name);
    }

    @Override
    public void merge(Acid acid, Appendable buff) {
        if (tock.isEmpty()) {
            logger.trace("[👹Merge:tock] skip RNA:DONE tock is empty");
        } else {
            logger.trace("[👹Merge:tock] deal RNA:DONE tock={}", tock);
            acid.execute.remove(tock);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RnaDone rnaDone = (RnaDone) o;
        return name.equals(rnaDone.name) &&
                tock.equals(rnaDone.tock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tock);
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder("RnaDone{");
        buff.append("tock='");
        Dent.line(buff, tock);
        buff.append("', name=[");
        Dent.line(buff, String.join(",", name));
        buff.append("]}");
        buff.append("; ").append(edge);
        return buff.toString();
    }
}
