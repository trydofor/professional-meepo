package pro.fessional.meepo.eval.time;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.eval.NameEval;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    private static final String dt19 = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter full = DateTimeFormatter.ofPattern(dt19);
    private static final DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static final Supplier<String> envNowDate = () -> LocalDate.now().format(date);
    public static final Supplier<String> envNowTime = () -> LocalTime.now().format(time);
    public static final NameEval funNow = new NameEval() {
        @Override
        public @NotNull String[] name() {
            return new String[]{FUN$NOW};
        }

        @Override
        @NotNull
        public String info() {
            return "动态计算，根据pattern格式化";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            String ptn = null;
            if (arg != null && arg.length > 0 && arg[0] != null) {
                ptn = arg[0].toString();
            }
            return now(obj, ptn);
        }
    };

    // //////////////

    /**
     * 有pattern对obj进行日期格式化。
     *
     * @param obj     Date, TemporalAccessor, else(=now)
     * @param pattern DateTimeFormatter
     * @return 格式化后日期
     */
    @NotNull
    public static String now(Object obj, String pattern) {
        if (obj instanceof Date) {
            Format fmt = new SimpleDateFormat(pattern == null ? dt19 : pattern);
            return fmt.format(obj);
        }

        DateTimeFormatter fmt = pattern == null ? full : DateTimeFormatter.ofPattern(pattern);
        TemporalAccessor tm;
        if (obj instanceof TemporalAccessor) {
            tm = (TemporalAccessor) obj;
        }
        else {
            tm = LocalDateTime.now();
        }
        return fmt.format(tm);
    }
}
