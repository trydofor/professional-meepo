package pro.fessional.meepo.sack;

import pro.fessional.meepo.bind.Exon;
import pro.fessional.meepo.bind.rna.RnaPut;
import pro.fessional.meepo.bind.txt.TxtRnaRun;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.poof.RnaManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2020-10-15
 */
public class Gene {

    public final String text;
    public final int size;
    private final List<Exon> exon;

    public Gene(List<Exon> exon, String text) {
        this.exon = new ArrayList<>(exon);
        this.text = text;
        this.size = text.length();
    }

    public String merge() {
        StringBuilder buf = new StringBuilder(size);
        Map<String, Object> ctx = new HashMap<>();
        Map<String, RnaEngine> egx = new HashMap<>();
        for (Exon exon : exon) {
            RnaEngine eg = getEngine(egx, exon);
            exon.merge(ctx, eg, buf);
        }
        return buf.toString();
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
