package pro.fessional.meepo.tmpl;

import org.junit.Assert;
import pro.fessional.meepo.bind.Const;
import pro.fessional.meepo.sack.Gene;
import pro.fessional.meepo.sack.Parser;
import pro.fessional.meepo.util.Read;

import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class TmplHelp {

    public static String trim(String txt) {
        if (txt == null) return Const.TXT$EMPTY;
        return txt.replaceAll("[\n\t\r ]+", " ");
    }

    public static void assertTmpl(String expected, String actual) {
        assertTmpl(expected, actual, new HashMap<>());
    }

    public static void assertTmpl(String expected, String actual, Map<String, Object> ctx) {
        String strIn = Read.read(TmplHelp.class.getResourceAsStream(actual));
        String strOut = Read.read(TmplHelp.class.getResourceAsStream(expected));

        Gene gene = Parser.parse(strIn);
        String merge = gene.merge(ctx);
        String build = gene.build();
        Assert.assertEquals("merge mismatch", strOut, merge);
        Assert.assertSame("origin mismatch", gene.text, strIn);
        Assert.assertNotSame(strIn, build);
        Assert.assertEquals("build mismatch",strIn, build);
    }

    public static void printGene(String clzPath) {
        String strIn = Read.read(TmplHelp.class.getResourceAsStream(clzPath));
        Gene gene = Parser.parse(strIn);
        System.out.println(gene.graph());
    }
}
