package pro.fessional.meepo.tmpl;

import org.junit.Test;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class JavaTest {
    @Test
    public void java() {
        TmplHelp.assertTmpl("/template/java/compile-java-i.htm", "/template/java/compile-java-o.htm");
    }
}
