package pro.fessional.meepo.bind.rna;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class RnaPutTest extends TraceTest {


    RnaPut d1 = new RnaPut("// DNA:PUT os/who/basename $(pwd)/", new Clop(0, 34,1,1), "os", "who", "basename $(pwd)", false);
    RnaPut d2 = new RnaPut(" // DNA:PUT os/who/basename $(pwd)/", new Clop(1, 35,1,1), "os", "who", "basename $(pwd)", false);

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