package pro.fessional.meepo.poof.impl;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.poof.RnaWarmed;

import java.util.HashMap;

/**
 * @author trydofor
 * @since 2020-10-15
 */
public class OsEngineTest extends TraceTest {

    @Test
    public void testSh() {
        OsEngine engine = new OsEngine();
        RnaWarmed warmed = engine.warm("sh", "me=trydofor;echo $me \\$100");
        Object r1 = engine.eval(new HashMap<>(), warmed, false);
        Assert.assertEquals("trydofor $100", r1);
    }

    @Test
    public void testExe() {
        OsEngine engine = new OsEngine();
        HashMap<String, Object> ctx = new HashMap<>();
        ctx.put("me", "trydofor");
        // "/bin/bash", "-c", "echo $u"
        // "cmd", "/c", "echo $u"
        RnaWarmed warmed = engine.warm("exe", "/bin/bash -c \"echo $me \\$100\"");
        Object r1 = engine.eval(ctx, warmed, false);
        Assert.assertEquals("trydofor $100", r1);
    }
}