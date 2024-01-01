package pro.fessional.meepo.eval.ctrl;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.Meepo;
import pro.fessional.meepo.sack.Gene;
import pro.fessional.meepo.sack.Holder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2021-01-04
 */
class ElvTest {

    private final Map<String, Object> ctx = new HashMap<>();

    @Test
    public void testSee() {
        ctx.put("nil", null);
        ctx.put("empty", "");
        ctx.put("value", "got");
        final Gene g1 = Holder.parse("{{ fun:see nil empty value }}");
        assertEquals("got", g1.merge(ctx));

        final Gene g2 = Holder.parse("{{ nil | fun:see empty value }}");
        assertEquals("got", g2.merge(ctx));

        final Gene g3 = Holder.parse("{{ see empty value }}");
        assertEquals("got", g3.merge(ctx));
    }

    @Test
    public void testSee2() {
        ctx.put("GitHash", "abc");
        ctx.put("ModTime", "got");

        final Gene g1 = Meepo.parse("g1","// HI-MEEPO\n"
                                    + "// RNA:USE /hash/fun:see GitHashX ModTime/\n"
                                    + "project hash");
        assertEquals("project got", g1.merge(ctx));

        final Gene g2 = Meepo.parse("g2","// HI-MEEPO\n"
                                     + "// RNA:USE /hash/fun:see GitHash ModTime/\n"
                                     + "project hash");
        assertEquals("project abc", g2.merge(ctx));
    }

}
