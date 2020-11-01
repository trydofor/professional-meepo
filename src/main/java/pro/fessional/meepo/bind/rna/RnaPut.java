package pro.fessional.meepo.bind.rna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.mark.Dyn;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.poof.RnaEngine;

import java.util.Map;
import java.util.Objects;

/**
 * <pre>
 * ` <% RNA:PUT os/who/basename $(pwd)/ %> \n`
 * edge=`<% RNA:PUT os/who/basename $(pwd)/ %>`
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
public class RnaPut extends Exon implements Dyn {

    @NotNull
    public final Clop main;
    @NotNull
    public final String type;
    @NotNull
    public final String para;
    @NotNull
    public final String expr;
    public final boolean mute;


    public RnaPut(String text, Clop edge, @NotNull Clop main, @NotNull String type, @NotNull String para, @NotNull String expr, boolean mute) {
        super(text, edge);
        this.main = main;
        this.type = type;
        this.para = para;
        this.expr = expr;
        this.mute = mute;
    }

    @Override
    public void merge(Map<String, Object> ctx, RnaEngine eng, StringBuilder buf) {
        if (eng != null) {
            Object s = eng.eval(type, expr, ctx, mute);
            ctx.put(para, s);
        }
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
        return "RnaPut{" +
                "type='" + type + '\'' +
                ", para='" + para + '\'' +
                ", expr='" + expr + '\'' +
                ", quiet=" + mute +
                '}';
    }
}
