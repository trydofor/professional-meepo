package pro.fessional.meepo.bind.rna;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.bind.wow.Clop;
import pro.fessional.meepo.bind.wow.Life;

import java.util.regex.Pattern;

/**
 * @author trydofor
 * @since 2020-10-22
 */
public class RnaUseTest {

    RnaUse d1 = new RnaUse("// DNA:USE /meepo/who/1-3", Life.parse("1-3"), new Clop(0, 35), new Clop(3, 35), Pattern.compile("meepo"), "who");
    RnaUse d2 = new RnaUse(" // DNA:USE /meepo/who/1-3", Life.parse("1-3"), new Clop(1, 36), new Clop(4, 36), Pattern.compile("meepo"), "who");

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