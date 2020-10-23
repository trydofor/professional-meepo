package pro.fessional.meepo.bind.dna;

import pro.fessional.meepo.bind.Clop;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.Life;

import java.util.Map;
import java.util.Objects;

/**
 * <pre>
 * ` <% DNA:RAW {{pebbleVariable}} %> \n`
 * edge=`<% DNA:RAW {{pebbleVariable}} %>`*
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

    private final int raw0;

    public DnaRaw(String text, Clop edge, Clop main, int raw0) {
        super(text, Life.nobodyOne(), edge, main);
        this.raw0 = raw0;
    }

    @Override
    public void merge(Map<String, Object> ctx, StringBuilder buf) {
        buf.append(text, raw0, main.until);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DnaRaw dnaRaw = (DnaRaw) o;
        return Objects.equals(text.substring(raw0, main.until),
                dnaRaw.text.substring(dnaRaw.raw0, dnaRaw.main.until));
    }

    @Override
    public int hashCode() {
        return Objects.hash(text.substring(raw0, main.until));
    }

    @Override
    public String toString() {
        return "DnaRaw{" +
                "text='" + text.substring(raw0, main.until) +
                "'}";
    }
}
