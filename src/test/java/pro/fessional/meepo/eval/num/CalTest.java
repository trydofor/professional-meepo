package pro.fessional.meepo.eval.num;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.sack.Gene;
import pro.fessional.meepo.sack.Holder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2021-01-05
 */
class CalTest {

    private final Map<String, Object> ctx = new HashMap<>();

    @Test
    void testFmtHolder() {
        String hd = "{{ index | mod even odd}}";
        final Gene gene = Holder.parse(hd);
        ctx.put("index", 3);
        String s3 = gene.merge(ctx);
        assertEquals("odd", s3);
        ctx.put("index", 4);
        String s4 = gene.merge(ctx);
        assertEquals("even", s4);

    }
}