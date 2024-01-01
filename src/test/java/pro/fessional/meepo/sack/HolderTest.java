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
        logger.debug(gene.build());
        logger.debug(gene.graph());
        String str = gene.merge(ctx);
        assertEquals("this is TryDoFor here", str);
    }

    @Test
    void parseEsc1() {
        String hd = "this is /{{ //{{ var | PascalCase | BIG_SNAKE }} /}} here";
        final Gene gene = Holder.parse(true, hd, "{{", "}}", "/");
        logger.debug(hd);
        logger.debug(gene.build());
        logger.debug(gene.graph());
        String str = gene.merge(ctx);
        assertEquals("this is {{ /TRY_DO_FOR }} here", str);
    }

    @Test
    void parseEsc2() {
        String hd = "} this is /${ //${ var | PascalCase | BIG_SNAKE } /} here ${";
        final Gene gene = Holder.parse(true, hd, "${", "}", "/");
        logger.debug(hd);
        logger.debug(gene.build());
        logger.debug(gene.graph());
        String str = gene.merge(ctx);
        assertEquals("} this is ${ /TRY_DO_FOR } here ${", str);
    }

    @Test
    void parseEsc3() {
        String hd = "}}}} this is /${ //${ var | PascalCase | BIG_SNAKE }}} /}}}} here ${";
        final Gene gene = Holder.parse(true, hd, "${", "}}}", "/");
        logger.debug(hd);
        logger.debug(gene.build());
        logger.debug(gene.graph());
        String str = gene.merge(ctx);
        assertEquals("}}}} this is ${ /TRY_DO_FOR }}}} here ${", str);
    }
}