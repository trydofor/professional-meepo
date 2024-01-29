package pro.fessional.meepo.bind.wow;

import org.junit.jupiter.api.Test;
import pro.fessional.meepo.TraceTest;
import pro.fessional.meepo.bind.Const;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-10-16
 */
public class LifeTest extends TraceTest {

    @Test
    public void testPoint() {
        // 1,3,5-9
        Life l1 = Life.parse("1,5-7,6-8,5-8,7-9,6-9,5-9,7,7,8,3");
        for (int i = 0; i < 10; i++) {
            assertEquals(Const.TXT$EMPTY, l1.name);
            assertEquals(Life.State.Live, l1.enjoy());
            assertEquals(1, l1.count());
            assertEquals(Life.State.Skip, l1.enjoy());
            assertEquals(2, l1.count());
            assertEquals(Life.State.Live, l1.enjoy());
            assertEquals(3, l1.count());
            assertEquals(Life.State.Skip, l1.enjoy());
            assertEquals(4, l1.count());
            assertEquals(Life.State.Live, l1.enjoy());
            assertEquals(5, l1.count());
            assertEquals(Life.State.Live, l1.enjoy());
            assertEquals(6, l1.count());
            assertEquals(Life.State.Live, l1.enjoy());
            assertEquals(7, l1.count());
            assertEquals(Life.State.Live, l1.enjoy());
            assertEquals(8, l1.count());
            assertEquals(Life.State.Live, l1.enjoy());
            assertEquals(9, l1.count());
            assertEquals(Life.State.Dead, l1.enjoy());
            assertEquals(10, l1.count());
            l1.reset();
        }
    }

    @Test
    public void testOne() {
        Life l1 = Life.parse("");
        for (int i = 0; i < 10; i++) {
            assertEquals(Const.TXT$EMPTY, l1.name);
            assertEquals(Life.State.Live, l1.enjoy());
            assertEquals(1, l1.count());
            assertEquals(Life.State.Dead, l1.enjoy());
            assertEquals(2, l1.count());
            l1.reset();
        }

        Life l2 = Life.parse("1");
        assertEquals(Const.TXT$EMPTY, l2.name);
        assertEquals(Life.State.Live, l2.enjoy());
        assertEquals(1, l2.count());
        assertEquals(Life.State.Dead, l2.enjoy());
        assertEquals(2, l2.count());
    }

    @Test
    public void testAny() {
        Life l1 = Life.parse("*");
        assertEquals(Const.TXT$EMPTY, l1.name);
        for (int i = 1; i <= 256; i++) {
            assertEquals(Life.State.Live, l1.enjoy());
            assertEquals(i, l1.count());
        }
        l1.reset();
        for (int i = 1; i <= 256; i++) {
            assertEquals(Life.State.Live, l1.enjoy());
            assertEquals(i, l1.count());
        }

        Life l2 = Life.parse("id");
        assertEquals("id", l2.name);
        for (int i = 1; i <= 256; i++) {
            assertEquals(Life.State.Live, l2.enjoy());
            assertEquals(i, l2.count());
        }
    }

    @Test
    public void testOther() {
        equalsLive(Life.parse("1"), Life.parse("   1   "));
        equalsLive(Life.parse("1"), Life.parse("   1,   "));
        equalsLive(Life.parse("1,3"), Life.parse("   1,3   "));
        equalsLive(Life.parse("1,3"), Life.parse("   1,3,   "));
        equalsLive(Life.parse("1-3"), Life.parse("   1-3,   "));
    }

    void equalsLive(Life l1, Life l2){
        assertEquals(l1.toString(), l2.toString());
    }
}