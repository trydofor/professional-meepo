package pro.fessional.meepo.tmpl;

import org.junit.Test;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class ReplTest {

    @Test
    public void replaceAll() {
        TmplHelp.assertTmpl("/template/repl/replace-all-i.htm", "/template/repl/replace-all-o.htm");
    }

    @Test
    public void replace1a3() {
        TmplHelp.assertTmpl("/template/repl/replace-1a3-i.htm", "/template/repl/replace-1a3-o.htm");
    }

    @Test
    public void replaceEnd() {
        TmplHelp.assertTmpl("/template/repl/replace-end-i.htm", "/template/repl/replace-end-o.htm");
    }
}
