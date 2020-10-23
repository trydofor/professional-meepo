package pro.fessional.meepo.poof;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.poof.impl.MapEngine;

import java.util.concurrent.ConcurrentHashMap;

/**
 * RnaEngine引擎工厂
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class RnaManager {

    private static final ConcurrentHashMap<String, RnaEngine> engines = new ConcurrentHashMap<>();

    private static RnaEngine defaultEngine = new MapEngine();

    /**
     * 返回一个新的引擎
     *
     * @param type 引擎类型
     * @return 引擎
     */
    @NotNull
    public static RnaEngine newEngine(String type) {
        RnaEngine engine = engines.get(type);
        return engine == null ? defaultEngine : engine.fork();
    }


    /**
     * 根据 engine的type注册
     *
     * @param engine 引擎
     */
    public static void register(RnaEngine engine) {
        if (engine != null) {
            for (String t : engine.type()) {
                engines.put(t, engine);
            }
        }
    }

    /**
     * 注册一个引擎
     *
     * @param type   类型
     * @param engine 引擎
     */
    public static void register(String type, RnaEngine engine) {
        if (engine != null && type != null) {
            engines.put(type, engine);
        }
    }

    @NotNull
    public static RnaEngine getDefaultEngine() {
        return defaultEngine;
    }

    public static void setDefaultEngine(@NotNull RnaEngine engine) {
        defaultEngine = engine;
    }
}
