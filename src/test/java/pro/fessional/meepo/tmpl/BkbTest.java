package pro.fessional.meepo.tmpl;

import org.junit.Test;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class BkbTest {
    @Test
    public void bkb() {
        TmplHelp.assertTmpl("/template/bkb/black-king-bar-o.htm", "/template/bkb/black-king-bar-i.htm");
    }
}
