package pro.fessional.meepo.poof.impl;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.poof.RnaWarmed;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-10-15
 */
public class JsEngineTest extends TraceTest {

    @Test
    public void test1() {
        JsEngine engine = new JsEngine();
        Object r1 = engine.eval(new HashMap<>(), new RnaWarmed("js", "var me = '123 456'; me"), false);
        assertEquals("123 456", r1);
    }

    @Test
    public void test2() {
        JsEngine engine = new JsEngine();
        HashMap<String, Object> ctx = new HashMap<>();
        ctx.put("me", "trydofor");
        Object r1 = engine.eval(ctx, new RnaWarmed("js", "ctx.me +' $100'"), false);
        assertEquals("trydofor $100", r1);

    }
}