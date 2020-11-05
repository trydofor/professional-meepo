package pro.fessional.meepo.sack;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.wow.Tock;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.poof.RnaProtein;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单线程下执行
 *
 * @author trydofor
 * @since 2020-11-02
 */
public class Acid {

    @NotNull
    public final Map<String, Tock> execute;
    @NotNull
    public final Map<String, Object> context;
    @NotNull
    public final List<RnaProtein> protein;

    public Acid() {
        this(new HashMap<>(), 0);
    }

    public Acid(Map<String, Object> context, int rna) {
        this.execute = new HashMap<>();
        this.protein = new ArrayList<>(Math.max(rna, 16));
        this.context = context == null ? new HashMap<>() : context;
    }

    public RnaEngine dirty(RnaProtein engine) {
        return engine.dirty();
    }

    public int clean() {
        execute.clear();
        int size = protein.size();
        for (RnaProtein rna : protein) {
            rna.clean();
        }
        return size;
    }

}
