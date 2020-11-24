package pro.fessional.meepo.bind.wow;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2020-10-19
 */
public class ClopTest extends TraceTest {

    @Test
    public void cross() {
        Clop c1 = new Clop(1, 2, 1, 1);
        Clop c2 = new Clop(1, 3, 1, 1);
        Clop c3 = new Clop(2, 3, 1, 1);
        Clop c4 = new Clop(0, 4, 1, 1);
        assertFalse(c1.cross(c3));
        assertFalse(c3.cross(c1));
        assertTrue(c1.cross(c2));
        assertTrue(c2.cross(c1));
        assertTrue(c3.cross(c2));
        assertTrue(c2.cross(c3));

        assertTrue(c4.cross(c1));
        assertTrue(c4.cross(c2));
        assertTrue(c4.cross(c3));
        assertTrue(c1.cross(c4));
        assertTrue(c2.cross(c4));
        assertTrue(c3.cross(c4));
    }
}