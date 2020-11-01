package pro.fessional.meepo.tmpl;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.poof.impl.JavaEngine;
import pro.fessional.meepo.sack.Gene;
import pro.fessional.meepo.sack.Parser;
import pro.fessional.meepo.util.Read;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author trydofor
 * @since 2020-10-28
 */
public class JavaTest {
    @Test
    public void java() {
        TmplHelp.assertTmpl("/template/java/compile-java-o.htm", "/template/java/compile-java-i.htm");
    }

    @Test
    public void meepo() {
        String pkg = "/pro/fessional/meepo/poof/impl/java/JavaName.java";
        String tmpl = Read.read(JavaEngine.class.getResourceAsStream(pkg));
        Gene gene = Parser.parse(tmpl);
        HashMap<String, Object> c = new HashMap<>();
        String name = "JavaOut";
        c.put("import", "import java.time.LocalTime;\nimport java.time.LocalDate;\n" +
                "import java.time.LocalDateTime;\n" +
                "import java.time.format.DateTimeFormatter;\n");
        c.put("class", name);
        c.put("method", Arrays.asList("LocalDate date = LocalDate.parse(\"2020-07-09\");\n",
                "LocalDateTime ldt = LocalDateTime.of(date, LocalTime.of(0, 0, 0));\n",
                "DateTimeFormatter fmt = DateTimeFormatter.ofPattern(\"yyyy-MM-dd HH:mm:ss\");\n",
                "return ldt.format(fmt);"));
        c.put("colon", "");
        String code = gene.merge(c);

        String out = Read.read(JavaEngine.class.getResourceAsStream("/pro/fessional/meepo/poof/impl/java/JavaOut.java"));
        Assert.assertEquals(out,code);
    }
}
