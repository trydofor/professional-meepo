package pro.fessional.meepo.bind.rna;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;

import java.util.regex.Pattern;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class RnaRunTest extends TraceTest {

    final RnaRun d1 = new RnaRun("// DNA:RUN os/rand/echo $RANDOM/1-3", new Clop(0, 35, 1, 1), Life.parse("1-3"), "os", Pattern.compile("rand"), "echo $RANDOM", false);
    final RnaRun d2 = new RnaRun(" // DNA:RUN os/rand/echo $RANDOM/1-3", new Clop(1, 36, 1, 1), Life.parse("1-3"), "os", Pattern.compile("rand"), "echo $RANDOM", false);

    @Test
    public void testToString() {
        logger.debug("d1={}", d1);
        logger.debug("d2={}", d2);

        Assertions.assertTrue(d1.toString().contains("type='os', find='rand', expr='echo $RANDOM'"));
        Assertions.assertTrue(d2.toString().contains("type='os', find='rand', expr='echo $RANDOM'"));
    }
}
