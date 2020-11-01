package pro.fessional.meepo.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author trydofor
 * @since 2020-11-01
 */
public class DentTest {

    @Test
    public void left1() {
        Assert.assertEquals(2, Dent.left(" \n  1234", 4));
        Assert.assertEquals(2, Dent.left("\n  1234", 3));
        Assert.assertEquals(2, Dent.left("  1234", 2));
        Assert.assertEquals(1, Dent.left("  1234", 1));
        Assert.assertEquals(0, Dent.left("  1234", 0));
        Assert.assertEquals(0, Dent.left("  1234", -1));
        Assert.assertEquals(0, Dent.left("1234", 0));
        Assert.assertEquals(0, Dent.left("1234", 1));
    }

    @Test
    public void left2() {
        StringBuilder buf = new StringBuilder();
        Dent.left(buf, 0);
        Assert.assertEquals(0, buf.length());
        buf.setLength(0);
        Dent.left(buf, -1);
        Assert.assertEquals(0, buf.length());
        buf.setLength(0);
        Dent.left(buf, 64);
        Assert.assertEquals(64, buf.length());
        buf.setLength(0);
        Dent.left(buf, 100);
        Assert.assertEquals(100, buf.length());
        buf.setLength(0);
        Dent.left(buf, 200);
        Assert.assertEquals(200, buf.length());
    }

    @Test
    public void left3() {
        StringBuilder buf = new StringBuilder();
        Dent.left(buf, 0, new int[]{1, 2, 3});
        Assert.assertEquals("123", buf.toString());

        buf.setLength(0);
        Dent.left(buf, 1, new int[]{1, 2, 3});
        Assert.assertEquals("1 2 3", buf.toString());

        buf.setLength(0);
        Dent.left(buf, 1, new String[]{"1\n", "2\n", "3\n"});
        Assert.assertEquals("1\n 2\n 3\n", buf.toString());

        buf.setLength(0);
        Dent.left(buf, 1, Arrays.asList("1\n", "2\n", "3\n"));
        Assert.assertEquals("1\n 2\n 3\n", buf.toString());
        buf.setLength(0);
        Dent.left(buf, 1, "123");
        Assert.assertEquals("123", buf.toString());
    }
}