package pro.fessional.meepo.bind.rna;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;

import java.util.regex.Pattern;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class RnaUseTest extends TraceTest {

    final RnaUse d1 = new RnaUse("// DNA:USE /meepo/who/1-3", new Clop(0, 25, 1, 1), Life.parse("1-3"), Pattern.compile("meepo"), "who");
    final RnaUse d2 = new RnaUse(" // DNA:USE /meepo/who/1-3", new Clop(1, 26, 1, 1), Life.parse("1-3"), Pattern.compile("meepo"), "who");

    @Test
    public void testToString() {
        logger.debug("d1={}", d1);
        logger.debug("d2={}", d2);

        Assertions.assertTrue(d1.toString().contains("find='meepo', para='who'"));
        Assertions.assertTrue(d2.toString().contains("find='meepo', para='who'"));
    }
}
