package pro.fessional.meepo.bind.dna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.poof.RnaEngine;

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

    @NotNull
    public final Clop main;
    public final int raw0;

    public DnaRaw(String text, Clop edge, @NotNull Clop main, int raw0) {
        super(text, edge);
        this.main = main;
        this.raw0 = raw0;
    }

    @Override
    public void merge(Map<String, Object> ctx, RnaEngine eng, StringBuilder buf) {
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
