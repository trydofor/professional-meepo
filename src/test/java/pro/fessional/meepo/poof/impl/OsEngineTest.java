package pro.fessional.meepo.poof.impl;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

/**
 * @author trydofor
 * @since 2020-10-15
 */
public class OsEngineTest {

    @Test
    public void testSh() {
        OsEngine engine = new OsEngine();
        Object r1 = engine.eval("sh", "me=trydofor;echo $me \\$100", new HashMap<>(), false);
        Assert.assertEquals("trydofor $100", r1);
    }

    @Test
    public void testExe() {
        OsEngine engine = new OsEngine();
        HashMap<String, Object> ctx = new HashMap<>();
        ctx.put("me","trydofor");
        // "/bin/bash", "-c", "echo $u"
        // "cmd", "/c", "echo $u"
        Object r1 = engine.eval("exe", "/bin/bash -c \"echo $me \\$100\"", ctx, false);
        Assert.assertEquals("trydofor $100", r1);
    }
}