package pro.fessional.meepo.tmpl;

import pro.fessional.meepo.sack.Gene;
import pro.fessional.meepo.sack.Parser;
import pro.fessional.meepo.util.Read;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class TmplHelp {

    public static void assertTmpl(String expected, String actual) {
        assertTmpl(expected, actual, new HashMap<>());
    }

    public static void assertTmpl(String expected, String actual, Map<String, Object> ctx) {
        String strIn = Read.read(TmplHelp.class.getResourceAsStream(actual));
        String strOut = Read.read(TmplHelp.class.getResourceAsStream(expected));

        Gene gene = Parser.parse(strIn, "classpath:" + expected);
        String merge = gene.merge(ctx);
        String build = gene.build();
        assertEquals(strOut.trim(), merge.trim(), "merge mismatch");
        assertNotSame(strIn, build);
        assertEquals(strIn.trim(), build.trim(), "build mismatch");
    }

    public static void printGene(String clzPath) {
        String strIn = Read.read(TmplHelp.class.getResourceAsStream(clzPath));
        Gene gene = Parser.parse(strIn);
        System.out.println(gene.graph());
    }
}
