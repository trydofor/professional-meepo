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

    DnaEnd d1 = new DnaEnd("// DNA:END 黑皇杖,id", new Clop(0, 17, 1, 1), Arrays.asList("黑皇杖", "id"));
    DnaEnd d2 = new DnaEnd(" // DNA:END 黑皇杖,id", new Clop(1, 18, 1, 1), Arrays.asList("id", "黑皇杖"));

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
