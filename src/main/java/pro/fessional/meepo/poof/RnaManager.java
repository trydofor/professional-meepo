package pro.fessional.meepo.poof;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.eval.JavaEval;
import pro.fessional.meepo.eval.NameEval;
import pro.fessional.meepo.eval.fmt.Case;
import pro.fessional.meepo.eval.fmt.Fmt;
import pro.fessional.meepo.eval.num.Cal;
import pro.fessional.meepo.eval.time.Now;
import pro.fessional.meepo.poof.impl.JsEngine;
import pro.fessional.meepo.poof.impl.OsEngine;
import pro.fessional.meepo.poof.impl.RawEngine;
import pro.fessional.meepo.poof.impl.UriEngine;
import pro.fessional.meepo.poof.impl.java.JavaEngine;
import pro.fessional.meepo.poof.impl.map.MapEngine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static pro.fessional.meepo.eval.FunEnv.ENV$NOW_DATE;
import static pro.fessional.meepo.eval.FunEnv.ENV$NOW_TIME;

/**
 * RnaEngine引擎工厂
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class RnaManager {

    protected static final Logger logger = LoggerFactory.getLogger(RnaManager.class);

    private static final ConcurrentHashMap<String, RnaEngine> engines = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Object> functions = new ConcurrentHashMap<>();

    private static final AtomicReference<RnaEngine> defaultEngine = new AtomicReference<>();
    private static volatile int engCount = 0;
    private static volatile int funCount = 0;

    // init engine
    static {
        setDefault(new MapEngine());

        RnaManager.register(new RawEngine());
        RnaManager.register(new UriEngine());
        RnaManager.register(new JsEngine());
        RnaManager.register(new JavaEngine());
        RnaManager.register(new OsEngine());
        //
        RnaManager.register(ENV$NOW_DATE, Now.envNowDate);
        RnaManager.register(ENV$NOW_TIME, Now.envNowTime);
        RnaManager.register(Now.funNow);
        //
        RnaManager.register(Fmt.funFmt);
        RnaManager.register(Case.funCamelCase);
        RnaManager.register(Case.funPascalCase);
        RnaManager.register(Case.funSnakeCase);
        RnaManager.register(Case.funBigSnake);
        RnaManager.register(Case.funKebabCase);
        RnaManager.register(Case.funBigKebab);
        RnaManager.register(Case.funDotCase);
        RnaManager.register(Case.funUpperCase);
        RnaManager.register(Case.funLowerCase);

        //
        RnaManager.register(Cal.funMod);
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
        return (engine == null ? defaultEngine.get() : engine).fork();
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
                } else if (old == engine) {
                    logger.info("skip same engine for type={}", t);
                } else {
                    logger.warn("replace engine for type={}, old={}, new={}", t, old.getClass().getName(), clz);
                }
            }
            engCount = engines.size();
        }
    }

    /**
     * 获得一个函数
     *
     * @param key 函数名
     * @param <T> 函数类型
     * @return 函数
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFunction(String key) {
        return (T) functions.get(key);
    }

    /**
     * 在上下文中注入function
     *
     * @param ctx 上下文
     */
    public static void putFunction(Map<String, Object> ctx) {
        ctx.putAll(functions);
    }

    /**
     * 注册一个函数变量
     *
     * @param key 函数名
     * @param fun 函数体
     */
    public static void register(String key, Supplier<?> fun) {
        register(key, fun, "Supplier");
    }

    /**
     * 注册一个函数
     *
     * @param key 函数名
     * @param fun 函数体
     */
    public static void register(String key, Function<?, ?> fun) {
        register(key, fun, "Function");
    }

    /**
     * 注册一个函数
     *
     * @param key 函数名
     * @param fun 函数体
     */
    public static void register(String key, JavaEval fun) {
        register(key, fun, "JavaEval");
    }

    public static void register(NameEval fun) {
        for (String name : fun.name()) {
            register(name, fun, fun.info());
        }
    }

    private static void register(String key, Object fun, String info) {
        Object old = functions.put(key, fun);
        if (old == null) {
            logger.info("register function for key={}, info={}", key, info);
        } else if (fun == old) {
            logger.warn("skip same function for key={}, info={}", key, info);
        } else {
            logger.warn("replace function for key={}, info={}", key, info);
        }
        funCount = functions.size();
    }

    /**
     * 当前存在的引擎数量
     *
     * @return 数量
     */
    public static int getEngineCount() {
        return engCount;
    }

    /**
     * 当前存在的函数数量
     *
     * @return 数量
     */
    public static int getFunctionCount() {
        return funCount;
    }

    @NotNull
    public static RnaEngine getDefault() {
        return defaultEngine.get();
    }

    /**
     * 设置默认值，并注册引擎
     *
     * @param engine 引擎
     */
    public static void setDefault(@NotNull RnaEngine engine) {
        defaultEngine.set(engine);
        register(engine);
    }
}
