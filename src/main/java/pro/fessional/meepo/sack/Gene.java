package pro.fessional.meepo.sack;

import pro.fessional.meepo.bind.Dyn;
import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.rna.RnaPut;
import pro.fessional.meepo.bind.txt.TxtRnaRun;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.poof.RnaManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author trydofor
 * @since 2020-10-15
 */
public class Gene {

    private static final int magic9 = 19;
    public final String text;
    public final int size;
    private final List<Exon> exon;

    private final AtomicReference<String> cache;
    private volatile int leng = -1;

    public Gene(List<Exon> xna, String txt) {
        this.exon = new ArrayList<>(xna);
        this.text = txt;
        this.size = txt.length() + magic9;
        this.cache = new AtomicReference<>();
    }

    public String merge(Map<String, Object> ctx) {
        String s = cache.get();
        if (s != null) return s;
        if (ctx == null) ctx = new HashMap<>();

        final int len = Math.max(leng, size);
        StringBuilder buf = new StringBuilder(len);

        Map<String, RnaEngine> egx = new HashMap<>();
        boolean cac = true;
        for (Exon exon : exon) {
            if (exon instanceof Dyn) cac = false;

            RnaEngine eg = getEngine(egx, exon);
            exon.merge(ctx, eg, buf);
        }

        String rst = buf.toString();
        if (cac) {
            cache.set(rst);
        } else {
            int bl = rst.length();
            if (bl > len) {
                this.leng = bl + magic9;
            }
        }
        return rst;
    }

    public String origin() {
        return text;
    }

    /**
     * using #origin() instead
     *
     * @return template
     * @see #origin()
     */
    public String build() {
        StringBuilder buf = new StringBuilder(size);
        for (Exon exon : exon) {
            exon.build(buf);
        }
        return buf.toString();
    }

    private RnaEngine getEngine(Map<String, RnaEngine> eng, Exon exon) {
        String type = null;
        if (exon instanceof RnaPut) {
            type = ((RnaPut) exon).type;
        } else if (exon instanceof TxtRnaRun) {
            type = ((TxtRnaRun) exon).type;
        }
        if (type == null) {
            return null;
        } else {
            return eng.computeIfAbsent(type, RnaManager::newEngine);
        }
    }
}
