package pro.fessional.meepo.bind.dna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Clop;
import pro.fessional.meepo.bind.Const;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.Life;

import java.util.Map;
import java.util.Objects;

/**
 * <pre>
 * ` <% DNA:BKB 黑皇杖 %> \n`
 * edge=`<% DNA:BKB 黑皇杖 %>`
 *
 * ` // DNA:BKB 黑皇杖 \n`
 * edge=`// DNA:BKB 黑皇杖 \n`
 *
 * main=`DNA:BKB 黑皇杖`
 * life.name=`黑皇杖`
 * </pre>
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class DnaBkb extends Exon {

    @NotNull
    public final String name;

    public DnaBkb(String text, Clop edge, Clop main, String name) {
        super(text, Life.namedAny(name), edge, main);
        this.name = name == null ? Const.TXT_EMPTY : name;
    }

    @Override
    public void merge(Map<String, Object> ctx, StringBuilder buf) {
        // skip
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DnaBkb dnaBkb = (DnaBkb) o;
        return name.equals(dnaBkb.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "DnaBkb{" +
                "name='" + name + '\'' +
                '}';
    }
}
