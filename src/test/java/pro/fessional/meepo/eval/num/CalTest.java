package pro.fessional.meepo.eval.num;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.eval.JavaEval;
import pro.fessional.meepo.sack.Gene;
import pro.fessional.meepo.sack.Holder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2021-01-05
 */
class CalTest {

    @Test
    void testFmtHolder() {
        Map<String, Object> ctx = new HashMap<>();
        String hd = "{{ index | mod even odd}}";
        final Gene gene = Holder.parse(hd);
        ctx.put("index", 3);
        String s3 = gene.merge(ctx);
        assertEquals("odd", s3);
        ctx.put("index", 4);
        String s4 = gene.merge(ctx);
        assertEquals("even", s4);

    }

    private final JavaEval type = (ctx, obj, arg) -> arg[0].getClass().getSimpleName();

    private void test(String v, Class<?> t) {
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
        test("1000", Integer.class);
        test("1000L", Long.class);
        test("1,000", Integer.class);
        test("1,000L", Long.class);
        test("1_000", Integer.class);
        test("1_000L", Long.class);
        test("-1000", Integer.class);
        test("-1000L", Long.class);
        test("-1,000", Integer.class);
        test("-1,000L", Long.class);
        test("-1_000", Integer.class);
        test("-1_000L", Long.class);
        test("+1000", Integer.class);
        test("+1000L", Long.class);
        test("+1,000", Integer.class);
        test("+1,000L", Long.class);
        test("+1_000", Integer.class);
        test("+1_000L", Long.class);

        test("1000.0", Float.class);
        test("1000.0F", Float.class);
        test("1000.0D", Double.class);
        test("1,000.0", Float.class);
        test("1,000.0D", Double.class);
        test("1_000.0", Float.class);
        test("1_000.0D", Double.class);
        test("-1000.0", Float.class);
        test("-1000.0D", Double.class);
        test("-1,000.0", Float.class);
        test("-1,000.0D", Double.class);
        test("-1_000.0", Float.class);
        test("-1_000.0D", Double.class);
        test("+1000.0", Float.class);
        test("+1000.0D", Double.class);
        test("+1,000.0", Float.class);
        test("+1,000.0D", Double.class);
        test("+1_000.0", Float.class);
        test("+1_000.0D", Double.class);

        test("1000N", BigDecimal.class);
        test("1,000N", BigDecimal.class);
        test("1_000N", BigDecimal.class);
        test("1000.0N", BigDecimal.class);
        test("1,000.0N", BigDecimal.class);
        test("1_000.0N", BigDecimal.class);
        test("-1000N", BigDecimal.class);
        test("-1,000N", BigDecimal.class);
        test("-1_000N", BigDecimal.class);
        test("-1000.0N", BigDecimal.class);
        test("-1,000.0N", BigDecimal.class);
        test("-1_000.0N", BigDecimal.class);
        test("+1000N", BigDecimal.class);
        test("+1,000N", BigDecimal.class);
        test("+1_000N", BigDecimal.class);
        test("+1000.0N", BigDecimal.class);
        test("+1,000.0N", BigDecimal.class);
        test("+1_000.0N", BigDecimal.class);

        test("DNF", String.class);
        test("D,N,F", String.class);
        test("D_N,F", String.class);
        test("-D_N,F", String.class);
        test("+D_N,F", String.class);
    }
}
