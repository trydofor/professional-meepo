package pro.fessional.meepo.bind.dna;

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
public class DnaSetTest extends TraceTest {

    final DnaSet d1 = new DnaSet("// DNA:SET /false/{{user.male}}/", new Clop(0, 32, 1, 1), Life.nobodyOne(), Pattern.compile("false"), "{{user.male}}");
    final DnaSet d2 = new DnaSet(" // DNA:SET /false/{{user.male}}/", new Clop(1, 33, 1, 1), Life.nobodyOne(), Pattern.compile("false"), "{{user.male}}");

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
