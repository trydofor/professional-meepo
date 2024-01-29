package pro.fessional.meepo.bind.dna;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;

import java.util.Arrays;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class DnaEndTest extends TraceTest {

    final DnaEnd d1 = new DnaEnd("// DNA:END BlackKingBar,id", new Clop(0, 17, 1, 1), Arrays.asList("BlackKingBar", "id"));
    final DnaEnd d2 = new DnaEnd(" // DNA:END BlackKingBar,id", new Clop(1, 18, 1, 1), Arrays.asList("id", "BlackKingBar"));

    @Test
    public void testToString() {
        logger.debug("d1={}", d1);
        logger.debug("d2={}", d2);
        Assertions.assertTrue(d1.toString().contains("name=[BlackKingBar,id]"));
        Assertions.assertTrue(d2.toString().contains("name=[BlackKingBar,id]"));
    }
}
