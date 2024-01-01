package pro.fessional.meepo.bind.rna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.kin.Rng;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.poof.RnaWarmed;
import pro.fessional.meepo.poof.RngChecker;
import pro.fessional.meepo.sack.Acid;
import pro.fessional.meepo.util.Dent;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;

/**
 * <pre>
 * ` &lt;% RNA:PUT os/who/basename $(pwd)/ %&gt; \n`
 * edge=`&lt;% RNA:PUT os/who/basename $(pwd)/ %&gt;`
 *
 * ` // RNA:PUT os/who/basename $(pwd)/ \n`
 * edge=`// RNA:PUT os/who/basename $(pwd)/ \n`
 *
 * main=`RNA:PUT os/who/basename $(pwd)/`
 * type=`os`
 * name=`who`
 * expr=`basename $(pwd)`
 * </pre>
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class RnaPut extends Exon implements Rng {

    @NotNull
    public final String type;
    @NotNull
    public final String para;
    @NotNull
    public final String expr;
    public final boolean mute;

    private RnaWarmed warmed;

    public RnaPut(String text, Clop edge, @NotNull String type, @NotNull String para, @NotNull String expr, boolean mute) {
        super(text, edge);
        this.type = type;
        this.para = para;
        this.expr = expr;
        this.mute = mute;
    }

    @Override
    public void check(StringBuilder err, RngChecker rng) {
        warmed = rng.check(err, type, expr);
    }

    @Override
    public void merge(Acid acid, Writer buff) {
        if (para.isEmpty() || expr.isEmpty()) return;
        RnaEngine eng = acid.getEngine(type);
        Map<String, Object> ctx = acid.context;
        Object obj = eng.eval(ctx, warmed, mute);
        ctx.put(para, obj);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RnaPut rnaPut = (RnaPut) o;
        return type.equals(rnaPut.type) &&
               para.equals(rnaPut.para) &&
               expr.equals(rnaPut.expr) &&
               mute == rnaPut.mute;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, para, expr, mute);
    }

    @Override
    public String toString() {
        StringWriter buff = new StringWriter();
        toString(buff);
        return buff.toString();
    }

    public void toString(Writer buff) {
        try {
            buff.append("RnaPut{");
            buff.append("type='");
            Dent.lineIt(buff, type);
            buff.append("', para='");
            Dent.lineIt(buff, para);
            buff.append("', expr='");
            Dent.lineIt(buff, expr);
            buff.append("', mute=");
            buff.append(String.valueOf(mute));
            buff.append("}");
            buff.append("; ");
            edge.toString(buff);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
