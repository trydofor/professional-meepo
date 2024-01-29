package pro.fessional.meepo.sack;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2021-01-04
 */
class HolderTest extends TraceTest {

    private final Map<String, Object> ctx = Collections.singletonMap("var", "try-do-for");

    @Test
    void parseSimple() {
        String hd = "this is {{var | PascalCase}} here";
        final Gene gene = Holder.parse(hd);
        String build = gene.build();
        String graph = gene.graph();
        logger.debug("hd={}", hd);
        logger.debug("build={}", build);
        logger.debug("graph={}", graph);
        String str = gene.merge(ctx);
        assertEquals("this is TryDoFor here", str);
        assertEquals(hd, build);
    }

    @Test
    void parseEsc1() {
        String hd = "this is /{{ //{{ var | PascalCase | BIG_SNAKE }} /}} here";
        final Gene gene = Holder.parse(true, hd, "{{", "}}", "/");
        String build = gene.build();
        String graph = gene.graph();
        logger.debug("hd={}", hd);
        logger.debug("build={}", build);
        logger.debug("graph={}", graph);
        String str = gene.merge(ctx);
        assertEquals("this is {{ /TRY_DO_FOR }} here", str);
        assertEquals("this is {{ /{{ var | PascalCase | BIG_SNAKE }} }} here", build);
    }

    @Test
    void parseEsc2() {
        String hd = "} this is /${ //${ var | PascalCase | BIG_SNAKE } /} here ${";
        final Gene gene = Holder.parse(true, hd, "${", "}", "/");
        String build = gene.build();
        String graph = gene.graph();
        logger.debug("hd={}", hd);
        logger.debug("build={}", build);
        logger.debug("graph={}", graph);
        String str = gene.merge(ctx);
        assertEquals("} this is ${ /TRY_DO_FOR } here ${", str);
        assertEquals("} this is ${ /${ var | PascalCase | BIG_SNAKE } } here ${", build);
    }

    @Test
    void parseEsc3() {
        String hd = "}}}} this is /${ //${ var | PascalCase | BIG_SNAKE }}} /}}}} here ${";
        final Gene gene = Holder.parse(true, hd, "${", "}}}", "/");
        String graph = gene.graph();
        String build = gene.build();
        logger.debug("hd={}", hd);
        logger.debug("build={}", build);
        logger.debug("graph={}", graph);
        String str = gene.merge(ctx);
        assertEquals("}}}} this is ${ /TRY_DO_FOR }}}} here ${", str);
        assertEquals("}}}} this is ${ /${ var | PascalCase | BIG_SNAKE }}} }}}} here ${", build);
    }
}