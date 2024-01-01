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

    @BeforeAll
    public static void setTrace() {
        String lvl = System.getProperty("meepo.log-level");
        if (lvl == null || lvl.isEmpty()) {
            lvl = "trace";
        }
        System.setProperty("org.slf4j.simpleLogger.log.pro.fessional.meepo", lvl);
    }
}
