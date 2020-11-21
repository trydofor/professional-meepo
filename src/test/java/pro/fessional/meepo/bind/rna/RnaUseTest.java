package pro.fessional.meepo.bind.rna;

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
public class RnaUseTest extends TraceTest {

    RnaUse d1 = new RnaUse("// DNA:USE /meepo/who/1-3", new Clop(0, 25,1,1), Life.parse("1-3"), Pattern.compile("meepo"), "who");
    RnaUse d2 = new RnaUse(" // DNA:USE /meepo/who/1-3", new Clop(1, 26,1,1), Life.parse("1-3"), Pattern.compile("meepo"), "who");

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