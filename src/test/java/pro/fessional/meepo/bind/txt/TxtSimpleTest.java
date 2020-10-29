package pro.fessional.meepo.bind.txt;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class TxtSimpleTest {
    TxtSimple d1 = new TxtSimple("0123456789", 1, 10);
    TxtSimple d2 = new TxtSimple("123456789", 0, 9);

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