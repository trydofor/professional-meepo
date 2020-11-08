package pro.fessional.meepo.util;

import org.junit.Assert;
import org.junit.Test;

import java.io.CharArrayWriter;
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
        CharArrayWriter buf = new CharArrayWriter();
        Dent.left(buf, 0);
        Assert.assertEquals(0, buf.size());

        buf.reset();
        Dent.left(buf, -1);
        Assert.assertEquals(0, buf.size());

        buf.reset();
        Dent.left(buf, 64);
        Assert.assertEquals(64, buf.size());

        buf.reset();
        Dent.left(buf, 100);
        Assert.assertEquals(100, buf.size());

        buf.reset();
        Dent.left(buf, 200);
        Assert.assertEquals(200, buf.size());
    }

    @Test
    public void left3() {
        CharArrayWriter buf = new CharArrayWriter();
        Dent.left(buf, 0, new int[]{1, 2, 3});
        Assert.assertEquals("123", buf.toString());

        buf.reset();
        Dent.left(buf, 1, new int[]{1, 2, 3});
        Assert.assertEquals("1 2 3", buf.toString());

        buf.reset();
        Dent.left(buf, 1, new String[]{"1\n", "2\n", "3\n"});
        Assert.assertEquals("1\n 2\n 3\n", buf.toString());

        buf.reset();
        Dent.left(buf, 1, Arrays.asList("1\n", "2\n", "3\n"));
        Assert.assertEquals("1\n 2\n 3\n", buf.toString());

        buf.reset();
        Dent.left(buf, 1, "123");
        Assert.assertEquals("123", buf.toString());
    }
}