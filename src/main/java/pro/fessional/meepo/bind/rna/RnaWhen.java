package pro.fessional.meepo.bind.rna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.kin.Rng;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Tock;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.poof.RnaWarmed;
import pro.fessional.meepo.poof.RngChecker;
import pro.fessional.meepo.sack.Acid;
import pro.fessional.meepo.util.Dent;
import pro.fessional.meepo.util.Eval;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Objects;

/**
 * <pre>
 * ` &lt;% RNA:WHEN os/not/basename $(pwd)/pwd %&gt; \n`
 * edge=`&lt;% RNA:WHEN os/not/basename $(pwd)/pwd %&gt;`
 *
 * ` // &lt;% RNA:WHEN os/not/basename $(pwd)/pwd %&gt; \n`
 * edge=`&lt;% RNA:WHEN os/not/basename $(pwd)/pwd %&gt; \n`
 *
 * main=`RNA:WHEN os/not/basename $(pwd)/pwd`
 * type=`os`
 * bool=`not`
 * expr=`basename $(pwd)`
 * </pre>
 *
 * @author trydofor
 * @since 2020-11-01
 */
public class RnaWhen extends Tock implements Rng {

    @NotNull
    public final String type;
    public final boolean nope; // not
    @NotNull
    public final String expr;
    public final boolean mute;

    private RnaWarmed warmed;

    public RnaWhen(String text, Clop edge, String tock, @NotNull String type, boolean nope, @NotNull String expr, boolean mute) {
        super(text, edge, tock);
        this.type = type;
        this.nope = nope;
        this.expr = expr;
        this.mute = mute;
    }

    @Override
    public void check(StringBuilder err, RngChecker rng) {
        warmed = rng.check(err, type, expr);
    }

    @Override
    public void merge(Acid acid, Writer buff) {
        final Tock th = this;
        Tock tk = acid.execute.compute(tock, (s, t) -> {
            if (t != null) return t;

            RnaEngine eng = acid.getEngine(type);
            Object obj = eng.eval(acid.context, warmed, mute);
            boolean af = Eval.asFalse(obj);

            if (nope == af) {
                return th;
            } else {
                return null;
            }
        });

        if (tk == th) {
            logger.trace("[👹Merge:tock] deal RNA:WHEN tock={}, type={}, expr={}", tock, type, expr);
            for (Exon exon : gene) {
                exon.merge(acid, buff);
            }
        } else {
            logger.trace("[👹Merge:tock] skip RNA:WHEN tock={}, type={}, expr={}", tock, type, expr);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RnaWhen rnaWhen = (RnaWhen) o;
        return nope == rnaWhen.nope &&
                mute == rnaWhen.mute &&
                tock.equals(rnaWhen.tock) &&
                type.equals(rnaWhen.type) &&
                expr.equals(rnaWhen.expr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tock, type, nope, expr, mute);
    }

    @Override
    public String toString() {
        StringWriter buff = new StringWriter();
        toString(buff);
        return buff.toString();
    }

    public void toString(Writer buff) {
        try {
            buff.append("RnaWhen{");
            buff.append("tock='");
            Dent.lineIt(buff, tock);
            buff.append("', type='");
            Dent.lineIt(buff, type);
            buff.append("', nope=");
            buff.append(String.valueOf(nope));
            buff.append(", expr='");
            Dent.lineIt(buff, expr);
            buff.append("', mute=");
            buff.append(String.valueOf(mute));
            buff.append("}");
            edge.toString(buff);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
