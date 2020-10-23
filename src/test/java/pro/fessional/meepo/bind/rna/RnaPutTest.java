package pro.fessional.meepo.bind.rna;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.bind.Clop;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class RnaPutTest {


    RnaPut d1 = new RnaPut("// DNA:PUT os/who/basename $(pwd)/", new Clop(0, 34), new Clop(3, 34), "os", "who", "basename $(pwd)", false);
    RnaPut d2 = new RnaPut(" // DNA:PUT os/who/basename $(pwd)/", new Clop(1, 35), new Clop(4, 35), "os", "who", "basename $(pwd)", false);

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