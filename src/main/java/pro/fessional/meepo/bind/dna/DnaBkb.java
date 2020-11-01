package pro.fessional.meepo.bind.dna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Const;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;
import pro.fessional.meepo.bind.wow.Tick;
import pro.fessional.meepo.util.Dent;

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
public class DnaBkb extends Tick {

    @NotNull
    public final Clop main;
    @NotNull
    public final String name;

    public DnaBkb(String text, Clop edge, @NotNull Clop main, String name) {
        super(text, edge, Life.namedAny(name));
        this.main = main;
        this.name = name == null ? Const.TXT$EMPTY : name;
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
        StringBuilder buff = new StringBuilder("DnaBkb{");
        buff.append("name='");
        Dent.line(buff, name);
        buff.append("'}");
        buff.append("; ").append(edge);
        buff.append("; ").append(life);
        buff.append("; ").append(life);
        return buff.toString();
    }
}
