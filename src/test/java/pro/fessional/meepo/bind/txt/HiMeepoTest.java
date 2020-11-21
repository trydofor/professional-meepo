package pro.fessional.meepo.bind.txt;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class HiMeepoTest extends TraceTest {

    HiMeepo d1 = new HiMeepo("/* H!MEEPO */", new Clop(0, 13, 1, 1), "/*", "*/", true);
    HiMeepo d2 = new HiMeepo("/* H!MEEPO  */ ", new Clop(0, 14, 1, 1), "/*", "*/", true);

    @Test
    public void testEquals() {
        Assert.assertEquals(d1, d2);
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    public void testToString() {
        System.out.println(d1.toString());
        System.out.println(d2.toString());
    }
}