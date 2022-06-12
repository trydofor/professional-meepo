package pro.fessional.meepo.sack;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.wow.Tock;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.poof.RnaManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

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
    private final Map<String, RnaEngine> engines;

    /**
     * 构造一个包含ctx所有元素的新HashMap
     *
     * @param ctx 上下文
     * @param rng 引擎名
     */
    public Acid(Map<String, Object> ctx, Set<String> rng) {
        this.execute = new HashMap<>();
        this.context = new HashMap<>(ctx.size() + RnaManager.getFunctionCount());
        this.engines = rng.stream().collect(toMap(Function.identity(), RnaManager::newEngine));
        //
        RnaManager.putFunction(this.context);
        context.putAll(ctx);
    }

    public RnaEngine getEngine(String type) {
        return engines.computeIfAbsent(type, RnaManager::newEngine);
    }

    public void clear() {
        execute.clear();
        context.clear();
        engines.clear();
    }
}
