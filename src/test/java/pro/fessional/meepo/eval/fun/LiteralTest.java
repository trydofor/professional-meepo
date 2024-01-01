package pro.fessional.meepo.eval.fun;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.eval.JavaEval;
import pro.fessional.meepo.sack.Gene;
import pro.fessional.meepo.sack.Holder;
import pro.fessional.meepo.util.Eval;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2022-06-11
 */
public class LiteralTest {

    private final JavaEval type = (ctx, obj, arg) -> arg[0] + "@" + arg[0].getClass().getSimpleName();

    private void checkType(String v, Class<?> t, String s) {
        Map<String, Object> ctx = new HashMap<>();
        String hd = "{{ index | type " + v + "}}";
        final Gene gene = Holder.parse(hd);
        ctx.put("index", 3);
        ctx.put("fun:type", type);
        String s4 = gene.merge(ctx);
        assertEquals(s + "@" + t.getSimpleName(), s4);
    }

    @Test
    void testIntegerArg() {
        checkType("1000", Integer.class, "1000");
        checkType("1000L", Long.class, "1000");
        checkType("1,000", Integer.class, "1000");
        checkType("1,000L", Long.class, "1000");
        checkType("1_000", Integer.class, "1000");
        checkType("1_000L", Long.class, "1000");
        checkType("-1000", Integer.class, "-1000");
        checkType("-1000L", Long.class, "-1000");
        checkType("-1,000", Integer.class, "-1000");
        checkType("-1,000L", Long.class, "-1000");
        checkType("-1_000", Integer.class, "-1000");
        checkType("-1_000L", Long.class, "-1000");
        checkType("+1000", Integer.class, "1000");
        checkType("+1000L", Long.class, "1000");
        checkType("+1,000", Integer.class, "1000");
        checkType("+1,000L", Long.class, "1000");
        checkType("+1_000", Integer.class, "1000");
        checkType("+1_000L", Long.class, "1000");
    }

    @Test
    void testFloatArg() {
        checkType("1000.0", Float.class, "1000.0");
        checkType("1000.0F", Float.class, "1000.0");
        checkType("1000.0D", Double.class, "1000.0");
        checkType("1,000.0", Float.class, "1000.0");
        checkType("1,000.0D", Double.class, "1000.0");
        checkType("1_000.0", Float.class, "1000.0");
        checkType("1_000.0D", Double.class, "1000.0");
        checkType("-1000.0", Float.class, "-1000.0");
        checkType("-1000.0D", Double.class, "-1000.0");
        checkType("-1,000.0", Float.class, "-1000.0");
        checkType("-1,000.0D", Double.class, "-1000.0");
        checkType("-1_000.0", Float.class, "-1000.0");
        checkType("-1_000.0D", Double.class, "-1000.0");
        checkType("+1000.0", Float.class, "1000.0");
        checkType("+1000.0D", Double.class, "1000.0");
        checkType("+1,000.0", Float.class, "1000.0");
        checkType("+1,000.0D", Double.class, "1000.0");
        checkType("+1_000.0", Float.class, "1000.0");
        checkType("+1_000.0D", Double.class, "1000.0");
    }

    @Test
    void testDecimalArg() {
        checkType("1000N", BigDecimal.class, "1000");
        checkType("1,000N", BigDecimal.class, "1000");
        checkType("1_000N", BigDecimal.class, "1000");
        checkType("1000.0N", BigDecimal.class, "1000.0");
        checkType("1,000.0N", BigDecimal.class, "1000.0");
        checkType("1_000.0N", BigDecimal.class, "1000.0");
        checkType("-1000N", BigDecimal.class, "-1000");
        checkType("-1,000N", BigDecimal.class, "-1000");
        checkType("-1_000N", BigDecimal.class, "-1000");
        checkType("-1000.0N", BigDecimal.class, "-1000.0");
        checkType("-1,000.0N", BigDecimal.class, "-1000.0");
        checkType("-1_000.0N", BigDecimal.class, "-1000.0");
        checkType("+1000N", BigDecimal.class, "1000");
        checkType("+1,000N", BigDecimal.class, "1000");
        checkType("+1_000N", BigDecimal.class, "1000");
        checkType("+1000.0N", BigDecimal.class, "1000.0");
        checkType("+1,000.0N", BigDecimal.class, "1000.0");
        checkType("+1_000.0N", BigDecimal.class, "1000.0");
    }

    @Test
    void testBooleanArg() {
        checkType("TRUE", Boolean.class, "true");
        checkType("FALSE", Boolean.class, "false");
    }

    @Test
    void testStringArg() {
        checkType("DNF", Eval.RefStr.class, "DNF");
        checkType("D,N,F", Eval.RefStr.class, "D,N,F");
        checkType("D_N,F", Eval.RefStr.class, "D_N,F");
        checkType("-D_N,F", Eval.RefStr.class, "-D_N,F");
        checkType("+D_N,F", Eval.RefStr.class, "+D_N,F");
        checkType("\"1\"", String.class, "1");
        checkType("'1'", String.class, "1");
        checkType("'1L'", String.class, "1L");
        checkType("'1F'", String.class, "1F");
        checkType("'1D'", String.class, "1D");
        checkType("'1N'", String.class, "1N");
        checkType("'TRUE'", String.class, "TRUE");
        checkType("'FALSE'", String.class, "FALSE");
    }
}
