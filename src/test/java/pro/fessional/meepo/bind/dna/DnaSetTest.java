package pro.fessional.meepo.bind.dna;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;

import java.util.regex.Pattern;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class DnaSetTest {

    DnaSet d1 = new DnaSet("// DNA:SET /false/{{user.male}}/", Life.nobodyOne(), new Clop(0, 32), new Clop(3, 32), Pattern.compile("false"), "{{user.male}}");
    DnaSet d2 = new DnaSet(" // DNA:SET /false/{{user.male}}/", Life.nobodyOne(), new Clop(1, 33), new Clop(4, 33), Pattern.compile("false"), "{{user.male}}");

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