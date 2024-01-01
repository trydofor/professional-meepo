package pro.fessional.meepo.poof;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.eval.JavaEval;
import pro.fessional.meepo.eval.NameEval;
import pro.fessional.meepo.eval.ctrl.Elv;
import pro.fessional.meepo.eval.fmt.Case;
import pro.fessional.meepo.eval.fmt.Fmt;
import pro.fessional.meepo.eval.num.Cal;
import pro.fessional.meepo.eval.time.Now;
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
 * RnaEngine factory
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
        RnaManager.register(Cal.funAbs);

        //
        RnaManager.register(Elv.funSee);

    }

    /**
     * Create a new engine of type.
     */
    @NotNull
    public static RnaEngine newEngine(String type) {
        RnaEngine engine = engines.get(type);
        return (engine == null ? defaultEngine.get() : engine).fork();
    }

    /**
     * Register an engine with its type
     */
    public static void register(RnaEngine engine) {
        if (engine != null) {
            String clz = engine.getClass().getName();
            for (String t : engine.type()) {
                RnaEngine old = engines.put(t, engine);
                if (old == null) {
                    logger.info("register new engine for type={}, clz={}", t, clz);
                }
                else if (old == engine) {
                    logger.info("skip same engine for type={}", t);
                }
                else {
                    logger.info("replace engine for type={}, old={}, new={}", t, old.getClass().getName(), clz);
                }
            }
            engCount = engines.size();
        }
    }

    /**
     * Get a function by its name.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFunction(String name) {
        return (T) functions.get(name);
    }

    /**
     * Put functions to the context
     */
    public static void putFunction(Map<String, Object> ctx) {
        ctx.putAll(functions);
    }

    /**
     * Register a function with its name
     */
    public static void register(String name, Supplier<?> fun) {
        register(name, fun, "Supplier");
    }

    /**
     * Register a function with its name
     */
    public static void register(String name, Function<?, ?> fun) {
        register(name, fun, "Function");
    }

    /**
     * Register a function with its name
     */
    public static void register(String name, JavaEval fun) {
        register(name, fun, "JavaEval");
    }

    /**
     * Register a function with its name
     */
    public static void register(NameEval fun) {
        for (String name : fun.name()) {
            register(name, fun, fun.info());
        }
    }

    /**
     * Register a function with its name
     */
    private static void register(String name, Object fun, String info) {
        Object old = functions.put(name, fun);
        if (old == null) {
            logger.info("register function for name={}, info={}", name, info);
        }
        else if (fun == old) {
            logger.info("skip same function for name={}, info={}", name, info);
        }
        else {
            logger.info("replace function for name={}, info={}", name, info);
        }
        funCount = functions.size();
    }

    /**
     * Get the count of the registered engine
     */
    public static int getEngineCount() {
        return engCount;
    }

    /**
     * Get the count of the registered function
     */
    public static int getFunctionCount() {
        return funCount;
    }

    @NotNull
    public static RnaEngine getDefault() {
        return defaultEngine.get();
    }

    /**
     * Set and register the default engine to handle unknown type
     */
    public static void setDefault(@NotNull RnaEngine engine) {
        defaultEngine.set(engine);
        register(engine);
    }
}
