package pro.fessional.meepo.poof.impl;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2020-10-31
 */
public class UriEngineTest {

    UriEngine engine = new UriEngine();
    Map<String, Object> context = new HashMap<>();

    @Test
    public void evalFile() {
        Object t1 = engine.eval("uri", "file://./readme.md", context, false);
        Object t2 = engine.eval("uri", "file://./readme.md", context, false);
        Object t3 = engine.eval("uri", "./readme.md", context, false);
        Assert.assertTrue(((String) t1).length() > 100);
        Assert.assertSame(t1, t2);
        Assert.assertEquals(t1, t3);
    }

    @Test
    public void evalClas() {
        Object t1 = engine.eval("uri", "classpath:/template/blog/blog-pure.htm", context, false);
        Object t2 = engine.eval("uri", "classpath:/template/blog/blog-pure.htm", context, false);
        Assert.assertTrue(((String) t1).length() > 100);
        Assert.assertSame(t1, t2);
    }

    @Test
    public void evalHttp() {
        Object t1 = engine.eval("uri", "http://www.gitee.com", context, false);
        Object t2 = engine.eval("uri", "http://www.gitee.com", context, false);
        Assert.assertTrue(((String) t1).length() > 100);
        Assert.assertSame(t1, t2);
    }
}