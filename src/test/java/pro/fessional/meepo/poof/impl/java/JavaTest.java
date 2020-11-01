package pro.fessional.meepo.poof.impl.java;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.Meepo;
import pro.fessional.meepo.util.Java;

import java.util.HashMap;

/**
 * @author trydofor
 * @since 2020-11-04
 */
public class JavaTest {

    @Test
    public void create() {
        String s0 = Java.create(String.class);
        String s1 = Java.create(String.class, "");
        Assert.assertEquals(s0, s1);
    }

    @Test
    public void compile() {
        HashMap<String, Object> ctx = new HashMap<>();
        String name = "Java01";
        ctx.put("import", "");
        ctx.put("class", name);
        ctx.put("method", "return true");
        ctx.put("colon", ";");
        String uri = "classpath:/pro/fessional/meepo/poof/impl/java/JavaName.java";
        String code = Meepo.merge(ctx, uri, Meepo.CACHE_ALWAYS);

        String fullName = "pro.fessional.meepo.poof.impl.java." + name;
        Class<Object> clz = Java.compile(fullName, code);
        Assert.assertEquals(fullName, clz.getName());
    }
}