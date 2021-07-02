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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.RandomAccess;

import static pro.fessional.meepo.bind.Const.BLT$EACH_COUNT;
import static pro.fessional.meepo.bind.Const.BLT$EACH_TOTAL;
import static pro.fessional.meepo.bind.Const.OBJ$NAVI_DOT;

/**
 * <pre>
 * ` &lt;% RNA:EACH map/1/users/user %&gt; \n`
 * edge=`&lt;% RNA:EACH map/1/users/user %&gt;`
 *
 * ` // &lt;% RNA:EACH map/1/users/user %&gt; \n`
 * edge=`&lt;% RNA:EACH map/1/users/user %&gt; \n`
 *
 * main=`RNA:EACH map/1/users/user`
 * type=`map`
 * step=`1`
 * expr=`users`
 * expr=`user`
 * </pre>
 *
 * @author trydofor
 * @since 2020-11-01
 */
public class RnaEach extends Tock implements Rng {

    @NotNull
    public final String type;
    public final int step;
    @NotNull
    public final String expr;
    public final boolean mute;

    private RnaWarmed warmed;

    public RnaEach(String text, Clop edge, String tock, @NotNull String type, int step, @NotNull String expr, boolean mute) {
        super(text, edge, tock);
        this.type = type;
        this.step = step;
        this.expr = expr;
        this.mute = mute;
    }

    @Override
    public void check(StringBuilder err, RngChecker rng) {
        warmed = rng.check(err, type, expr);
    }

    @Override
    public void merge(Acid acid, Writer buff) {
        RnaEngine eng = acid.getEngine(type);

        Map<String, Object> ctx = acid.context;
        Object obj = eng.eval(ctx, warmed, mute);

        final int size;
        if (obj instanceof Collection) {
            size = ((Collection<?>) obj).size();
        }
        else if (obj != null && obj.getClass().isArray()) {
            size = Array.getLength(obj);
        }
        else {
            size = 0;
        }

        if (size == 0) {
            logger.trace("[👹Merge:tock] skip RNA:EACH tock={}, size={}, step={}, type={}, expr={}", tock, size, step, type, expr);
        }
        else {
            logger.trace("[👹Merge:tock] deal RNA:EACH tock={}, size={}, step={}, type={}, expr={}", tock, size, step, type, expr);
            acid.execute.put(tock, this);

            final String keyTotal = tock + OBJ$NAVI_DOT + BLT$EACH_TOTAL;
            final String keyCount = tock + OBJ$NAVI_DOT + BLT$EACH_COUNT;

            ctx.put(keyTotal, size);
            ctx.put(keyCount, 0);

            loop(acid, buff, obj, size, tock, keyCount);
        }
    }

    private void loop(Acid acid, Writer buf, Object obj, int size, String keyRefer, String keyCount) {
        final Map<String, Object> ctx = acid.context;
        if (obj instanceof List && obj instanceof RandomAccess) {
            int count = 1;
            List<?> list = (List<?>) obj;
            if (step > 0) {
                for (int i = 0; i < size; i += step) {
                    ctx.put(keyRefer, list.get(i));
                    ctx.put(keyCount, count++);
                    for (Exon exon : gene) {
                        exon.merge(acid, buf);
                    }
                }
            }
            else {
                for (int i = size - 1; i >= 0; i += step) {
                    ctx.put(keyRefer, list.get(i));
                    ctx.put(keyCount, count++);
                    for (Exon exon : gene) {
                        exon.merge(acid, buf);
                    }
                }
            }
            return;
        }

        if (obj instanceof Collection) {
            int count = 1;
            Collection<?> col = (Collection<?>) obj;
            if (step > 0) {
                for (Object it : col) {
                    if (count % step != 0) continue;
                    ctx.put(keyRefer, it);
                    ctx.put(keyCount, count);
                    for (Exon exon : gene) {
                        exon.merge(acid, buf);
                    }
                    count++;
                }
                return;
            }
            else {
                if (obj instanceof Deque) {
                    for (Iterator<?> rit = ((Deque<?>) obj).descendingIterator(); rit.hasNext(); ) {
                        Object it = rit.next();
                        if (count % step != 0) continue;
                        ctx.put(keyRefer, it);
                        ctx.put(keyCount, count);
                        for (Exon exon : gene) {
                            exon.merge(acid, buf);
                        }
                        count++;
                    }
                    return;
                }
                else {
                    obj = col.toArray();
                }
            }
        }

        if (obj.getClass().isArray()) {
            int count = 1;
            if (step > 0) {
                for (int i = 0; i < size; i += step) {
                    ctx.put(keyRefer, Array.get(obj, i));
                    ctx.put(keyCount, count++);
                    for (Exon exon : gene) {
                        exon.merge(acid, buf);
                    }
                }
            }
            else {
                for (int i = size - 1; i >= 0; i += step) {
                    ctx.put(keyRefer, Array.get(obj, i));
                    ctx.put(keyCount, count++);
                    for (Exon exon : gene) {
                        exon.merge(acid, buf);
                    }
                }
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RnaEach rnaEach = (RnaEach) o;
        return mute == rnaEach.mute &&
               step == rnaEach.step &&
               tock.equals(rnaEach.tock) &&
               type.equals(rnaEach.type) &&
               expr.equals(rnaEach.expr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tock, type, step, expr, mute);
    }

    @Override
    public String toString() {
        StringWriter buff = new StringWriter();
        toString(buff);
        return buff.toString();
    }

    public void toString(Writer buff) {
        try {
            buff.append("RnaEach{");
            buff.append("tock='");
            Dent.lineIt(buff, tock);
            buff.append("', type='");
            Dent.lineIt(buff, type);
            buff.append("', step=");
            buff.write(String.valueOf(step));
            buff.append(", expr='");
            Dent.lineIt(buff, expr);
            buff.append("', mute=");
            buff.write(String.valueOf(mute));
            buff.append("}");
            buff.append("; ");
            edge.toString(buff);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
