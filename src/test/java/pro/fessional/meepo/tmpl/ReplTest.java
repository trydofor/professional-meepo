package pro.fessional.meepo.tmpl;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class ReplTest extends TraceTest {

    @Test
    public void replaceAll() {
        TmplHelp.assertTmpl("/template/repl/replace-all-o.htm", "/template/repl/replace-all-i.htm");
    }

    @Test
    public void replace1a3() {
        TmplHelp.assertTmpl("/template/repl/replace-1a3-o.htm", "/template/repl/replace-1a3-i.htm");
    }

    @Test
    public void replaceEnd() {
        TmplHelp.assertTmpl("/template/repl/replace-end-o.htm", "/template/repl/replace-end-i.htm");
    }
}
