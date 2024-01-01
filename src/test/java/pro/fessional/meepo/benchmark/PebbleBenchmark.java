package pro.fessional.meepo.benchmark;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author trydofor
 * @since 2020-10-28
 */
@Fork(2)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class PebbleBenchmark {

    private PebbleTemplate template;

    private Map<String, Object> context;

    @Setup
    public void setup() throws PebbleException {
        context = Stock.mockContext();
        PebbleEngine engine = new PebbleEngine.Builder()
                .autoEscaping(false).build();
        template = engine.getTemplate("template/jmh/stocks.pebble.html");
    }

    @Benchmark
    public String benchmark() throws PebbleException, IOException {
        try (Writer writer = new StringWriter(10240)) {
            template.evaluate(writer, context);
            return writer.toString();
        }
    }

    public static void main(String[] args) throws RunnerException {
        System.setProperty("org.slf4j.simpleLogger.log.pro.fessional.meepo", "error");
        Options opt = new OptionsBuilder()
                .include(PebbleBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
