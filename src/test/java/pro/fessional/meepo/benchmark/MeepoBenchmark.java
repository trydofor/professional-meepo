package pro.fessional.meepo.benchmark;

import com.mitchellbosecke.pebble.error.PebbleException;
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
import pro.fessional.meepo.sack.Gene;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author trydofor
 * @since 2020-10-28
 */
@Fork(5)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class MeepoBenchmark {

    private Gene template;
    private Map<String,Object> context;

    @Setup
    public void setup() throws PebbleException {
        context = Stock.mockContext();
        template = pro.fessional.meepo.Meepo.parse("classpath:/template/jmh/stocks.meepo.html");
    }

    @Benchmark
    public String benchmark() throws PebbleException {
        return template.merge(context);
    }

    public static void main(String[] args) throws RunnerException {
        System.setProperty("org.slf4j.simpleLogger.log.pro.fessional.meepo", "error");
        Options opt = new OptionsBuilder()
                .include(MeepoBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
