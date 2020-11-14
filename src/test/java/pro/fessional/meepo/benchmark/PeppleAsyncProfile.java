package pro.fessional.meepo.benchmark;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * @author trydofor
 * @since 2020-10-28
 */

public class PeppleAsyncProfile {

    private PebbleTemplate template;

    private Map<String, Object> context;

    public void setup() throws PebbleException {
        context = Stock.mockContext();
        PebbleEngine engine = new PebbleEngine.Builder()
                .autoEscaping(false).build();
        template = engine.getTemplate("template/jmh/stocks.pebble.html");
    }

    public String benchmark() throws PebbleException, IOException {
        StringWriter writer = new StringWriter();
        template.evaluate(writer, context);
        return writer.toString();
    }


    public static void main(String[] args) throws IOException {
        System.setProperty("org.slf4j.simpleLogger.log.pro.fessional.meepo", "error");
        PeppleAsyncProfile profile = new PeppleAsyncProfile();
        profile.setup();
        int end = 1000_0000;
        System.out.println("don setup, loop= " + end);
        for (int i = 1; i <= end; i++) {
            profile.benchmark();
            if (i % 1_0000 == 0) {
                System.out.println(" - looped= " + i);
            }
        }
    }
}
