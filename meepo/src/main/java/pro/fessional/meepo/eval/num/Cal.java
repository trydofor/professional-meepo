package pro.fessional.meepo.eval.num;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.eval.NameEval;
import pro.fessional.meepo.poof.impl.map.MapHelper;

import java.math.BigDecimal;
import java.util.Map;

import static pro.fessional.meepo.eval.FunEnv.FUN$ABS;
import static pro.fessional.meepo.eval.FunEnv.FUN$MOD;


/**
 * @author trydofor
 * @since 2021-01-02
 */
public class Cal {

    public static final NameEval funMod = new NameEval() {
        @Override
        public @NotNull String[] name() {
            return new String[]{FUN$MOD};
        }

        @Override
        public @NotNull String info() {
            return "Using mod as index, get element in args";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            final int num;
            if (obj == null) {
                num = 0;
            }
            else if (obj instanceof Boolean) {
                num = (Boolean) obj ? 1 : 0;
            }
            else if (obj instanceof Number) {
                num = ((Number) obj).intValue();
            }
            else {
                num = new BigDecimal(obj.toString()).intValue();
            }

            final Object k = arg[num % arg.length];
            if (k instanceof CharSequence) {
                return MapHelper.arg(ctx, (CharSequence) k, false);
            }
            else {
                return k;
            }
        }
    };

    public static final NameEval funAbs = new NameEval() {
        @Override
        public @NotNull String[] name() {
            return new String[]{FUN$ABS};
        }

        @Override
        public @NotNull String info() {
            return "Get absolute value in Long or BigDecimal type";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (obj == null) {
                return 0L;
            }

            if (obj instanceof Boolean) {
                return (Boolean) obj ? 0L : 1L;
            }

            if (obj instanceof Byte || obj instanceof Short || obj instanceof Integer || obj instanceof Long) {
                return Math.abs(((Number) obj).longValue());
            }

            return new BigDecimal(obj.toString()).abs();
        }
    };

}
