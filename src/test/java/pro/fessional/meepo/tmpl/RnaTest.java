package pro.fessional.meepo.tmpl;

import org.junit.Test;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class RnaTest {
    @Test
    public void putUse() {
        TmplHelp.assertTmpl("/template/rna/put-use-i.htm", "/template/rna/put-use-o.htm");
    }

    @Test
    public void runAny() {
        TmplHelp.assertTmpl("/template/rna/run-any-i.htm", "/template/rna/run-any-o.htm");
    }
}
