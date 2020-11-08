package pro.fessional.meepo.bind.rna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Const;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Tock;
import pro.fessional.meepo.sack.Acid;
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
 * ` <% RNA:DONE user pwd %> \n`
 * </pre>
 *
 * @author trydofor
 * @since 2020-11-01
 */
public class RnaDone extends Tock {

    @NotNull
    public final Set<String> name;

    public RnaDone(String text, Clop edge, Collection<String> name) {
        this(Dent.chars(text, edge), edge, Const.TXT$EMPTY, name);
    }

    protected RnaDone(char[] text9, Clop edge, @NotNull String tock, Collection<String> name) {
        super(text9, edge, tock);
        if (name instanceof Set) {
            this.name = Collections.unmodifiableSet((Set<String>) name);
        } else {
            Set<String> set = new HashSet<>(name);
            this.name = Collections.unmodifiableSet(set);
        }
    }

    public RnaDone copy(String tock) {
        return new RnaDone(text9, edge, tock, name);
    }

    @Override
    public void merge(Acid acid, Writer buff) {
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
        StringWriter buff = new StringWriter();
        toString(buff);
        return buff.toString();
    }

    public void toString(Writer buff) {
        try {
            buff.append("RnaDone{");
            buff.append("tock='");
            Dent.line(buff, tock9);
            buff.append("', name=[");
            Dent.line(buff, String.join(",", name));
            buff.append("]}");
            buff.append("; ");
            edge.toString(buff);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
