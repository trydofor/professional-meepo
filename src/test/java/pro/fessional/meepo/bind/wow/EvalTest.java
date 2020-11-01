package pro.fessional.meepo.bind.wow;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static pro.fessional.meepo.bind.Const.OBJ$NAVIGATOR;

/**
 * @author trydofor
 * @since 2020-11-04
 */
public class EvalTest {

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
        System.out.println(Bean.class.getName());
        Object a = new Object() {};
        System.out.println(a.getClass().getName());
        Method md = Bean.class.getMethod(getName("code"));
        System.out.println(md.getName());
        System.out.println(md.getParameterCount());

        Enumeration<?> en = System.getProperties().propertyNames();
        while (en.hasMoreElements()) {
            System.out.println(en.nextElement());
        }
    }

    @Test
    public void asFalse() {
        Assert.assertTrue(Eval.asFalse(null));
        Assert.assertTrue(Eval.asFalse(""));
        Assert.assertTrue(Eval.asFalse(false));
        Assert.assertTrue(Eval.asFalse(Boolean.FALSE));

        Assert.assertTrue(Eval.asFalse(0));
        Assert.assertTrue(Eval.asFalse(0L));
        Assert.assertTrue(Eval.asFalse(0.0D));
        Assert.assertTrue(Eval.asFalse(0.0F));
        Assert.assertTrue(Eval.asFalse(-0.0D));
        Assert.assertTrue(Eval.asFalse(-0.0F));
        Assert.assertTrue(Eval.asFalse(0.00000F));
        Assert.assertTrue(Eval.asFalse(-0.00000F));
        Assert.assertTrue(Eval.asFalse(Float.NaN));
        Assert.assertTrue(Eval.asFalse(Double.NaN));
        Assert.assertTrue(Eval.asFalse(BigDecimal.ZERO));
        Assert.assertTrue(Eval.asFalse(new BigDecimal("0.0")));
        Assert.assertTrue(Eval.asFalse(new BigDecimal("0.000")));
        Assert.assertTrue(Eval.asFalse(new BigDecimal("-0.000")));

        Assert.assertTrue(Eval.asFalse(new boolean[0]));
        Assert.assertTrue(Eval.asFalse(new byte[0]));
        Assert.assertTrue(Eval.asFalse(new short[0]));
        Assert.assertTrue(Eval.asFalse(new int[0]));
        Assert.assertTrue(Eval.asFalse(new long[0]));
        Assert.assertTrue(Eval.asFalse(new float[0]));
        Assert.assertTrue(Eval.asFalse(new double[0]));
        Assert.assertTrue(Eval.asFalse(new Object[0]));

        Assert.assertTrue(Eval.asFalse(new ArrayList<>()));
        Assert.assertTrue(Eval.asFalse(new HashMap<>()));
    }

    @Test
    public void getterSpeed() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "name");
        map.put("code", "code");
        map.put("male", true);
        Bean bean = new Bean();

        int tm = 100_0000;
        System.out.println("loop " + tm + " times");
        long s1 = System.currentTimeMillis();
        for (int i = 0; i < tm; i++) {
            map.get("code");
        }
        System.out.println("map.get cost " + (System.currentTimeMillis() - s1) + "ms");
        //
        long s2 = System.currentTimeMillis();
        for (int i = 0; i < tm; i++) {
            bean.getCode();
        }
        System.out.println("bean.get cost " + (System.currentTimeMillis() - s2) + "ms");

        //
        long s3 = System.currentTimeMillis();
        for (int i = 0; i < tm; i++) {
            Method md = bean.getClass().getMethod(getName("code"));
            md.invoke(bean);
        }
        System.out.println("bean.each-reflect cost " + (System.currentTimeMillis() - s3) + "ms");

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
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException(e);
                }
            });
            md.invoke(bean);
        }
        System.out.println("bean.cached-reflect cost " + (System.currentTimeMillis() - s4) + "ms");

        long s5 = System.currentTimeMillis();
        for (int i = 0; i < tm; i++) {
            Eval.naviGet(bean, "code", OBJ$NAVIGATOR); // bean.naviget cost 417ms
        }
        System.out.println("bean.naviget cost " + (System.currentTimeMillis() - s5) + "ms");
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

        Assert.assertEquals("name", Eval.naviGet(map, "name", OBJ$NAVIGATOR));
        Assert.assertEquals("code", Eval.naviGet(map, "code", OBJ$NAVIGATOR));
        Assert.assertEquals(Boolean.TRUE, Eval.naviGet(map, "male", OBJ$NAVIGATOR));

        Assert.assertEquals("name", Eval.naviGet(bean, "name", OBJ$NAVIGATOR));
        Assert.assertEquals("code", Eval.naviGet(bean, "code", OBJ$NAVIGATOR));
        Assert.assertEquals(Boolean.TRUE, Eval.naviGet(bean, "male", OBJ$NAVIGATOR));

        Assert.assertEquals("name", Eval.naviGet(map, "bean:name", OBJ$NAVIGATOR));
        Assert.assertEquals("code", Eval.naviGet(map, "bean:code", OBJ$NAVIGATOR));
        Assert.assertEquals(Boolean.TRUE, Eval.naviGet(map, "bean:male", OBJ$NAVIGATOR));
    }

    static class Bean {
        public final String name = "name";
        private final String code = "code";
        private final boolean male = true;

        public String getCode() {
            return code;
        }

        public boolean isMale() {
            return male;
        }
    }
}