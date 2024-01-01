package pro.fessional.meepo.bind.dna;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class DnaEndTest extends TraceTest {

    final DnaEnd d1 = new DnaEnd("// DNA:END BlackKingBar,id", new Clop(0, 17, 1, 1), Arrays.asList("BlackKingBar", "id"));
    final DnaEnd d2 = new DnaEnd(" // DNA:END BlackKingBar,id", new Clop(1, 18, 1, 1), Arrays.asList("id", "BlackKingBar"));

    @Test
    public void testEquals() {
        assertEquals(d1, d2);
    }

    @Test
    public void testHashCode() {
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    public void testToString() {
        logger.debug("d1={}", d1);
        logger.debug("d2={}", d2);
    }
}
