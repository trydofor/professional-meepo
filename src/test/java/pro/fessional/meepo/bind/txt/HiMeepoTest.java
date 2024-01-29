package pro.fessional.meepo.bind.txt;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class HiMeepoTest extends TraceTest {

    final HiMeepo d1 = new HiMeepo("/* H!MEEPO */", new Clop(0, 13, 1, 1), "/*", "*/", true);
    final HiMeepo d2 = new HiMeepo("/* H!MEEPO  */ ", new Clop(0, 14, 1, 1), "/*", "*/", true);

    @Test
    public void testToString() {
        logger.debug("d1={}", d1);
        logger.debug("d2={}", d2);
    }
}
