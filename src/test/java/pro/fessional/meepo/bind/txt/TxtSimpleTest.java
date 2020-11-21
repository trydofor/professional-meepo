package pro.fessional.meepo.bind.txt;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class TxtSimpleTest extends TraceTest {
    TxtSimple d1 = new TxtSimple("0123456789", new Clop(1, 10, 1, 1));
    TxtSimple d2 = new TxtSimple("123456789", new Clop(0, 9, 1, 1));

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