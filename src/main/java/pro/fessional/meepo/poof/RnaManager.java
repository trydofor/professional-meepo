package pro.fessional.meepo.poof;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.poof.impl.JsEngine;
import pro.fessional.meepo.poof.impl.OsEngine;
import pro.fessional.meepo.poof.impl.RawEngine;
import pro.fessional.meepo.poof.impl.UriEngine;
import pro.fessional.meepo.poof.impl.java.JavaEngine;
import pro.fessional.meepo.poof.impl.java.JavaEval;
import pro.fessional.meepo.poof.impl.map.MapEngine;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static pro.fessional.meepo.bind.Const.KEY$ENVS_NOW_DATE;
import static pro.fessional.meepo.bind.Const.KEY$ENVS_NOW_TIME;
import static pro.fessional.meepo.bind.Const.KEY$FUNC_FMT;
import static pro.fessional.meepo.bind.Const.KEY$FUNC_MOD;
import static pro.fessional.meepo.bind.Const.KEY$FUNC_NOW;

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
    private static volatile int count = 0;

    // init engine
    static {
        setDefault(new MapEngine());

        RnaManager.register(new RawEngine());
        RnaManager.register(new UriEngine());
        RnaManager.register(new JsEngine());
        RnaManager.register(new JavaEngine());
        RnaManager.register(new OsEngine());

        DateTimeFormatter full = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm:ss");

        RnaManager.register(KEY$ENVS_NOW_DATE, () -> LocalDate.now().format(date));
        RnaManager.register(KEY$ENVS_NOW_TIME, () -> LocalTime.now().format(time));
        RnaManager.register(KEY$FUNC_NOW, (ctx, obj, arg) -> {
            DateTimeFormatter df = full;
            if (arg != null && arg.length > 0) {
                df = DateTimeFormatter.ofPattern((String) arg[0]);
            }
            TemporalAccessor tm;
            if (obj instanceof TemporalAccessor) {
                tm = (TemporalAccessor) obj;
            } else if (obj instanceof Date) {
                tm = Instant.ofEpochMilli(((Date) obj).getTime()).atZone(ZoneOffset.UTC);
            } else {
                tm = LocalDateTime.now();
            }
            return df.format(tm);
        });

        RnaManager.register(KEY$FUNC_FMT, (ctx, obj, arg) -> String.format((String) arg[0], obj));
        RnaManager.register(KEY$FUNC_MOD, (ctx, obj, arg) -> arg[((Number) obj).intValue() % arg.length]);
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
            count = engines.size();
        }
    }

    /**
     * 获得一个函数
     *
     * @param key 函数名
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

    private static void register(String key, Object fun, String info) {
        Object old = functions.put(key, fun);
        if (old == null) {
            logger.info("register function for key={}, info={}", key, info);
        } else if (fun == old) {
            logger.warn("skip same function for key={}, info={}", key, info);
        } else {
            logger.warn("replace function for key={}, info={}", key, info);
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
