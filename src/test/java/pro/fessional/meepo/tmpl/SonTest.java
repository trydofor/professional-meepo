package pro.fessional.meepo.tmpl;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class SonTest extends TraceTest {
    @Test
    public void imp() {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("gmail","gmail.com");
        TmplHelp.assertTmpl("/template/son/include-o.htm", "/template/son/include-i1.htm", ctx);
        TmplHelp.assertTmpl("/template/son/include-o.htm", "/template/son/include-i2.htm", ctx);
    }
}
