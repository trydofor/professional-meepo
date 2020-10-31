package pro.fessional.meepo.bind.dna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Const;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;
import pro.fessional.meepo.bind.wow.Live;

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
public class DnaBkb extends Live {

    @NotNull
    public final Clop main;
    @NotNull
    public final String name;

    public DnaBkb(String text, Clop edge, @NotNull Clop main, String name) {
        super(text, edge, Life.namedAny(name));
        this.main = main;
        this.name = name == null ? Const.TXT_EMPTY : name;
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
