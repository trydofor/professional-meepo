package pro.fessional.meepo.bind.txt;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class HiMeepoTest extends TraceTest {

    final HiMeepo d1 = new HiMeepo("/* H!MEEPO */", new Clop(0, 13, 1, 1), "/*", "*/", true);
    final HiMeepo d2 = new HiMeepo("/* H!MEEPO  */ ", new Clop(0, 14, 1, 1), "/*", "*/", true);

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
