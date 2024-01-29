package pro.fessional.meepo.bind.txt;

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

import static pro.fessional.meepo.bind.Const.ENGINE$MAP;

/**
 * Get Var from the environment
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class TxtRnaUse extends Exon implements Rng {
    @NotNull
    public final String expr;
    public final int left;
    public final String type;

    private RnaWarmed warmed;

    public TxtRnaUse(@NotNull String text, Clop edge, @NotNull String expr, int left) {
        super(text, edge);
        this.expr = expr;
        this.left = left;
        this.type = ENGINE$MAP;
    }

    @Override
    public void check(StringBuilder err, RngChecker rng) {
        warmed = rng.check(err, type, expr);
    }

    @Override
    public void merge(Acid acid, Writer buff) {
        RnaEngine eng = acid.getEngine(type);

        Object o = eng.eval(acid.context, warmed, true);
        Dent.indent(buff, left, o);
    }

    @Override
    public String toString() {
        StringWriter buff = new StringWriter();
        toString(buff);
        return buff.toString();
    }

    public void toString(Writer buff) {
        try {
            buff.append("TxtRnaUse{");
            buff.append("para='");
            Dent.lineIt(buff, expr);
            buff.append("'}");
            buff.append("; ");
            edge.toString(buff);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
