package pro.fessional.meepo.sack;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.util.Seek;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-10-17
 */
public class SeekTest extends TraceTest {

    @Test
    public void seekToken() {
        {
            String str = " //  DNA:MEEPO  ";
            int p1 = str.indexOf("DNA:");
            int t1 = Seek.seekToken(str, 0, str.length(), "DNA:", true);
            assertEquals(p1, t1);
        }

        {
            String str = " /////  DNA:MEEPO  ";
            int p1 = str.lastIndexOf("//");
            int t1 = Seek.seekToken(str, 0, str.length(), "//", true);
            assertEquals(p1, t1);
        }
    }

    @Test
    public void trimBlank() {
        String txt = " 1234  56";
        int p2 = txt.indexOf('5');
        int[] ps = Seek.trimBlank(txt, 0, p2);
        assertEquals("1234", txt.substring(ps[0], ps[1]));
    }

    @Test
    public void seekGrace() {
        String txt = " 1234  |  56";
        int p1 = txt.indexOf('4');
        int p2 = txt.indexOf('|');
        int p3 = txt.indexOf('5');
        assertEquals(p1, Seek.seekPrevGrace(txt, 0, p2));
        assertEquals(p3, Seek.seekNextGrace(txt, p2 + 1, txt.length()));
        assertEquals(-1, Seek.seekPrevGrace("   \n", 0, 3));
        assertEquals(-1, Seek.seekNextGrace("   \n", 0, 3));
    }

    @Test
    public void seekWhite() {
        String txt = " 12|34  56";
        int p1 = txt.indexOf('1') - 1;
        int p2 = txt.indexOf('|');
        int p3 = txt.indexOf('4') + 1;
        assertEquals(p1, Seek.seekPrevWhite(txt, 0, p2));
        assertEquals(p3, Seek.seekNextWhite(txt, p2, txt.length()));
        assertEquals(-1, Seek.seekPrevWhite("txt\n", 0, 3));
        assertEquals(-1, Seek.seekNextWhite("txt\n", 0, 3));
    }

    @Test
    public void seekWords() {
        String txt = " 12 | 34  56";
        int p2 = txt.indexOf('|');
        int[] p1 = {txt.indexOf("1"), txt.indexOf("2") + 1};
        int[] p3 = {txt.indexOf("3"), txt.indexOf("4") + 1};
        assertArrayEquals(p1, Seek.seekPrevWords(txt, 0, p2));
        assertArrayEquals(p3, Seek.seekNextWords(txt, p2 + 1, txt.length()));

        assertArrayEquals(new int[]{0, 0}, Seek.seekPrevWords("  ", 0, 2));
        assertArrayEquals(new int[]{2, 2}, Seek.seekNextWords("  ", 1, 2));
        assertArrayEquals(new int[]{2, 2}, Seek.seekPrevWords(" \n ", 0, 3));
        assertArrayEquals(new int[]{1, 1}, Seek.seekNextWords(" \n ", 0, 3));
        assertArrayEquals(new int[]{2, 2}, Seek.seekNextWords("\n \n \n", 1, 4));
        assertArrayEquals(new int[]{3, 3}, Seek.seekPrevWords("\n \n \n", 1, 4));
    }

    @Test
    public void countPreToken() {
        assertEquals(0, Seek.countPrevToken("/{", 1, ""));
        assertEquals(1, Seek.countPrevToken("/{", 1, "/"));
        assertEquals(3, Seek.countPrevToken("///{", 3, "/"));
        assertEquals(2, Seek.countPrevToken("//{", 2, "/"));
        assertEquals(4, Seek.countPrevToken("////{", 4, "/"));
        assertEquals(2, Seek.countPrevToken("////{", 4, "//"));
    }

    @Test
    public void seekPrevToken() {
        assertArrayEquals(new int[]{1, 1}, Seek.seekPrevToken("/{", 1, 2, "{", "/"));
        assertArrayEquals(new int[]{2, 2}, Seek.seekPrevToken("//{", 1, 3, "{", "/"));
        assertArrayEquals(new int[]{3, 3}, Seek.seekPrevToken("///{", 1, 4, "{", "/"));
        assertArrayEquals(new int[]{4, 4}, Seek.seekPrevToken("////{", 1, 5, "{", "/"));
    }

    @Test
    public void seekNextToken() {
        assertArrayEquals(new int[]{1, 1}, Seek.seekNextToken("/{", 0, 2, "{", "/"));
        assertArrayEquals(new int[]{2, 2}, Seek.seekNextToken("//{", 0, 3, "{", "/"));
        assertArrayEquals(new int[]{3, 3}, Seek.seekNextToken("///{", 0, 4, "{", "/"));
        assertArrayEquals(new int[]{4, 4}, Seek.seekNextToken("////{", 0, 5, "{", "/"));
    }
}