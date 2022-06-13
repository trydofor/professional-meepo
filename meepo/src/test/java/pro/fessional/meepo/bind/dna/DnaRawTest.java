package pro.fessional.meepo.bind.dna;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class DnaRawTest extends TraceTest {

    DnaRaw d1 = new DnaRaw("// DNA:RAW SUPER(1010100", new Clop(0, 24, 1, 1), 11, 24);
    DnaRaw d2 = new DnaRaw(" // DNA:RAW SUPER(1010100", new Clop(1, 25, 1, 1), 12, 25);

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
        System.out.println(d1.toString());
        System.out.println(d2.toString());
    }

}
