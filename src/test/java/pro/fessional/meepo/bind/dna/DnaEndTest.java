package pro.fessional.meepo.bind.dna;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.bind.Clop;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class DnaEndTest {

    DnaEnd d1 = new DnaEnd("// DNA:END 黑皇杖,id", new Clop(0, 17), new Clop(3, 17), "黑皇杖", "id");
    DnaEnd d2 = new DnaEnd(" // DNA:END 黑皇杖,id", new Clop(1, 18), new Clop(4, 18), "id", "黑皇杖");

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