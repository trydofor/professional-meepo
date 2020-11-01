package pro.fessional.meepo.bind.rna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.kin.Dyn;
import pro.fessional.meepo.bind.kin.Ngx;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.sack.Acid;
import pro.fessional.meepo.util.Dent;

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
public class RnaPut extends Exon implements Dyn, Ngx {

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
    public @NotNull String getType() {
        return type;
    }

    @Override
    public void merge(Acid acid, StringBuilder buf) {
        RnaEngine eng = acid.getEngine(this);
        if (eng == null) return;

        Map<String, Object> ctx = acid.context;
        Object s = eng.eval(type, expr, ctx, mute);
        ctx.put(para, s);
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
        StringBuilder buff = new StringBuilder("RnaPut{");
        buff.append("type='");
        Dent.line(buff, type);
        buff.append("', para='");
        Dent.line(buff, para);
        buff.append("', expr='");
        Dent.line(buff, expr);
        buff.append("', mute=");
        buff.append(mute);
        buff.append("}");
        buff.append("; ").append(edge);
        return buff.toString();
    }
}
