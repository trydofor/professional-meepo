package pro.fessional.meepo.eval.num;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.eval.JavaEval;
import pro.fessional.meepo.sack.Gene;
import pro.fessional.meepo.sack.Holder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2021-01-05
 */
class CalTest {

    @Test
    void testMod() {
        Map<String, Object> ctx = new HashMap<>();
        String hd = "{{ index | mod zero one.go \"\" four }}";
        final Gene gene = Holder.parse(hd);
        ctx.put("index", 0);
        String s1 = gene.merge(ctx);
        assertEquals("zero", s1);
        ctx.put("index", 1);
        String s2 = gene.merge(ctx);
        assertEquals("one.go", s2);
        ctx.put("index", 2);
        String s3 = gene.merge(ctx);
        assertEquals("", s3);

        ctx.clear();
        ctx.put("index", 1);
        ctx.put("one.go", "ONE");
        String s2a = gene.merge(ctx);
        assertEquals("ONE", s2a);

        ctx.remove("one.go");
        ctx.put("one", Collections.singletonMap("go", "ONE.GO"));
        String s2b = gene.merge(ctx);
        assertEquals("ONE.GO", s2b);
    }

    @Test
    void testAbs() {
        Map<String, Object> ctx = new HashMap<>();
        String hd = "{{ index | abs}}";
        final Gene gene = Holder.parse(hd);
        ctx.put("index", -1);
        String s1 = gene.merge(ctx);
        assertEquals("1", s1);
        ctx.put("index", "-3.14");
        String s2 = gene.merge(ctx);
        assertEquals("3.14", s2);
        ctx.put("index", null);
        String s3 = gene.merge(ctx);
        assertEquals("0", s3);
    }

    private final JavaEval type = (ctx, obj, arg) -> arg[0].getClass().getSimpleName();

    private void checkType(String v, Class<?> t) {
        Map<String, Object> ctx = new HashMap<>();
        String hd = "{{ index | type " + v + "}}";
        final Gene gene = Holder.parse(hd);
        ctx.put("index", 3);
        ctx.put("fun:type", type);
        String s4 = gene.merge(ctx);
        assertEquals(t.getSimpleName(), s4);
    }

    @Test
    void testArgType() {
        checkType("1000", Integer.class);
        checkType("1000L", Long.class);
        checkType("1,000", Integer.class);
        checkType("1,000L", Long.class);
        checkType("1_000", Integer.class);
        checkType("1_000L", Long.class);
        checkType("-1000", Integer.class);
        checkType("-1000L", Long.class);
        checkType("-1,000", Integer.class);
        checkType("-1,000L", Long.class);
        checkType("-1_000", Integer.class);
        checkType("-1_000L", Long.class);
        checkType("+1000", Integer.class);
        checkType("+1000L", Long.class);
        checkType("+1,000", Integer.class);
        checkType("+1,000L", Long.class);
        checkType("+1_000", Integer.class);
        checkType("+1_000L", Long.class);

        checkType("1000.0", Float.class);
        checkType("1000.0F", Float.class);
        checkType("1000.0D", Double.class);
        checkType("1,000.0", Float.class);
        checkType("1,000.0D", Double.class);
        checkType("1_000.0", Float.class);
        checkType("1_000.0D", Double.class);
        checkType("-1000.0", Float.class);
        checkType("-1000.0D", Double.class);
        checkType("-1,000.0", Float.class);
        checkType("-1,000.0D", Double.class);
        checkType("-1_000.0", Float.class);
        checkType("-1_000.0D", Double.class);
        checkType("+1000.0", Float.class);
        checkType("+1000.0D", Double.class);
        checkType("+1,000.0", Float.class);
        checkType("+1,000.0D", Double.class);
        checkType("+1_000.0", Float.class);
        checkType("+1_000.0D", Double.class);

        checkType("1000N", BigDecimal.class);
        checkType("1,000N", BigDecimal.class);
        checkType("1_000N", BigDecimal.class);
        checkType("1000.0N", BigDecimal.class);
        checkType("1,000.0N", BigDecimal.class);
        checkType("1_000.0N", BigDecimal.class);
        checkType("-1000N", BigDecimal.class);
        checkType("-1,000N", BigDecimal.class);
        checkType("-1_000N", BigDecimal.class);
        checkType("-1000.0N", BigDecimal.class);
        checkType("-1,000.0N", BigDecimal.class);
        checkType("-1_000.0N", BigDecimal.class);
        checkType("+1000N", BigDecimal.class);
        checkType("+1,000N", BigDecimal.class);
        checkType("+1_000N", BigDecimal.class);
        checkType("+1000.0N", BigDecimal.class);
        checkType("+1,000.0N", BigDecimal.class);
        checkType("+1_000.0N", BigDecimal.class);

        checkType("DNF", String.class);
        checkType("D,N,F", String.class);
        checkType("D_N,F", String.class);
        checkType("-D_N,F", String.class);
        checkType("+D_N,F", String.class);
    }
}
