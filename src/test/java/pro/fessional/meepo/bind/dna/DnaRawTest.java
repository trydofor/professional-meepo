package pro.fessional.meepo.bind.dna;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class DnaRawTest extends TraceTest {

    final DnaRaw d1 = new DnaRaw("// DNA:RAW SUPER(1010100", new Clop(0, 24, 1, 1), 11, 24);
    final DnaRaw d2 = new DnaRaw(" // DNA:RAW SUPER(1010100", new Clop(1, 25, 1, 1), 12, 25);



    @Test
    public void testToString() {
        logger.debug("d1={}", d1);
        logger.debug("d2={}", d2);
        Assertions.assertTrue(d1.toString().contains("text='SUPER(1010100'"));
        Assertions.assertTrue(d2.toString().contains("text='SUPER(1010100'"));
    }

}
