package pro.fessional.meepo.benchmark;

import com.mitchellbosecke.pebble.error.PebbleException;
import org.junit.Test;
import pro.fessional.meepo.util.Read;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author trydofor
 * @since 2020-11-10
 */
public class ExpectedOutputTest {

    @Test
    public void testMeepoOutput() {
        MeepoBenchmark meepo = new MeepoBenchmark();
        meepo.setup();
        String expect = trim(Read.read("classpath:/template/jmh/expected-output.html"));
        for (int i = 0; i < 10; i++) {
            String actual = trim(meepo.benchmark());
            assertEquals(expect, actual);
        }
    }

    @Test
    public void testPebbleOutput() throws IOException, PebbleException {
        PeppleBenchmark pepple = new PeppleBenchmark();
        pepple.setup();
        String expect = trim(Read.read("classpath:/template/jmh/expected-output.html"));
        for (int i = 0; i < 10; i++) {
            String actual = trim(pepple.benchmark());
            assertEquals(expect, actual);
        }
    }


    private String trim(String str) {
        return str
                .replaceAll("\\s", "")
                .replaceAll("<td", "\n<td")
                .replaceAll("<tr", "\n<tr")
                .replaceAll("</tr", "\n</tr");
    }
}
