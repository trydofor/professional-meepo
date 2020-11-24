package pro.fessional.meepo.tmpl;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class RnaTest extends TraceTest {
    @Test
    public void putUse() {
        TmplHelp.assertTmpl("/template/rna/put-use-o.htm", "/template/rna/put-use-i.htm");
    }

    @Test
    public void runAny() {
        TmplHelp.assertTmpl("/template/rna/run-any-o.htm", "/template/rna/run-any-i.htm");
    }
}
