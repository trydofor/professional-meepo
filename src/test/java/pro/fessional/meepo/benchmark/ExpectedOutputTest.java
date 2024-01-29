package pro.fessional.meepo.benchmark;

import io.pebbletemplates.pebble.error.PebbleException;
import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.fessional.meepo.TestingHelper.ExpectOutputContent;
import static pro.fessional.meepo.TestingHelper.trimOutput;

/**
 * @author trydofor
 * @since 2020-11-10
 */
public class ExpectedOutputTest extends TraceTest {

    @Test
    public void testMeepoOutput() throws IOException {
        MeepoBenchmark meepo = new MeepoBenchmark();
        meepo.setup();
        for (int i = 0; i < 10; i++) {
            String actual = trimOutput(meepo.benchmark());
            assertEquals(ExpectOutputContent, actual);
        }
    }

    @Test
    public void testPebbleOutput() throws IOException, PebbleException {
        PebbleBenchmark pebble = new PebbleBenchmark();
        pebble.setup();
        for (int i = 0; i < 10; i++) {
            String actual = trimOutput(pebble.benchmark());
            assertEquals(ExpectOutputContent, actual);
        }
    }
}
