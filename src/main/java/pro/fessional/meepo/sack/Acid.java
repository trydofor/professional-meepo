package pro.fessional.meepo.sack;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.kin.Ngx;
import pro.fessional.meepo.bind.wow.Tock;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.poof.RnaManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 单线程下执行
 *
 * @author trydofor
 * @since 2020-11-02
 */
public class Acid {

    @NotNull
    private final Map<String, RnaEngine> engines;
    @NotNull
    public final Map<String, Tock> execute;
    @NotNull
    public final Map<String, Object> context;

    public Acid() {
        this.engines = new HashMap<>();
        this.context = new HashMap<>();
        this.execute = new HashMap<>();
    }

    public Acid(Map<String, Object> context) {
        this.engines = new HashMap<>();
        this.execute = new HashMap<>();
        this.context = context == null ? new HashMap<>() : context;
    }


    public RnaEngine getEngine(Ngx exon) {
        String type = exon.getType();
        return engines.computeIfAbsent(type, RnaManager::newEngine);
    }
}
