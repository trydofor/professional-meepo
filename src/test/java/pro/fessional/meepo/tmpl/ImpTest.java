package pro.fessional.meepo.tmpl;

import org.junit.Test;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class ImpTest {
    @Test
    public void imp() {
        TmplHelp.assertTmpl("/template/imp/import-o.htm", "/template/imp/import-i.htm");
    }
}
