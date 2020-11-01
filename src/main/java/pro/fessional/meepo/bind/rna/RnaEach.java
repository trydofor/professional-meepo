package pro.fessional.meepo.bind.rna;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.kin.Dyn;
import pro.fessional.meepo.bind.kin.Ngx;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Tock;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.sack.Acid;
import pro.fessional.meepo.util.Dent;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.RandomAccess;

import static pro.fessional.meepo.bind.Const.DOT$EACH_COUNT;
import static pro.fessional.meepo.bind.Const.DOT$EACH_TOTAL;

/**
 * <pre>
 * ` <% RNA:EACH map/1/users/user %> \n`
 * edge=`<% RNA:EACH map/1/users/user %>`
 *
 * ` // <% RNA:EACH map/1/users/user %> \n`
 * edge=`<% RNA:EACH map/1/users/user %> \n`
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
public class RnaEach extends Tock implements Dyn, Ngx {

    @NotNull
    public final Clop main;
    @NotNull
    public final String type;
    public final int step;
    @NotNull
    public final String expr;
    public final boolean mute;

    public RnaEach(String text, Clop edge, String tock, @NotNull Clop main, @NotNull String type, int step, @NotNull String expr, boolean mute) {
        super(text, edge, tock);
        this.main = main;
        this.type = type;
        this.step = step;
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
        Object obj = eng.eval(type, expr, ctx, mute);

        final int size;
        if (obj instanceof Collection) {
            size = ((Collection<?>) obj).size();
        } else if (obj.getClass().isArray()) {
            size = Array.getLength(obj);
        } else {
            size = 0;
        }

        if (size == 0) {
            logger.trace("[👹Merge:tock] skip RNA:EACH tock={}, size={}, step={}, type={}, expr={}", tock, size, step, type, expr);
        } else {
            logger.trace("[👹Merge:tock] deal RNA:EACH tock={}, size={}, step={}, type={}, expr={}", tock, size, step, type, expr);
            acid.execute.put(tock, this);

            final String keyTotal = tock + DOT$EACH_TOTAL;
            final String keyCount = tock + DOT$EACH_COUNT;

            ctx.put(keyTotal, size);
            ctx.put(keyCount, 0);
            loop(acid, buf, obj, size, tock, keyCount);
        }
    }

    private void loop(Acid acid, StringBuilder buf, Object obj, int size, String keyRefer, String keyCount) {
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
            } else {
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
            } else {
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
                } else {
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
            } else {
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
        StringBuilder buff = new StringBuilder("RnaEach{");
        buff.append("tock='");
        Dent.line(buff, tock);
        buff.append("', type='");
        Dent.line(buff, type);
        buff.append("', step=");
        buff.append(step);
        buff.append(", expr='");
        Dent.line(buff, expr);
        buff.append("', mute=");
        buff.append(mute);
        buff.append("}");
        buff.append("; ").append(edge);
        return buff.toString();
    }
}
