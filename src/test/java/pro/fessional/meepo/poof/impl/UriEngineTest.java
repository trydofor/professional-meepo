package pro.fessional.meepo.poof.impl;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.poof.RnaWarmed;

import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2020-10-31
 */
public class UriEngineTest extends TraceTest {

    UriEngine engine = new UriEngine();
    Map<String, Object> context = new HashMap<>();

    @Test
    public void evalFile() {
        Object t1 = engine.eval(context, new RnaWarmed("uri", "file://./readme.md"), false);
        Object t2 = engine.eval(context, new RnaWarmed("uri", "file://./readme.md"), false);
        Object t3 = engine.eval(context, new RnaWarmed("uri", "./readme.md"), false);
        Assert.assertTrue(((String) t1).length() > 100);
        Assert.assertSame(t1, t2);
        Assert.assertEquals(t1, t3);
    }

    @Test
    public void evalClas() {
        Object t1 = engine.eval(context, new RnaWarmed("uri", "classpath:/template/blog/blog-pure.htm"), false);
        Object t2 = engine.eval(context, new RnaWarmed("uri", "classpath:/template/blog/blog-pure.htm"), false);
        Assert.assertTrue(((String) t1).length() > 100);
        Assert.assertSame(t1, t2);
    }

    @Test
    public void evalHttp() {
        Object t1 = engine.eval(context, new RnaWarmed("uri", "http://www.gitee.com"), false);
        Object t2 = engine.eval(context, new RnaWarmed("uri", "http://www.gitee.com"), false);
        Assert.assertTrue(((String) t1).length() > 100);
        Assert.assertSame(t1, t2);
    }
}