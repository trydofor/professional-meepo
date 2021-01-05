package pro.fessional.meepo.eval.fmt;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.sack.Gene;
import pro.fessional.meepo.sack.Holder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2021-01-04
 */
class FmtTest {

    private final Map<String, Object> ctx = new HashMap<>();

    @Test
    public void testFmt() {
        ctx.put("amount", 1000);
        final Gene gene = Holder.parse("{{ amount | fmt '$%,d' }}");
        String str = gene.merge(ctx);
        assertEquals("$1,000", str);
    }

    @Test
    void testFmtHolder() {
        String hd = "{{ now 'yyyy-MM-dd HH:mm:ss' | fmt 'now is %s' }}";
        final Gene gene = Holder.parse(hd);
        String str = gene.merge(ctx);
        assertTrue(Pattern.compile("now is \\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}").matcher(str).find());
    }
}