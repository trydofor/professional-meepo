package pro.fessional.meepo.bind.txt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class TxtSimpleTest extends TraceTest {

    final TxtSimple d1 = new TxtSimple("0123456789", new Clop(1, 10, 1, 1));
    final TxtSimple d2 = new TxtSimple("123456789", new Clop(0, 9, 1, 1));

    @Test
    public void testToString() {
        logger.debug("d1={}", d1);
        logger.debug("d2={}", d2);

        Assertions.assertTrue(d1.toString().contains("text='123456789'"));
        Assertions.assertTrue(d2.toString().contains("text='123456789'"));
    }
}
