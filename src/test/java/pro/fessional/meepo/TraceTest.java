package pro.fessional.meepo;

import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author trydofor
 * @since 2020-11-09
 */
public class TraceTest {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String TestVerbose = "TEST_VERBOSE";

    private static volatile Boolean NotTestVerbose = null;

    @BeforeAll
    public static void setMeepoLogLevel() {
        if (NotTestVerbose == null) {
            NotTestVerbose = "false".equalsIgnoreCase(System.getenv(TestVerbose));
            System.setProperty("org.slf4j.simpleLogger.log.pro.fessional.meepo", NotTestVerbose ? "ERROR" : "DEBUG");
        }
    }
}
