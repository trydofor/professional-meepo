package pro.fessional.meepo.util;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2020-11-04
 */
public class EvalTest extends TraceTest {

    @Test
    public void asFalse() {
        assertTrue(Eval.asFalse(null));
        assertTrue(Eval.asFalse(""));
        assertTrue(Eval.asFalse(false));
        assertTrue(Eval.asFalse(Boolean.FALSE));

        assertTrue(Eval.asFalse(0));
        assertTrue(Eval.asFalse(0L));
        assertTrue(Eval.asFalse(0.0D));
        assertTrue(Eval.asFalse(0.0F));
        assertTrue(Eval.asFalse(-0.0D));
        assertTrue(Eval.asFalse(-0.0F));
        assertTrue(Eval.asFalse(0.00000F));
        assertTrue(Eval.asFalse(-0.00000F));
        assertTrue(Eval.asFalse(Float.NaN));
        assertTrue(Eval.asFalse(Double.NaN));
        assertTrue(Eval.asFalse(BigDecimal.ZERO));
        assertTrue(Eval.asFalse(new BigDecimal("0.0")));
        assertTrue(Eval.asFalse(new BigDecimal("0.000")));
        assertTrue(Eval.asFalse(new BigDecimal("-0.000")));

        assertTrue(Eval.asFalse(new boolean[0]));
        assertTrue(Eval.asFalse(new byte[0]));
        assertTrue(Eval.asFalse(new short[0]));
        assertTrue(Eval.asFalse(new int[0]));
        assertTrue(Eval.asFalse(new long[0]));
        assertTrue(Eval.asFalse(new float[0]));
        assertTrue(Eval.asFalse(new double[0]));
        assertTrue(Eval.asFalse(new Object[0]));

        assertTrue(Eval.asFalse(new ArrayList<>()));
        assertTrue(Eval.asFalse(new HashMap<>()));
    }

    @Test
    public void parseArgs() {
        assertEquals(Collections.singletonList(""), Eval.parseArgs("\"\"", Eval.ArgType.Str));
        assertEquals(Collections.singletonList(12), Eval.parseArgs("12", Eval.ArgType.Obj));
        assertEquals(Collections.singletonList("12"), Eval.parseArgs("12", Eval.ArgType.Str));
        assertEquals(Collections.singletonList("12\\"), Eval.parseArgs("12\\", Eval.ArgType.Obj));
        assertEquals(Collections.singletonList("12\\"), Eval.parseArgs("12\"\\", Eval.ArgType.Obj));
        assertEquals(Arrays.asList("12\"", ""), Eval.parseArgs("12\\\" \"\"", Eval.ArgType.Obj));
        assertEquals(Arrays.asList(12, 34D), Eval.parseArgs("12 34D", Eval.ArgType.Obj));
        assertEquals(Arrays.asList(-12, 34L), Eval.parseArgs("-12 +34L", Eval.ArgType.Obj));
        assertEquals(Arrays.asList("12", "34L"), Eval.parseArgs("12 34L", Eval.ArgType.Str));
        assertEquals(Arrays.asList(12, 34F), Eval.parseArgs(" 12 34F ", Eval.ArgType.Obj));
        assertEquals(Arrays.asList(12, 34L), Eval.parseArgs(" 1_2 3,4L ", Eval.ArgType.Obj));
        assertEquals(Arrays.asList(12, 34L), Eval.parseArgs("\n 12 \n34L ", Eval.ArgType.Obj));
        assertEquals(Arrays.asList(12D, "3 4"), Eval.parseArgs(" 12D '3 4' ", Eval.ArgType.Obj));
        assertEquals(Arrays.asList(12F, "3' 4"), Eval.parseArgs(" 12F '3\\' 4' ", Eval.ArgType.Obj));
        assertEquals(Arrays.asList(new BigDecimal("12"), "3' 4"), Eval.parseArgs(" 12N \"3' 4\" ", Eval.ArgType.Obj));
        assertEquals(Arrays.asList(12, "3\" 4"), Eval.parseArgs(" 12 '3\" 4' ", Eval.ArgType.Obj));

        assertEquals(Arrays.asList("0.1.2", "34FF"), Eval.parseArgs(" 0.1.2 34FF ", Eval.ArgType.Obj));
        assertEquals(Arrays.asList("-1,2-", "34DF"), Eval.parseArgs(" -1,2- 34DF ", Eval.ArgType.Obj));
    }

    @Test
    public void split() {
        assertEquals(Arrays.asList("12", "34"), Eval.split("12|34", '|', '\\'));
        assertEquals(Collections.singletonList("1|2"), Eval.split("1\\|2", '|', '\\'));
        assertEquals(Arrays.asList("12", "34"), Eval.split("|12|34|", '|', '\\'));
        assertEquals(Arrays.asList("1|2", "34"), Eval.split("|1\\|2|34|", '|', '\\'));
        assertEquals(Arrays.asList("1\\2", "34"), Eval.split("|1\\2|34|", '|', '\\'));
        assertEquals(Arrays.asList("1\\'2", "34"), Eval.split("|1\\'2|34|", '|', '\\'));
    }
}
