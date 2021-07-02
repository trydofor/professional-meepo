package pro.fessional.meepo.bind.rna;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class RnaRunTest extends TraceTest {

    RnaRun d1 = new RnaRun("// DNA:RUN os/rand/echo $RANDOM/1-3", new Clop(0, 35, 1, 1), Life.parse("1-3"), "os", Pattern.compile("rand"), "echo $RANDOM", false);
    RnaRun d2 = new RnaRun(" // DNA:RUN os/rand/echo $RANDOM/1-3", new Clop(1, 36, 1, 1), Life.parse("1-3"), "os", Pattern.compile("rand"), "echo $RANDOM", false);

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
