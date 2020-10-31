package pro.fessional.meepo.sack;

import org.junit.Assert;
import org.junit.Test;
import pro.fessional.meepo.util.Seek;

/**
 * @author trydofor
 * @since 2020-10-17
 */
public class SeekTest {

    @Test
    public void seekToken() {
        {
            String str = " //  DNA:MEEPO  ";
            int p1 = str.indexOf("DNA:");
            int t1 = Seek.seekToken(str, 0, str.length(), "DNA:", true);
            Assert.assertEquals(p1, t1);
        }

        {
            String str = " /////  DNA:MEEPO  ";
            int p1 = str.lastIndexOf("//");
            int t1 = Seek.seekToken(str, 0, str.length(), "//", true);
            Assert.assertEquals(p1, t1);
        }
    }

    @Test
    public void trimBlank() {
        String txt = " 1234  56";
        int p2 = txt.indexOf('5');
        int[] ps = Seek.trimBlank(txt, 0, p2);
        Assert.assertEquals("1234", txt.substring(ps[0], ps[1]));
    }

    @Test
    public void seekGrace() {
        String txt = " 1234  |  56";
        int p1 = txt.indexOf('4');
        int p2 = txt.indexOf('|');
        int p3 = txt.indexOf('5');
        Assert.assertEquals(p1, Seek.seekPrevGrace(txt, 0, p2));
        Assert.assertEquals(p3, Seek.seekNextGrace(txt, p2 + 1, txt.length()));
        Assert.assertEquals(-1, Seek.seekPrevGrace("   \n", 0, 3));
        Assert.assertEquals(-1, Seek.seekNextGrace("   \n", 0, 3));
    }

    @Test
    public void seekWhite() {
        String txt = " 12|34  56";
        int p1 = txt.indexOf('1') - 1;
        int p2 = txt.indexOf('|');
        int p3 = txt.indexOf('4') + 1;
        Assert.assertEquals(p1, Seek.seekPrevWhite(txt, 0, p2));
        Assert.assertEquals(p3, Seek.seekNextWhite(txt, p2, txt.length()));
        Assert.assertEquals(-1, Seek.seekPrevWhite("txt\n", 0, 3));
        Assert.assertEquals(-1, Seek.seekNextWhite("txt\n", 0, 3));
    }

    @Test
    public void seekWords() {
        String txt = " 12 | 34  56";
        int p2 = txt.indexOf('|');
        int[] p1 = {txt.indexOf("1"), txt.indexOf("2") + 1};
        int[] p3 = {txt.indexOf("3"), txt.indexOf("4") + 1};
        Assert.assertArrayEquals(p1, Seek.seekPrevWords(txt, 0, p2));
        Assert.assertArrayEquals(p3, Seek.seekNextWords(txt, p2 + 1, txt.length()));

        Assert.assertArrayEquals(new int[]{0, 0}, Seek.seekPrevWords("  ", 0, 2));
        Assert.assertArrayEquals(new int[]{2, 2}, Seek.seekNextWords("  ", 1, 2));
        Assert.assertArrayEquals(new int[]{2, 2}, Seek.seekPrevWords(" \n ", 0, 3));
        Assert.assertArrayEquals(new int[]{1, 1}, Seek.seekNextWords(" \n ", 0, 3));
        Assert.assertArrayEquals(new int[]{2, 2}, Seek.seekNextWords("\n \n \n", 1, 4));
        Assert.assertArrayEquals(new int[]{3, 3}, Seek.seekPrevWords("\n \n \n", 1, 4));
    }
}