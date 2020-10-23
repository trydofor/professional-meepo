package pro.fessional.meepo.poof.impl;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

/**
 * @author trydofor
 * @since 2020-10-15
 */
public class JsEngineTest {

    @Test
    public void test1() {
        JsEngine engine = new JsEngine();
        String r1 = engine.eval("js", "var me = '123 456'; me", new HashMap<>(), false);
        Assert.assertEquals("123 456", r1);
    }

    @Test
    public void test2() {
        JsEngine engine = new JsEngine();
        HashMap<String, Object> ctx = new HashMap<>();
        ctx.put("me", "trydofor");
        String r1 = engine.eval("js", "me +' $100'", ctx, false);
        Assert.assertEquals("trydofor $100", r1);

    }
}