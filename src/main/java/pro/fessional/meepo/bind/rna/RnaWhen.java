package pro.fessional.meepo.bind.rna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.kin.Dyn;
import pro.fessional.meepo.bind.kin.Ngx;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Eval;
import pro.fessional.meepo.bind.wow.Tock;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.poof.RnaProtein;
import pro.fessional.meepo.sack.Acid;
import pro.fessional.meepo.util.Dent;

import java.util.Objects;

/**
 * <pre>
 * ` <% RNA:WHEN os/not/basename $(pwd)/pwd %> \n`
 * edge=`<% RNA:WHEN os/not/basename $(pwd)/pwd %>`
 *
 * ` // <% RNA:WHEN os/not/basename $(pwd)/pwd %> \n`
 * edge=`<% RNA:WHEN os/not/basename $(pwd)/pwd %> \n`
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
public class RnaWhen extends Tock implements Dyn, Ngx {

    @NotNull
    public final Clop main;
    @NotNull
    public final String type;
    public final boolean nope; // not
    @NotNull
    public final String expr;
    public final boolean mute;
    @NotNull
    private final RnaProtein prot;

    public RnaWhen(String text, Clop edge, String tock, @NotNull Clop main, @NotNull String type, boolean nope, @NotNull String expr, boolean mute) {
        super(text, edge, tock);
        this.main = main;
        this.type = type;
        this.nope = nope;
        this.expr = expr;
        this.mute = mute;
        this.prot = RnaProtein.of(type);
    }

    @Override
    public void check(StringBuilder err) {
        prot.check(err, expr, this);
    }

    @Override
    public void merge(Acid acid, Appendable buff) {
        final Tock th = this;
        Tock tk = acid.execute.compute(tock, (s, t) -> {
            if (t != null) return t;

            RnaEngine eng = acid.dirty(prot);

            Object obj = eng.eval(type, expr, acid.context, mute);
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
        StringBuilder buff = new StringBuilder("RnaWhen{");
        buff.append("tock='");
        Dent.line(buff, tock);
        buff.append("', type='");
        Dent.line(buff, type);
        buff.append("', nope=");
        buff.append(nope);
        buff.append(", expr='");
        Dent.line(buff, expr);
        buff.append("', mute=");
        buff.append(mute);
        buff.append('}');
        buff.append("; ").append(edge);
        return buff.toString();
    }
}
