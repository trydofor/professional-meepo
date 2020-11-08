package pro.fessional.meepo.bind.wow;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.TraceTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author trydofor
 * @since 2020-11-04
 */
public class EvalTest extends TraceTest {

    @Test
    public void asFalse() {
        Assert.assertTrue(Eval.asFalse(null));
        Assert.assertTrue(Eval.asFalse(""));
        Assert.assertTrue(Eval.asFalse(false));
        Assert.assertTrue(Eval.asFalse(Boolean.FALSE));

        Assert.assertTrue(Eval.asFalse(0));
        Assert.assertTrue(Eval.asFalse(0L));
        Assert.assertTrue(Eval.asFalse(0.0D));
        Assert.assertTrue(Eval.asFalse(0.0F));
        Assert.assertTrue(Eval.asFalse(-0.0D));
        Assert.assertTrue(Eval.asFalse(-0.0F));
        Assert.assertTrue(Eval.asFalse(0.00000F));
        Assert.assertTrue(Eval.asFalse(-0.00000F));
        Assert.assertTrue(Eval.asFalse(Float.NaN));
        Assert.assertTrue(Eval.asFalse(Double.NaN));
        Assert.assertTrue(Eval.asFalse(BigDecimal.ZERO));
        Assert.assertTrue(Eval.asFalse(new BigDecimal("0.0")));
        Assert.assertTrue(Eval.asFalse(new BigDecimal("0.000")));
        Assert.assertTrue(Eval.asFalse(new BigDecimal("-0.000")));

        Assert.assertTrue(Eval.asFalse(new boolean[0]));
        Assert.assertTrue(Eval.asFalse(new byte[0]));
        Assert.assertTrue(Eval.asFalse(new short[0]));
        Assert.assertTrue(Eval.asFalse(new int[0]));
        Assert.assertTrue(Eval.asFalse(new long[0]));
        Assert.assertTrue(Eval.asFalse(new float[0]));
        Assert.assertTrue(Eval.asFalse(new double[0]));
        Assert.assertTrue(Eval.asFalse(new Object[0]));

        Assert.assertTrue(Eval.asFalse(new ArrayList<>()));
        Assert.assertTrue(Eval.asFalse(new HashMap<>()));
    }
}