package pro.fessional.meepo.tmpl;

import org.junit.Assert;
import pro.fessional.meepo.bind.Const;
import pro.fessional.meepo.sack.Gene;
import pro.fessional.meepo.sack.Parser;
import pro.fessional.meepo.util.Read;

import java.util.HashMap;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class TmplHelp {

    public static String trim(String txt) {
        if (txt == null) return Const.TXT_EMPTY;
        return txt.replaceAll("[\n\t\r ]+", " ");
    }

    public static void assertTmpl(String meepo, String output) {
        String strIn = Read.read(TmplHelp.class.getResourceAsStream(meepo));
        String strOut = Read.read(TmplHelp.class.getResourceAsStream(output));

        Gene gene = Parser.parse(strIn);
        String merge = gene.merge(new HashMap<>());
        String build = gene.build();
        Assert.assertEquals(strOut, merge);
        Assert.assertSame(strIn, gene.text);
        Assert.assertNotSame(strIn, build);
        Assert.assertEquals(strIn, build);
    }
}
