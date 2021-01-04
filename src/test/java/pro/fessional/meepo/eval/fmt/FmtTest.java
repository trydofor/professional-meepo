package pro.fessional.meepo.eval.fmt;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2021-01-04
 */
class FmtTest {

    private final Map<String, Object> ctx = Collections.emptyMap();

    @Test
    public void testFmt() {
        assertEquals("1,000", Fmt.funFmt.eval(ctx, 1000, "%,d"));
    }
}