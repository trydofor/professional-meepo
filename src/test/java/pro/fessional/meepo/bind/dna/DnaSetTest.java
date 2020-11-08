package pro.fessional.meepo.bind.dna;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;

import java.util.regex.Pattern;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class DnaSetTest extends TraceTest {

    DnaSet d1 = new DnaSet("// DNA:SET /false/{{user.male}}/", new Clop(0, 32), Life.nobodyOne(), Pattern.compile("false"), "{{user.male}}");
    DnaSet d2 = new DnaSet(" // DNA:SET /false/{{user.male}}/", new Clop(1, 33), Life.nobodyOne(), Pattern.compile("false"), "{{user.male}}");

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