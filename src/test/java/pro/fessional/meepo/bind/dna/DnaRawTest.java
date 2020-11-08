package pro.fessional.meepo.bind.dna;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class DnaRawTest extends TraceTest {

    DnaRaw d1 = new DnaRaw("// DNA:RAW SUPER(1010100", new Clop(0, 24), 11, 24);
    DnaRaw d2 = new DnaRaw(" // DNA:RAW SUPER(1010100", new Clop(1, 25), 12, 25);

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