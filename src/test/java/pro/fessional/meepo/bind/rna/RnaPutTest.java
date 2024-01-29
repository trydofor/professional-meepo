package pro.fessional.meepo.bind.rna;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class RnaPutTest extends TraceTest {

    final RnaPut d1 = new RnaPut("// DNA:PUT os/who/basename $(pwd)/", new Clop(0, 34, 1, 1), "os", "who", "basename $(pwd)", false);
    final RnaPut d2 = new RnaPut(" // DNA:PUT os/who/basename $(pwd)/", new Clop(1, 35, 1, 1), "os", "who", "basename $(pwd)", false);

    @Test
    public void testToString() {
        logger.debug("d1={}", d1);
        logger.debug("d2={}", d2);

        Assertions.assertTrue(d1.toString().contains("type='os', para='who', expr='basename $(pwd)'"));
        Assertions.assertTrue(d2.toString().contains("type='os', para='who', expr='basename $(pwd)'"));
    }
}
