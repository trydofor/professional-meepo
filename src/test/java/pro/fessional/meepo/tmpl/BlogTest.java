package pro.fessional.meepo.tmpl;

import org.junit.Test;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class BlogTest {

    @Test
    public void blogPure() {
        TmplHelp.assertTmpl("/template/blog/blog-pure.peb", "/template/blog/blog-pure.htm");
    }

    @Test
    public void blogTrim() {
        TmplHelp.assertTmpl("/template/blog/blog-trim.peb", "/template/blog/blog-trim.htm");
    }
}
