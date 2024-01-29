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

/**
 * Get Var from the Execution Engine
 *
 * @author trydofor
 * @since 2020-10-16
 */
public class TxtRnaRun extends Exon implements Rng {

    @NotNull
    public final String type;
    @NotNull
    public final String expr;
    public final boolean mute;
    public final int left;

    private RnaWarmed warmed;

    public TxtRnaRun(@NotNull String text, Clop edge, @NotNull String type, @NotNull String expr, boolean mute, int left) {
        super(text, edge);
        this.type = type;
        this.expr = expr;
        this.mute = mute;
        this.left = left;
    }

    @Override
    public void check(StringBuilder err, RngChecker rng) {
        warmed = rng.check(err, type, expr);
    }

    @Override
    public void merge(Acid acid, Writer buff) {
        RnaEngine eng = acid.getEngine(type);

        Object s = eng.eval(acid.context, warmed, mute);
        if (edge.length > 0) {
            Dent.indent(buff, left, s);
        }
    }

    @Override
    public String toString() {
        StringWriter buff = new StringWriter();
        toString(buff);
        return buff.toString();
    }

    public void toString(Writer buff) {
        try {
            buff.append("TxtRnaRun{");
            buff.append("type='");
            Dent.lineIt(buff, type);
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
