package pro.fessional.meepo.tmpl;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.poof.RnaManager;
import pro.fessional.meepo.poof.impl.JsEngine;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class RnaTest extends TraceTest {

    static {
        RnaManager.register(new JsEngine());
    }
    @Test
    public void putUse() {
        TmplHelp.assertTmpl("/template/rna/put-use-o.htm", "/template/rna/put-use-i.htm");
    }

    @Test
    public void runAny() {
        TmplHelp.assertTmpl("/template/rna/run-any-o.htm", "/template/rna/run-any-i.htm");
    }
}
