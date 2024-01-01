package pro.fessional.meepo.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.sack.Parser;

/**
 * @author trydofor
 * @since 2022-09-15
 */
public class ParseTest {
    protected static final Logger logger = LoggerFactory.getLogger(ParseTest.class);
    @Test
    public void testCross() {
        String tmpl = "// HI-MEEPO\n"
                      + "// RNA:WHEN /yes/GitHash/bg\n"
                      + "//   RNA:USE /hash/GitHash/\n"
                      + "// RNA:ELSE bg\n"
                      + "//   RNA:USE /hash/ModTime/\n"
                      + "// RNA:DONE bg\n"
                      + "project hash";

        try {
            Parser.parse(tmpl);
            Assertions.fail();
        }
        catch (Exception e) {
            logger.info("test exception", e);
        }
    }
}
