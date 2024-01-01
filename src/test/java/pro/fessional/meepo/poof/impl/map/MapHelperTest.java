package pro.fessional.meepo.poof.impl.map;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.fessional.meepo.bind.Const.ARR$EMPTY_STRING;

/**
 * @author trydofor
 * @since 2020-11-10
 */
public class MapHelperTest extends TraceTest {

    /**
     * java.runtime.name
     * sun.boot.library.path
     * java.vm.version
     * gopherProxySet
     * java.vm.vendor
     * java.vendor.url
     * path.separator
     * java.vm.name
     * file.encoding.pkg
     * user.country
     */
    @Test
    public void env() throws NoSuchMethodException {
        logger.debug(Bean.class.getName());
        Object a = new Object() {};
        logger.debug(a.getClass().getName());
        Method md = Bean.class.getMethod(getName("code"));
        logger.debug(md.getName());
        logger.debug("{}", md.getParameterCount());

        Enumeration<?> en = System.getProperties().propertyNames();
        while (en.hasMoreElements()) {
            logger.debug("{}", en.nextElement());
        }
    }

    @Test
    public void getterSpeed() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "name");
        map.put("code", "code");
        map.put("male", true);
        Bean bean = new Bean();

        int tm = 100_0000;
        logger.debug("loop {} times", tm);
        long s1 = System.currentTimeMillis();
        for (int i = 0; i < tm; i++) {
            map.get("code");
        }
        logger.debug("map.get cost {}ms",System.currentTimeMillis() - s1);
        //
        long s2 = System.currentTimeMillis();
        for (int i = 0; i < tm; i++) {
            bean.getCode();
        }
        logger.debug("bean.get cost {}ms", System.currentTimeMillis() - s2);

        //
        long s3 = System.currentTimeMillis();
        for (int i = 0; i < tm; i++) {
            Method md = bean.getClass().getMethod(getName("code"));
            md.invoke(bean);
        }
        logger.debug("bean.each-reflect cost {}ms", System.currentTimeMillis() - s3);

        /*
         loop 1000000 times
         map.get cost 27ms
         bean.get cost 7ms
         bean.each-reflect cost 1424ms
         bean.cached-reflect cost 192ms
         */
//        Map<String, Method> cache = new HashMap<>(); // bean.cached-reflect cost 451ms
        Map<String, Method> cache = new ConcurrentHashMap<>(); // bean.cached-reflect cost 192ms
        long s4 = System.currentTimeMillis();
        for (int i = 0; i < tm; i++) {
            Method md = cache.computeIfAbsent("code", s -> {
                try {
                    return bean.getClass().getMethod(getName("code"));
                }
                catch (NoSuchMethodException e) {
                    throw new IllegalStateException(e);
                }
            });
            md.invoke(bean);
        }
        logger.debug("bean.cached-reflect cost {}ms", System.currentTimeMillis() - s4);

        long s5 = System.currentTimeMillis();
        for (int i = 0; i < tm; i++) {
            MapHelper.get(bean, "code", ARR$EMPTY_STRING); // bean.naviget cost 417ms
        }
        logger.debug("bean.naviget cost {}ms", System.currentTimeMillis() - s5);
    }

    private String getName(String prop) {
        return "get" + Character.toUpperCase(prop.charAt(0)) + prop.substring(1);
    }

    @Test
    public void beanGetter() {
        Bean bean = new Bean();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "name");
        map.put("code", "code");
        map.put("male", true);
        map.put("bean", bean);

        assertEquals("name", MapHelper.get(map, "name", ARR$EMPTY_STRING));
        assertEquals("code", MapHelper.get(map, "code", ARR$EMPTY_STRING));
        assertEquals(Boolean.TRUE, MapHelper.get(map, "male", ARR$EMPTY_STRING));

        assertEquals("name", MapHelper.get(bean, "name", ARR$EMPTY_STRING));
        assertEquals("code", MapHelper.get(bean, "code", ARR$EMPTY_STRING));
        assertEquals(Boolean.TRUE, MapHelper.get(bean, "male", ARR$EMPTY_STRING));

        assertEquals("name", MapHelper.get(map, "bean.name", new String[]{"bean", "name"}));
        assertEquals("code", MapHelper.get(map, "bean.code", new String[]{"bean", "code"}));
        assertEquals(Boolean.TRUE, MapHelper.get(map, "bean.male", new String[]{"bean", "male"}));
    }

    public static class Bean {
        public final String name = "name";
        public final String code = "code";
        public final boolean male = true;

        public String getCode() {
            return code;
        }

        public boolean isMale() {
            return male;
        }
    }
}
