package pro.fessional.meepo.poof;

import org.jetbrains.annotations.NotNull;
import pro.fessional.meepo.bind.Const;
import pro.fessional.meepo.bind.Exon;

import java.util.HashMap;
import java.util.Map;

/**
 * ThreadLocal<RnaEngine>
 *
 * @author trydofor
 * @since 2020-11-05
 */
public class RnaProtein {

    private final ThreadLocal<RnaEngine> engine;
    private final String type;
    private final HashMap<String, String> checked = new HashMap<>();

    private RnaProtein(String type) {
        this.type = type;
        engine = ThreadLocal.withInitial(() -> RnaManager.newEngine(type));
    }

    /**
     * 同一expr仅check一次
     *
     * @param err  输出
     * @param expr 功能体
     * @param xna  所在xna
     */
    public void check(StringBuilder err, String expr, Exon xna) {
        String s = checked.get(expr);
        if (s != null) {
            err.append(s);
            return;
        }

        RnaEngine eng = engine.get();

        int bad = 1;
        for (String str : eng.type()) {
            if (str.equals(type)) {
                bad = 0;
                break;
            }
        }

        int off = err.length();
        if (bad > 0) {
            err.append("\nengine dis-match, ");
        }

        String ms = eng.warm(type, expr);
        if (ms != null) {
            bad++;
            err.append("\nengine warm failed ");
            err.append(ms);
        }

        String msg = Const.TXT$EMPTY;
        if (bad > 0) {
            err.append("\n");
            err.append(xna.toString());
            msg = err.substring(off, err.length());
        }
        checked.put(expr, msg);
    }

    /**
     * 合并时，脏标记
     *
     * @return 引擎
     */
    @NotNull
    public RnaEngine dirty() {
        return engine.get();
    }

    /**
     * 一次合并后，清理引擎上下文
     */
    public void clean() {
        engine.remove();
    }

    private static final ThreadLocal<Map<String, RnaProtein>> TON = ThreadLocal.withInitial(HashMap::new);

    /**
     * 同一线程内，同一type，必须分配同一个对象
     *
     * @param type 引擎类型
     * @return RnaProtein
     */
    public static RnaProtein of(String type) {
        Map<String, RnaProtein> map = TON.get();
        return map.computeIfAbsent(type, RnaProtein::new);
    }
}
