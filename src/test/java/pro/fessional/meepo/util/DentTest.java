package pro.fessional.meepo.util;

import org.junit.jupiter.api.Test;

import java.io.CharArrayWriter;
import java.io.StringWriter;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-11-01
 */
public class DentTest {

    @Test
    public void left1() {
        assertEquals(2, Seek.indent(" \n  1234", 4));
        assertEquals(2, Seek.indent("\n  1234", 3));
        assertEquals(2, Seek.indent("  1234", 2));
        assertEquals(1, Seek.indent("  1234", 1));
        assertEquals(0, Seek.indent("  1234", 0));
        assertEquals(0, Seek.indent("  1234", -1));
        assertEquals(0, Seek.indent("1234", 0));
        assertEquals(0, Seek.indent("1234", 1));
    }

    @Test
    public void left2() {
        StringWriter out = new StringWriter();
        StringBuffer buf = out.getBuffer();
        Dent.indent(out, 0);
        assertEquals(0, buf.length());

        buf.setLength(0);
        Dent.indent(out, -1);
        assertEquals(0, buf.length());

        buf.setLength(0);
        Dent.indent(out, 64);
        assertEquals(64, buf.length());

        buf.setLength(0);
        Dent.indent(out, 100);
        assertEquals(100, buf.length());

        buf.setLength(0);
        Dent.indent(out, 200);
        assertEquals(200, buf.length());
    }

    @Test
    public void left3() {
        CharArrayWriter buf = new CharArrayWriter();
        Dent.indent(buf, 0, new int[]{1, 2, 3});
        assertEquals("123", buf.toString());

        buf.reset();
        Dent.indent(buf, 1, new char[]{'1', '2', '3'});
        assertEquals("1 2 3", buf.toString());

        buf.reset();
        Dent.indent(buf, 1, new String[]{"1\n", "2\n", "3\n"});
        assertEquals("1\n 2\n 3\n", buf.toString());

        buf.reset();
        Dent.indent(buf, 1, Arrays.asList("1\n", "2\n", "3\n"));
        assertEquals("1\n 2\n 3\n", buf.toString());

        buf.reset();
        Dent.indent(buf, 1, "123");
        assertEquals("123", buf.toString());
    }

    @Test
    public void left4() {
        StringWriter out = new StringWriter();
        StringBuffer buf = out.getBuffer();

        Dent.indent(out, 0, new int[]{1, 2, 3});
        assertEquals("123", out.toString());

        buf.setLength(0);
        Dent.indent(out, 1, new char[]{'1', '2', '3'});
        assertEquals("1 2 3", out.toString());

        buf.setLength(0);
        Dent.indent(out, 1, new String[]{"1\n", "2\n", "3\n"});
        assertEquals("1\n 2\n 3\n", out.toString());

        buf.setLength(0);
        Dent.indent(out, 1, Arrays.asList("1\n", "2\n", "3\n"));
        assertEquals("1\n 2\n 3\n", out.toString());

        buf.setLength(0);
        Dent.indent(out, 1, "123");
        assertEquals("123", out.toString());
    }
}