package pro.fessional.meepo.poof.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.wow.Eval;
import pro.fessional.meepo.poof.RnaEngine;

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
import static pro.fessional.meepo.bind.Const.OBJ$NAVIGATOR;
import static pro.fessional.meepo.bind.Const.TXT$EMPTY;

/**
 * 依次从context，System.getProperty 和System.getenv 取值
 *
 * @author trydofor
 * @since 2020-10-15
 */
public class MapEngine implements RnaEngine {

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
    public @NotNull Object eval(@NotNull String type, @NotNull String expr, @NotNull Map<String, Object> ctx, boolean mute) {
        if (mute || expr.isEmpty()) return TXT$EMPTY;

        Object v = Eval.naviGet(ctx, expr, OBJ$NAVIGATOR);
        if (v != null) return v;

        String s = System.getProperty(expr);
        if (s != null) return s;

        String e = System.getenv(expr);
        if (e != null) return e;

        Supplier<String> ss = BUILTIN.get(expr);
        if (ss != null) return ss.get();

        return TXT$EMPTY;
    }

    @Override
    public @NotNull RnaEngine fork() {
        return this;
    }
}
