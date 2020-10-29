package pro.fessional.meepo.tmpl;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.sack.Gene;
import pro.fessional.meepo.sack.Parser;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class BlogTest {

    @Test
    public void testPure() {
        String meepo = TmplHelp.load("/template/blog/blog-pure.htm");
        String peb = TmplHelp.load("/template/blog/blog-pure.peb");

        Gene gene = Parser.parse(meepo);
        String merge = gene.merge();
        String build = gene.build();
        Assert.assertEquals(peb, merge);
        Assert.assertSame(meepo, gene.text);
        Assert.assertNotSame(meepo, build);
        Assert.assertEquals(meepo, build);
    }

    @Test
    public void testTrim() {
        String meepo = TmplHelp.load("/template/blog/blog-trim.htm");
        String peb = TmplHelp.load("/template/blog/blog-trim.peb");

        Gene gene = Parser.parse(meepo);
        String merge = gene.merge();
        String build = gene.build();
        Assert.assertEquals(peb, merge);
        Assert.assertSame(meepo, gene.text);
        Assert.assertNotSame(meepo, build);
        Assert.assertEquals(meepo, build);
    }
}
