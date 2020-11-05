package pro.fessional.meepo.poof;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.poof.impl.JavaEngine;
import pro.fessional.meepo.poof.impl.JsEngine;
import pro.fessional.meepo.poof.impl.MapEngine;
import pro.fessional.meepo.poof.impl.OsEngine;
import pro.fessional.meepo.poof.impl.RawEngine;
import pro.fessional.meepo.poof.impl.UriEngine;

import java.util.concurrent.ConcurrentHashMap;

/**
 * RnaEngine引擎工厂
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class RnaManager {

    protected static final Logger logger = LoggerFactory.getLogger(RnaManager.class);
    private static final ConcurrentHashMap<String, RnaEngine> engines = new ConcurrentHashMap<>();
    private static volatile int count = 0;

    private static RnaEngine defaultEngine = new MapEngine();

    // init engine
    static {
        register(defaultEngine);
        register(new RawEngine());
        register(new UriEngine());
        register(new JsEngine());
        register(new JavaEngine());
        register(new OsEngine());
    }

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
            String clz = engine.getClass().getName();
            for (String t : engine.type()) {
                RnaEngine old = engines.put(t, engine);
                if (old == null) {
                    logger.info("register new engine for type={}, clz={}", t, clz);
                } else {
                    logger.warn("replace engine for type={}, old={}, new={}", t, old.getClass().getName(), clz);
                }
            }
            count = engines.size();
        }
    }

    /**
     * 当前存在的引擎数量
     *
     * @return 数量
     */
    public static int getCount() {
        return count;
    }

    @NotNull
    public static RnaEngine getDefault() {
        return defaultEngine;
    }

    public static void setDefault(@NotNull RnaEngine engine) {
        defaultEngine = engine;
    }
}
