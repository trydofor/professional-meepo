package pro.fessional.meepo.bind.dna;

import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.sack.Acid;
import pro.fessional.meepo.util.Dent;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * <pre>
 * ` &lt;% DNA:RAW {{pebbleVariable}} %&gt; \n`
 * edge=`&lt;% DNA:RAW {{pebbleVariable}} %&gt;`*
 * ` // DNA:RAW {{pebbleVariable}} \n`
 * edge=`// DNA:RAW {{pebbleVariable}} \n`
 *
 * main=`DNA:RAW {{pebbleVariable}}`
 * raw0=`{{pebbleVariable}}`.indexOf("{{")
 * </pre>
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class DnaRaw extends Exon {

    private final char[] body9;

    public DnaRaw(String text, Clop edge, int raw0, int raw1) {
        super(text, edge);
        this.body9 = Dent.chars(text, raw0, raw1);
    }

    @Override
    public void merge(Acid acid, Writer buff) {
        Dent.write(buff, body9);
    }

    @Override
    public String toString() {
        StringWriter buff = new StringWriter();
        toString(buff);
        return buff.toString();
    }

    public void toString(Writer buff) {
        try {
            buff.append("DnaRaw{");
            buff.append("text='");
            Dent.lineIt(buff, body9);
            buff.append("'}");
            buff.append("; ");
            edge.toString(buff);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
