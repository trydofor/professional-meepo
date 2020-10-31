package pro.fessional.meepo.bind.dna;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.bind.wow.Clop;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class DnaBkbTest {

    DnaBkb d1 = new DnaBkb("// DNA:BKB 黑皇杖", new Clop(0,14), new Clop(3,14), "黑皇杖");
    DnaBkb d2 = new DnaBkb(" // DNA:BKB 黑皇杖", new Clop(1,15), new Clop(4,15),  "黑皇杖");

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