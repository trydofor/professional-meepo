package pro.fessional.meepo.bind.txt;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class TxtPlainTest {
    TxtPlain d1 = new TxtPlain("0123456789", 1, 10, "test");
    TxtPlain d2 = new TxtPlain("123456789", 0, 9, "test2");

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