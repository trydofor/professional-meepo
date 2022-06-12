package pro.fessional.meepo.poof.impl;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.poof.RnaWarmed;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2020-10-31
 */
public class UriEngineTest extends TraceTest {

    UriEngine engine = new UriEngine();
    Map<String, Object> context = new HashMap<>();

    @Test
    public void evalFile() {
        Object t1 = engine.eval(context, new RnaWarmed("uri", "file://./pom.xml"), false);
        Object t2 = engine.eval(context, new RnaWarmed("uri", "file://./pom.xml"), false);
        Object t3 = engine.eval(context, new RnaWarmed("uri", "./pom.xml"), false);
        assertTrue(((String) t1).length() > 100);
        assertSame(t1, t2);
        assertEquals(t1, t3);
    }

    @Test
    public void evalClass() {
        Object t1 = engine.eval(context, new RnaWarmed("uri", "classpath:/template/blog/blog-pure.htm"), false);
        Object t2 = engine.eval(context, new RnaWarmed("uri", "classpath:/template/blog/blog-pure.htm"), false);
        assertTrue(((String) t1).length() > 100);
        assertSame(t1, t2);
    }

    @Test
    public void evalHttp() {
        Object t1 = engine.eval(context, new RnaWarmed("uri", "http://www.gitee.com"), false);
        Object t2 = engine.eval(context, new RnaWarmed("uri", "http://www.gitee.com"), false);
        assertTrue(((String) t1).length() > 100);
        assertSame(t1, t2);
    }
}
