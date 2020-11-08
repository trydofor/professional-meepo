package pro.fessional.meepo.tmpl;

import org.junit.Test;
import pro.fessional.meepo.TraceTest;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class DelTest extends TraceTest {
    @Test
    public void del1a3() {
        TmplHelp.assertTmpl("/template/del/delete-1a3-o.htm", "/template/del/delete-1a3-i.htm");
    }

    @Test
    public void delAll() {
        TmplHelp.assertTmpl("/template/del/delete-all-o.htm", "/template/del/delete-all-i.htm");
    }
}
