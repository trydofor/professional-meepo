package pro.fessional.meepo.poof.impl.java;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.Meepo;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.util.Java;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-11-04
 */
public class JavaEvalTest extends TraceTest {

    @Test
    public void create() {
        String s0 = Java.create(String.class);
        String s1 = Java.create(String.class, "");
        assertEquals(s0, s1);
    }

    @Test
    public void compile() {
        HashMap<String, Object> ctx = new HashMap<>();
        String name = "JavaCompile1";
        ctx.put("import", "");
        ctx.put("class", name);
        ctx.put("method", "return true");
        ctx.put("colon", ";");
        String tmpl = "classpath:/pro/fessional/meepo/poof/impl/java/JavaName.java";
        String code = Meepo.merge(ctx, tmpl, Meepo.CACHE_ALWAYS);

        String fullName = "pro.fessional.meepo.poof.impl.java." + name;
        Class<Object> clz = Java.compile(fullName, code);
        assertEquals(fullName, clz.getName());
    }
}