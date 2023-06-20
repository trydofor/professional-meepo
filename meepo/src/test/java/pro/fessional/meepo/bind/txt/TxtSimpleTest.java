package pro.fessional.meepo.bind.txt;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class TxtSimpleTest extends TraceTest {

    final TxtSimple d1 = new TxtSimple("0123456789", new Clop(1, 10, 1, 1));
    final TxtSimple d2 = new TxtSimple("123456789", new Clop(0, 9, 1, 1));

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
