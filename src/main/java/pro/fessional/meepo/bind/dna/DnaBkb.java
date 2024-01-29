package pro.fessional.meepo.bind.dna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;
import pro.fessional.meepo.bind.wow.Tick;
import pro.fessional.meepo.util.Dent;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * <pre>
 * ` &lt;% DNA:BKB BlackKingBar %&gt; \n`
 * edge=`&lt;% DNA:BKB BlackKingBar %&gt;`
 *
 * ` // DNA:BKB BlackKingBar \n`
 * edge=`// DNA:BKB BlackKingBar \n`
 *
 * main=`DNA:BKB BlackKingBar`
 * life.name=`BlackKingBar`
 * </pre>
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class DnaBkb extends Tick {

    @NotNull
    public final String name;
    private final char[] name9;

    public DnaBkb(String text, Clop edge, @NotNull String name) {
        super(text, edge, Life.namedAny(name));
        this.name = name;
        this.name9 = name.toCharArray();
    }

    @Override
    public String toString() {
        StringWriter buff = new StringWriter();
        toString(buff);
        return buff.toString();
    }

    public void toString(Writer buff) {
        try {
            buff.append("DnaBkb{");
            buff.append("name='");
            Dent.lineIt(buff, name9);
            buff.append("'}");
            buff.append("; ");
            edge.toString(buff);
            buff.append("; ");
            life.toString(buff);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
