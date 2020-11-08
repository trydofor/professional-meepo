package pro.fessional.meepo.poof.impl.map;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.poof.RnaEngine;
import pro.fessional.meepo.poof.RnaWarmed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static pro.fessional.meepo.bind.Const.ENGINE$MAP;
import static pro.fessional.meepo.bind.Const.KEY$ENVS_NOW;
import static pro.fessional.meepo.bind.Const.KEY$ENVS_NOW_DATE;
import static pro.fessional.meepo.bind.Const.KEY$ENVS_NOW_TIME;

/**
 * 依次从context，System.getProperty 和System.getenv 取值
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class MapEngine implements RnaEngine {

    private static final Logger logger = LoggerFactory.getLogger(MapEngine.class);

    private static final String[] TYPE = {ENGINE$MAP};

    public static final Map<String, Supplier<String>> BUILTIN = new HashMap<>();

    static {
        DateTimeFormatter full = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm:ss");
        BUILTIN.put(KEY$ENVS_NOW, () -> LocalDateTime.now().format(full));
        BUILTIN.put(KEY$ENVS_NOW_DATE, () -> LocalDate.now().format(date));
        BUILTIN.put(KEY$ENVS_NOW_TIME, () -> LocalTime.now().format(time));
    }

    @Override
    public @NotNull String[] type() {
        return TYPE;
    }

    @Override
    public @NotNull RnaWarmed warm(@NotNull String type, @NotNull String expr) {
        return new RnaWarmed(type, expr, AttrGetter.nav(expr));
    }

    @Override
    public Object eval(@NotNull Map<String, Object> ctx, @NotNull RnaWarmed expr, boolean mute) {

        Object obj = null;
        try {
            obj = AttrGetter.get(ctx, expr.expr, (String[]) expr.work);
            if (obj == null) {
                obj = System.getProperty(expr.expr);
            }

            if (obj == null) {
                obj = System.getenv(expr.expr);
            }

            if (obj == null) {
                Supplier<String> ss = BUILTIN.get(expr.expr);
                obj = ss == null ? null : ss.get();
            }
        } catch (Throwable t) {
            if (mute) {
                logger.warn("mute failed-eval " + expr, t);
            } else {
                throw new IllegalStateException(expr.expr, t);
            }
        }

        return obj;
    }

    @Override
    public @NotNull RnaEngine fork() {
        return this;
    }
}
