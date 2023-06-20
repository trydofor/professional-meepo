package pro.fessional.meepo.eval.ctrl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.eval.NameEval;
import pro.fessional.meepo.poof.impl.map.MapHelper;

import java.util.Map;

import static pro.fessional.meepo.eval.FunEnv.FUN$SEE;

/**
 * @author trydofor
 * @since 2022-09-14
 */
public class Elv {

    public static final NameEval funSee = new NameEval() {
        @Override
        public @NotNull String[] name() {
            return new String[]{FUN$SEE};
        }

        @Override
        public @NotNull String info() {
            return "返回第一个可见值";
        }

        @Override
        public Object eval(@NotNull Map<String, Object> ctx, Object obj, Object... arg) {
            if (canSee(obj)) return obj;

            for (Object k : arg) {
                if (k instanceof CharSequence) {
                    Object v = MapHelper.arg(ctx, (CharSequence) k, true);
                    if (canSee(v)) {
                        return v;
                    }
                }
            }

            return null;
        }
    };

    public static boolean canSee(Object obj) {
        if (obj == null) return false;
        // empty string
        if (obj instanceof CharSequence && ((CharSequence) obj).length() == 0) {
            return false;
        }
        // others
        return true;
    }
}
