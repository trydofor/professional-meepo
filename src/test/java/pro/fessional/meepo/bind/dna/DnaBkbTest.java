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

    DnaBkb d1 = new DnaBkb("// DNA:BKB 黑皇杖", new Clop(0,14,1,1), "黑皇杖");
    DnaBkb d2 = new DnaBkb(" // DNA:BKB 黑皇杖", new Clop(1,15,1,1), "黑皇杖");

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