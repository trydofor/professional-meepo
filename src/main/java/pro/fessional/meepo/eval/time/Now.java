package pro.fessional.meepo.eval.time;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.eval.NameEval;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

import static pro.fessional.meepo.eval.FunEnv.FUN$NOW;


/**
 * @author trydofor
 * @since 2021-01-02
 */
public class Now {

    private static final DateTimeFormatter full = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static final Supplier<String> envNowDate = () -> LocalDate.now().format(date);
    public static final Supplier<String> envNowTime = () -> LocalTime.now().format(time);
    public static final NameEval funNow = new NameEval() {
        @Override
        @NotNull
        public String name() {
            return FUN$NOW;
        }

        @Override
        @NotNull
        public String info() {
            return "动态计算，根据pattern格式化";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
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
        }
    };
}
