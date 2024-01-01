package pro.fessional.meepo.eval.time;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author trydofor
 * @since 2021-01-07
 */
class NowTest {

    @Test
    void now() {
        final String s1 = Now.now(new Date(), null);
        final String s2 = Now.now(LocalDateTime.now(), null);
        final String s3 = Now.now(null, null);
        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s3);
    }
}
