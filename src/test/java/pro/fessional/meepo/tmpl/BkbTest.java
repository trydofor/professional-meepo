package pro.fessional.meepo.tmpl;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class BkbTest extends TraceTest {
    @Test
    public void bkb() {
        TmplHelp.assertTmpl("/template/bkb/black-king-bar-o.htm", "/template/bkb/black-king-bar-i.htm");
    }
}
