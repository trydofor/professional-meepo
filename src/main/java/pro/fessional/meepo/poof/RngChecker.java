package pro.fessional.meepo.poof;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author trydofor
 * @since 2020-11-05
 */
public class RngChecker {

    private final Map<String, RnaEngine> engines;
    private final Map<Key, RnaWarmed> checked;

    public RngChecker() {
        this.engines = new HashMap<>();
        this.checked = new HashMap<>();
    }

    /**
     * Warm up (check) the same expr only once
     *
     * @param err  error message to output
     * @param type engine type
     * @param expr function body
     */
    public RnaWarmed check(StringBuilder err, String type, String expr) {

        RnaWarmed warmed = checked.computeIfAbsent(new Key(type, expr), s -> {
            RnaEngine eng = getEngine(type);
            StringBuilder sb = new StringBuilder();
            boolean bad = true;
            for (String str : eng.type()) {
                if (str.equals(type)) {
                    bad = false;
                    break;
                }
            }

            if (bad) {
                sb.append("\nengine type dis-match, need=")
                  .append(type)
                  .append(", but get engine=")
                  .append(eng.getClass().getName());
            }

            RnaWarmed warm = eng.warm(type, expr);
            if (warm.hasInfo()) {
                sb.append(warm.info);
            }

            warm.info = sb.toString();
            return warm;
        });

        if (warmed.hasInfo()) {
            err.append(warmed.info);
        }

        return warmed;
    }

    public Set<String> getCheckedEngine() {
        return engines.keySet();
    }

    /**
     * Get the engine by its type
     */
    @NotNull
    public RnaEngine getEngine(String type) {
        return engines.computeIfAbsent(type, RnaManager::newEngine);
    }

    private static class Key {
        private final String type;
        private final String expr;

        public Key(String type, String expr) {
            this.type = type;
            this.expr = expr;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return Objects.equals(type, key.type) &&
                   Objects.equals(expr, key.expr);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, expr);
        }
    }
}
