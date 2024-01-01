package pro.fessional.meepo.eval.time;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author trydofor
 * @since 2021-01-07
 */
class NowTest extends TraceTest {

    @Test
    void now() {
        final String s1 = Now.now(new Date(), null);
        final String s2 = Now.now(LocalDateTime.now(), null);
        final String s3 = Now.now(null, null);
        logger.debug("s1={}", s1);
        logger.debug("s2={}", s2);
        logger.debug("s2={}", s3);
    }
}
