package pro.fessional.meepo.bind.dna;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class DnaBkbTest extends TraceTest {
    final DnaBkb d1 = new DnaBkb("// DNA:BKB BlackKingBar", new Clop(0, 14, 1, 1), "BlackKingBar");
    final DnaBkb d2 = new DnaBkb(" // DNA:BKB BlackKingBar", new Clop(1, 15, 1, 1), "BlackKingBar");

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
