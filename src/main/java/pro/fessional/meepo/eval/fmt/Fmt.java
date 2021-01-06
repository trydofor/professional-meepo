package pro.fessional.meepo.eval.fmt;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.eval.NameEval;

import java.util.Map;

import static pro.fessional.meepo.eval.FunEnv.FUN$FMT;

/**
 * @author trydofor
 * @since 2021-01-02
 */
public class Fmt {

    public static final NameEval funFmt = new NameEval() {
        @Override
        public @NotNull String[] name() {
            return new String[]{FUN$FMT};
        }

        @Override
        public @NotNull String info() {
            return "printf格式化对象";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            return String.format((String) arg[0], obj);
        }
    };
}
