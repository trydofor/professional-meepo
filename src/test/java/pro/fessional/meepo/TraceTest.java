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

    public static final String MeepoLogLevel = "meepo.log-level";
    public static final String TestVerbose = "test-verbose";

    private static volatile boolean done = false;

    @BeforeAll
    public static void setMeepoLogLevel() {
        if (done) return;

        String lvl = System.getProperty(MeepoLogLevel);
        if (lvl == null || lvl.isEmpty()) {
            if ("false".equalsIgnoreCase(System.getProperty(TestVerbose))) {
                lvl = "ERROR";
            }
            else {
                lvl = "TRACE";
            }
        }
        System.setProperty("org.slf4j.simpleLogger.log.pro.fessional.meepo", lvl);

        done = true;
    }
}
