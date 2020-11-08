package pro.fessional.meepo;

import org.junit.BeforeClass;

/**
 * @author trydofor
 * @since 2020-11-09
 */
public class TraceTest {

    @BeforeClass
    public static void setTrace(){
        System.setProperty("org.slf4j.simpleLogger.log.pro.fessional.meepo","trace");
    }
}
